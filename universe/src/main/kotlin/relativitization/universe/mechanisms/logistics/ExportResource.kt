package relativitization.universe.mechanisms.logistics

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.component.popsystem.pop.MutableAllPopData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object ExportResource : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        TODO("Not yet implemented")
    }

    /**
     * Compute the fraction of the effeciency of export centers
     */
    fun computeExportFraction(
        mutableAllPopData: MutableAllPopData
    ) : Double {
        return 1.0
    }
}