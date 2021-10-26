package relativitization.universe.mechanisms.logistics

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.component.popsystem.pop.MutableAllPopData
import relativitization.universe.data.component.popsystem.pop.service.MutableServicePopData
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
        mutableServicePopData: MutableServicePopData
    ) : Double {
        val numEmployee: Double = mutableServicePopData.commonPopData.numEmployee()

        val educationLevelMultiplier: Double = mutableServicePopData.commonPopData.educationLevel * 10.0

        val totalExportAmount: Double = mutableServicePopData.exportData.totalExportAmount()

        val fraction: Double = if (totalExportAmount > 0.0) {
            numEmployee * educationLevelMultiplier / totalExportAmount
        } else {
            1.0
        }

        return when {
            fraction > 1.0 -> 1.0
            fraction < 0.0 -> 0.0
            else -> fraction
        }
    }
}