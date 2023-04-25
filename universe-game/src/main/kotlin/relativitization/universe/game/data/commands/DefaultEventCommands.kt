package relativitization.universe.game.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.events.Event
import relativitization.universe.game.data.events.MutableEventData
import relativitization.universe.game.maths.physics.Int4D
import relativitization.universe.game.utils.I18NString
import relativitization.universe.game.utils.IntString
import relativitization.universe.game.utils.NormalString
import relativitization.universe.game.utils.RelativitizationLogManager

/**
 * Add event to player
 */
@Serializable
data class AddEventCommand(
    val event: Event,
) : DefaultCommand() {
    override val toId: Int = event.toId

    override fun name(): String = "Add Event"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Add event ("),
            IntString(0),
            NormalString(") to player "),
            IntString(1),
            NormalString(" from player "),
            IntString(2),
            NormalString(". Event description: ")
        ),
        listOf(
            event.name(),
            toId.toString(),
            fromId.toString(),
        ),
        event.description(fromId)
    )

    /**
     * Whether this player can send the event depends on the event
     */
    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val canAdd = CommandErrorMessage(
            canAddEvent(event, universeSettings),
            I18NString("Cannot add this event by command. ")
        )

        val canSendEvent: CommandErrorMessage = event.canSend(
            playerData = playerData,
            universeSettings = universeSettings
        )


        return CommandErrorMessage(
            listOf(
                canAdd,
                canSendEvent
            )
        )
    }

    /**
     * Whether the event can be added to the player depends on the event
     */
    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {

        val canAdd = CommandErrorMessage(
            canAddEvent(event, universeSettings),
            I18NString("Cannot add this event by command. ")
        )

        val canExecuteEvent = event.canExecute(
            playerData = playerData,
            fromId = fromId,
            universeSettings = universeSettings
        )

        return CommandErrorMessage(
            listOf(
                canAdd,
                canExecuteEvent,
            )
        )
    }


    /**
     * Add event data to player data
     */
    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val eventData = MutableEventData(event, fromId)
        playerData.playerInternalData.addEventData(eventData)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        /**
         * Whether player can add this event to other player, used by AddEventCommand
         */
        fun canAddEvent(event: Event, universeSettings: UniverseSettings): Boolean {
            val commandAvailability: CommandAvailability = CommandCollection.commandAvailabilityNameMap.getOrElse(
                    universeSettings.commandCollectionName
                ) {
                    logger.error("No add event command collection name: " +
                            "${universeSettings.commandCollectionName} found")
                    DefaultCommandAvailability
                }

            return if (commandAvailability.name() != AllCommandAvailability.name()) {
                commandAvailability.canAddEvent(event)
            } else {
                true
            }
        }
    }
}

/**
 * Select event choice, can only apply to player himself
 *
 * @property eventKey the index of the event in eventDataList
 * @property choice the player choice on the event
 */
@Serializable
data class SelectEventChoiceCommand(
    override val toId: Int,
    val eventKey: Int,
    val choice: Int,
) : DefaultCommand() {
    override fun name(): String = "Select Event Choice"

    override fun description(fromId: Int): I18NString = I18NString(
        listOf(
            NormalString("Select choice "),
            IntString(0),
            NormalString(" for event "),
            IntString(1),
            NormalString(" ("),
            IntString(2),
            NormalString(")")
        ),
        listOf(
            choice.toString(),
            eventKey.toString(),
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(playerData.playerId, toId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == fromId,
            CommandI18NStringFactory.isNotFromSelf(playerData.playerId, fromId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
            )
        )
    }

    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        val eventDataMap: MutableMap<Int, MutableEventData> =
            playerData.playerInternalData.eventDataMap

        // Check if eventIndex is in range
        if (eventDataMap.containsKey(eventKey)) {
            val eventData: MutableEventData = eventDataMap.getValue(eventKey)
            eventData.eventRecordData.hasChoice = true
            eventData.eventRecordData.choice = choice
        } else {
            logger.error("Can't select event choice, index out of range")
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }

}