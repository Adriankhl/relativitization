package relativitization.universe.data.events

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.CanSendCheckMessage
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.I18NString

/**
 * Ask to merge this player to its direct leader
 */
@Serializable
data class MergeCarrierEvent(
    override val toId: Int,
    override val fromId: Int
) : DefaultEvent() {
    override val description: I18NString
        get() = TODO("Not yet implemented")
    override val choiceDescription: Map<Int, I18NString>
        get() = TODO("Not yet implemented")
    override val stayTime: Int
        get() = TODO("Not yet implemented")

    override fun shouldCancelThisEvent(
        mutableEventData: MutableEventData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun defaultChoice(eventId: Int, universeData3DAtPlayer: UniverseData3DAtPlayer): Int {
        TODO("Not yet implemented")
    }

    override fun canSend(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): CanSendCheckMessage {
        TODO("Not yet implemented")
    }

    override fun canExecute(
        playerData: MutablePlayerData,
        universeSettings: UniverseSettings
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun generateCommands(
        eventId: Int,
        choice: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): List<Command> {
        TODO("Not yet implemented")
    }
}