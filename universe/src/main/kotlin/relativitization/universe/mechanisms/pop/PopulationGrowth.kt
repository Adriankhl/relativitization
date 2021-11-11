package relativitization.universe.mechanisms.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.popsystem.pop.medic.MedicPopData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism
import relativitization.universe.utils.RelativitizationLogManager

object PopulationGrowth : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val gamma: Double = Relativistic.gamma(
            universeData3DAtPlayer.getCurrentPlayerData().velocity,
            universeSettings.speedOfLight
        )

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            val totalPopulation: Double = mutableCarrierData.allPopData.totalAdultPopulation()
        }

        return listOf()
    }

    /**
     * Compute the effect ot medic pop
     */
    fun computeMedicFactor(
        medicPopData: MedicPopData,
        totalPopulation: Double
    ): Double {
        // adjust the effective population by satisfaction
        val effectiveMedicPopulation: Double = if (medicPopData.commonPopData.satisfaction > 1.0) {
            medicPopData.commonPopData.adultPopulation
        } else {
            medicPopData.commonPopData.adultPopulation * medicPopData.commonPopData.satisfaction
        }

        return if (totalPopulation > 0.0) {
            if (effectiveMedicPopulation * 10.0 > totalPopulation) {
                1.0
            } else {
                effectiveMedicPopulation * 10.0 / totalPopulation
            }
        } else {
            logger.error("Population <= 0.0")
            1.0
        }
    }
}