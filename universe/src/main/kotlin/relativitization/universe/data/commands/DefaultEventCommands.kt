package relativitization.universe.data.commands

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.data.events.Event
import relativitization.universe.data.events.MutableEventData
import relativitization.universe.data.events.name
import relativitization.universe.utils.I18NString
import relativitization.universe.utils.IntString
import relativitization.universe.utils.NormalString
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Add event to player
 */
@Serializable
data class AddEventCommand(
    val event: Event,
    override val fromInt4D: Int4D,
) : DefaultCommand() {
    override val toId: Int = event.toId
    override val fromId: Int = event.fromId

    override fun description(): I18NString = I18NString(
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
        event.description()
    )

    /**
     * Whether this player can send the event depends on the event
     */
    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {

        val isIdValid = CommandErrorMessage(
            isEventPlayerIdValid(),
            I18NString("Event player id is not valid. ")
        )

        val canAdd = CommandErrorMessage(
            canAddEvent(universeSettings, event),
            I18NString("Cannot add this event by command. ")
        )

        val canSendEvent: CommandErrorMessage = event.canSend(playerData, universeSettings)


        return CommandErrorMessage(
            listOf(
                isIdValid,
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
        universeSettings: UniverseSettings
    ): CommandErrorMessage {

        val canAdd = CommandErrorMessage(
            canAddEvent(universeSettings, event),
            I18NString("Cannot add this event by command. ")
        )

        val canExecuteEvent = event.canExecute(playerData, universeSettings)

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
    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val eventData: MutableEventData = MutableEventData(event)
        playerData.playerInternalData.addEventData(eventData)
    }

    /**
     * Check whether the fromId and toId in the event is equal to those in the command
     */
    private fun isEventPlayerIdValid(): Boolean = (fromId == event.fromId) && (toId == event.toId)

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        /**
         * Whether player can add this event to other player, used by AddEventCommand
         */
        fun canAddEvent(universeSettings: UniverseSettings, event: Event): Boolean {
            val commandAvailability: CommandAvailability = CommandCollection.commandAvailabilityNameMap.getOrElse(
                universeSettings.commandCollectionName
            ) {
                logger.error("No add event command collection name: ${universeSettings.commandCollectionName} found")
                DefaultCommandAvailability
            }

            return if (commandAvailability.name() != AllCommandAvailability.name()) {
                val addEventList: List<String> = commandAvailability.addEventList
                addEventList.contains(event.name())
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
 * @property eventName name of the event, for ensuring the command acts on the correct event
 * @property choice the player choice on the event
 */
@Serializable
data class SelectEventChoiceCommand(
    override val toId: Int,
    override val fromId: Int,
    override val fromInt4D: Int4D,
    val eventKey: Int,
    val eventName: String,
    val choice: Int,
) : DefaultCommand() {

    override fun description(): I18NString = I18NString(
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
            eventName,
        ),
    )

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CommandErrorMessage {
        val isSelf = CommandErrorMessage(
            playerData.playerId == toId,
            CommandI18NStringFactory.isNotToSelf(fromId, toId)
        )

        return CommandErrorMessage(
            listOf(
                isSelf
            )
        )
    }

    override fun canExecute(
        playerData: MutablePlayerData,
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

    override fun execute(playerData: MutablePlayerData, universeSettings: UniverseSettings) {
        val eventDataMap: MutableMap<Int, MutableEventData> =
            playerData.playerInternalData.eventDataMap

        // Check if eventIndex is in range
        if (eventDataMap.containsKey(eventKey)) {
            val eventData: MutableEventData = eventDataMap.getValue(eventKey)
            if (eventData.event.name() == eventName) {
                eventData.eventRecordData.hasChoice = true
                eventData.eventRecordData.choice = choice
            } else {
                logger.error("Can't select event choice, wrong event name")
            }
        } else {
            logger.error("Can't select event choice, index out of range")
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }

}