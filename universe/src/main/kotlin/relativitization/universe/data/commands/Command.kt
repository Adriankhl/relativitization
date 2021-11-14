package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.physics.Int4D
import relativitization.universe.data.events.MoveToDouble3DEvent
import relativitization.universe.data.events.name
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.RealString
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.KClass

@Serializable
sealed class Command {
    abstract val toId: Int
    abstract val fromId: Int
    abstract val fromInt4D: Int4D

    /**
     * Description of the command
     */
    abstract val description: I18NString


    /**
     * Check to see if fromId match
     */
    private fun checkFromId(playerData: MutablePlayerData): Boolean {
        return if (playerData.playerId == fromId) {
            true
        } else {
            val className = this::class.qualifiedName
            logger.error("${className}: player id not equal to command from id")
            false
        }
    }

    /**
     * Check to see if toId match
     */
    private fun checkToId(playerData: MutablePlayerData): Boolean {
        return if (playerData.playerId == toId) {
            true
        } else {
            val className = this::class.qualifiedName
            logger.error("${className}: player id not equal to command target id")
            false
        }
    }


    /**
     * Check if the player (sender) can send the command
     */
    protected abstract fun canSend(playerData: MutablePlayerData, universeSettings: UniverseSettings): CanSendCheckMessage

    /**
     * Check if can send and have command
     *
     * @param playerData the player data to send this command
     */
    fun canSendFromPlayer(playerData: MutablePlayerData, universeSettings: UniverseSettings): CanSendCheckMessage {
        val hasCommand: Boolean = CommandCollection.hasCommand(universeSettings, this)
        val hasCommandI18NString: I18NString = if (hasCommand) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("No such command: "),
                    IntString(0),
                    RealString(". ")
                ),
                listOf(
                    this.toString()
                ),
            )
        }

        val canSendMessage: CanSendCheckMessage =  canSend(playerData, universeSettings)

        val isFromInt4DValid: Boolean = playerData.int4D.toInt4D() == fromInt4D
        val isFromInt4DValidI18NString: I18NString = if (isFromInt4DValid) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Player coordinate "),
                    IntString(0),
                    RealString(" is not the same as the coordinate "),
                    IntString(1),
                    RealString(" in this command. ")
                ),
                listOf(
                    playerData.int4D.toInt4D().toString(),
                    fromInt4D.toString(),
                ),
            )
        }

        val isFromIdValid: Boolean = checkFromId(playerData)
        val isFromIdValidI18NString: I18NString = if (isFromIdValid) {
            I18NString("")
        } else {
            I18NString(
                listOf(
                    RealString("Player id "),
                    IntString(0),
                    RealString(" is not the same as the id "),
                    IntString(1),
                    RealString(" in this command. ")
                ),
                listOf(
                    playerData.playerId.toString(),
                    fromId.toString(),
                ),
            )
        }



        if (!hasCommand || !(canSendMessage.canSend) || !isFromInt4DValid || !isFromIdValid) {
            val className = this::class.qualifiedName
            logger.error("${className}: cannot send command")
        }

        return CanSendCheckMessage(
            hasCommand && canSendMessage.canSend && isFromInt4DValid && isFromIdValid,
            I18NString.combine(listOf(
                hasCommandI18NString,
                canSendMessage.message,
                isFromInt4DValidI18NString,
                isFromIdValidI18NString
            ))
        )
    }

    /**
     * Execute on self in order to end this command
     */
    protected open fun selfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ) {}

    /**
     * Check and self execute
     */
    fun checkAndSelfExecuteBeforeSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        return if (canSendFromPlayer(playerData, universeSettings).canSend) {
            try {
                selfExecuteBeforeSend(playerData, universeSettings)
                CanSendCheckMessage(true)
            } catch (e: Throwable) {
                logger.error("checkAndSelfExecuteBeforeSend fail, throwable $e")
                throw e
            }
        } else {
            val className = this::class.qualifiedName
            logger.info("$className cannot be sent by $fromId")
            val reasonI18NString = I18NString("Reason: ")
            CanSendCheckMessage(
                false,
                I18NString.combine(listOf(
                    reasonI18NString,
                    canSendFromPlayer(playerData, universeSettings).message
                ))
            )
        }
    }



    /**
     * Check if the player can receive the command
     */
    protected abstract fun canExecute(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean

    /**
     * Check if can execute and have command
     *
     * @param playerData the command execute on this player
     * @param universeSettings universe setting, e.g., have
     */
    fun canExecuteOnPlayer(playerData: MutablePlayerData, universeSettings: UniverseSettings): Boolean {
        val hasCommand: Boolean = CommandCollection.hasCommand(universeSettings, this)
        val correctId: Boolean = checkToId(playerData)
        val canExecute: Boolean = canExecute(playerData, universeSettings)
        return hasCommand && correctId && canExecute
    }


    /**
     * Execute on playerData, for AI/human planning and action
     */
    protected abstract fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings)


    /**
     * Check and execute
     */
    fun checkAndExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        return if (canExecuteOnPlayer(playerData, universeSettings)) {
            try {
                execute(playerData, universeSettings)
                true
            } catch (e: Throwable) {
                logger.error("checkAndExecute fail, throwable $e")
                throw e
            }
        } else {
            val className = this::class.qualifiedName
            logger.info("$className cannot be executed on $toId")
            false
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

fun Command.name(): String = this::class.simpleName.toString()

fun <T : Command> KClass<T>.name(): String = this.simpleName.toString()

abstract class CommandAvailability {
    abstract val commandList: List<String>

    // Allowed event list for AddEventCommand
    abstract val addEventList: List<String>
}

object DefaultCommandAvailability : CommandAvailability() {
    override val commandList: List<String> = listOf(
        DamageCommand::class.name(),
        DummyCommand::class.name(),
        CannotSendCommand::class.name(),
        AddEventCommand::class.name(),
        SelectEventChoiceCommand::class.name(),
        BuildForeignFuelFactoryCommand::class.name(),
        BuildForeignResourceFactoryCommand::class.name(),
        BuildLocalFuelFactoryCommand::class.name(),
        BuildLocalResourceFactoryCommand::class.name(),
        AddDirectSubordinateCommand::class.name(),
        SendFuelFromStorageCommand::class.name(),
        SendResourceFromStorageCommand::class.name(),
        SendFuelCommand::class.name(),
        SendResourceCommand::class.name(),
        SendResourceToPopCommand::class.name(),
        PopBuyResourceCommand::class.name(),
        PlayerBuyResourceCommand::class.name(),
        DisableFuelIncreaseCommand::class.name(),
        ChangeVelocityCommand::class.name(),
    )

    override val addEventList: List<String> = listOf(
        MoveToDouble3DEvent::class.name(),
    )
}

fun CommandAvailability.name(): String = this::class.simpleName.toString()

object CommandCollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val commandAvailabilityList: List<CommandAvailability> = listOf(
        DefaultCommandAvailability
    )

    val commandAvailabilityNameMap: Map<String, CommandAvailability> = commandAvailabilityList.map {
        it.name() to it
    }.toMap()

    fun hasCommand(universeSettings: UniverseSettings, command: Command): Boolean {
        return if (universeSettings.commandCollectionName != "All") {
            val commandCollection: List<String> = commandAvailabilityNameMap.getOrElse(
                universeSettings.commandCollectionName
            ) {
                logger.error("No command collection name: ${universeSettings.commandCollectionName} found")
                DefaultCommandAvailability
            }.commandList

            commandCollection.contains(command.name())
        } else {
            true
        }
    }
}

@Serializable
data class CanSendCheckMessage(
    val canSend: Boolean,
    val message: I18NString = I18NString(listOf(), listOf())
)

object CanSendWIthMessageI18NStringFactory {
    fun isNotSubordinate(playerId: Int, toId: Int): I18NString = I18NString(
        listOf(
            RealString("Player "),
            IntString(0),
            RealString(" not a subordinate of player "),
            IntString(1),
            RealString(".")
        ),
        listOf(
            toId.toString(),
            playerId.toString(),
        )
    )

    fun isNotToSelf(playerId: Int, toId: Int): I18NString = I18NString(
        listOf(
            RealString("Player id "),
            IntString(0),
            RealString(" the same as toId "),
            IntString(1),
            RealString(".")
        ),
        listOf(
            playerId.toString(),
            toId.toString(),
        )
    )

    fun isTopLeaderIdWrong(playerTopLeaderId: Int, topLeaderId: Int): I18NString = I18NString(
        listOf(
            RealString("Player top leader id "),
            IntString(0),
            RealString("the same as "),
            IntString(1),
            RealString(".")
        ),
        listOf(
            playerTopLeaderId.toString(),
            topLeaderId.toString()
        )
    )
}