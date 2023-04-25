package relativitization.universe.game.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.PlayerType
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.data.commands.CommandI18NStringFactory
import relativitization.universe.game.data.commands.DeclareIndependenceToDirectLeaderCommand
import relativitization.universe.game.data.components.politicsData
import relativitization.universe.core.utils.I18NString
import relativitization.universe.core.utils.IntString
import relativitization.universe.core.utils.NormalString
import kotlin.random.Random

/**
 * Ask to merge this player to its direct leader
 */
@Serializable
data class AskToMergeCarrierEvent(
    override val toId: Int,
) : DefaultEvent() {
    override fun name(): String = "Ask To Merge Carrier"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Give all carriers of  "),
            IntString(0),
            NormalString(" to direct leader. ")
        ),
        listOf(
            toId.toString()
        )
    )

    override fun choiceDescription(fromId: Int): Map<Int, I18NString> = mapOf(
        0 to I18NString("Accept and die"),
        1 to I18NString("Reject and declare war")
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isDirectSubordinate = CommandErrorMessage(
            playerData.playerInternalData.directSubordinateIdSet.contains(toId),
            CommandI18NStringFactory.isNotDirectSubordinate(
                playerId = playerData.playerId, otherPlayerId = toId
            )
        )

        return CommandErrorMessage(
            listOf(
                isDirectSubordinate
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isEventUnique = CommandErrorMessage(
            !playerData.playerInternalData.eventDataMap.values.any {
                it.event is MoveToDouble3DEvent
            },
            I18NString("Event already exists. ")
        )

        val isDirectLeader = CommandErrorMessage(
            playerData.playerInternalData.directLeaderId == fromId,
            CommandI18NStringFactory.isNotDirectLeader(playerData.playerId, fromId),
        )

        val isNotTopLeader = CommandErrorMessage(
            !playerData.isTopLeader(),
            CommandI18NStringFactory.isTopLeader(playerData.playerId)
        )

        return CommandErrorMessage(
            listOf(
                isEventUnique,
                isDirectLeader,
                isNotTopLeader,
            )
        )
    }

    override fun shouldCancel(
        mutablePlayerData: MutablePlayerData,
        fromId: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Boolean {
        // Only cancel this event if the player agree to merge
        val hasAgreedMerge: Boolean = universeData3DAtPlayer.getCurrentPlayerData()
            .playerInternalData.politicsData().hasAgreedMerge

        val isNotDirectLeader: Boolean =
            mutablePlayerData.playerInternalData.directLeaderId != fromId

        return hasAgreedMerge || isNotDirectLeader
    }

    override fun choiceAction(
        mutablePlayerData: MutablePlayerData,
        fromId: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ): Map<Int, () -> List<Command>> = mapOf(
        0 to {
            mutablePlayerData.playerInternalData.politicsData().hasAgreedMerge = true
            listOf()
        },
        1 to {
            // Declare war if reject
            val declareIndependenceToDirectLeaderCommand = DeclareIndependenceToDirectLeaderCommand(
                toId = fromId,
            )

            val message: CommandErrorMessage = declareIndependenceToDirectLeaderCommand
                .checkAndSelfExecuteBeforeSend(
                    mutablePlayerData,
                    universeSettings,
                )

            if (message.success) {
                listOf(
                    declareIndependenceToDirectLeaderCommand
                )
            } else {
                listOf()
            }
        }
    )

    override fun defaultChoice(
        mutablePlayerData: MutablePlayerData,
        fromId: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        random: Random,
    ): Int {
        return when (universeData3DAtPlayer.getCurrentPlayerData().playerType) {
            PlayerType.HUMAN -> 1
            PlayerType.NONE -> 0
            PlayerType.AI -> random.nextInt(0, 2)
        }
    }
}