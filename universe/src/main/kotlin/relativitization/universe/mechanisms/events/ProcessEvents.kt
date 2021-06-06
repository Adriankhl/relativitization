package relativitization.universe.mechanisms.events

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.mechanisms.Mechanism

object ProcessEvents : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData
    ): List<Command> {
        return listOf()
    }
}