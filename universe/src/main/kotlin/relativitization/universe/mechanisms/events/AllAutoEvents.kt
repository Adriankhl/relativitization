package relativitization.universe.mechanisms.events

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.data.events.AutoEvent
import relativitization.universe.mechanisms.Mechanism


/**
 * Object to store all the generator of events
 */
object AllAutoEvent : Mechanism() {
    val autoEventList: List<AutoEvent> = listOf(
    )

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): List<Command> {
        for (autoEvent in autoEventList) {
            // TODO: set add self event and generate commands for non self event
        }
        return listOf()
    }
}
