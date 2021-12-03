package relativitization.universe.mechanisms.defaults.regular.events

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.events.AutoEvent
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism


/**
 * Object to store all the generator of events
 */
object AutoEventCollection : Mechanism() {
    private val autoEventList: List<AutoEvent> = listOf(
    )

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        return autoEventList.map { autoEvent ->
            val eventList = autoEvent.generateEventList(universeData3DAtPlayer)
            eventList.map { event ->
                AddEventCommand(
                    event = event,
                    fromInt4D = mutablePlayerData.int4D.toInt4D()
                )
            }
        }.flatten()
    }
}
