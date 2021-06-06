package relativitization.universe.mechanisms.events

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.events.AutoEvent
import relativitization.universe.mechanisms.Mechanism


/**
 * Object to store all the generator of events
 */
object AutoEventCollection : Mechanism() {
    val autoEventList: List<AutoEvent> = listOf(
    )

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData
    ): List<Command> {
        return autoEventList.map { autoEvent ->
            val eventList = autoEvent.generateEventList(universeData3DAtPlayer)
            eventList.map { event ->
                AddEventCommand(
                    event = event,
                    fromId = mutablePlayerData.id,
                    fromInt4D = mutablePlayerData.int4D.toInt4D()
                )
            }
        }.flatten()
    }
}
