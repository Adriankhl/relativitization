package relativitization.universe.mechanisms.default.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.default.popsystem.pop.PopType
import relativitization.universe.data.components.default.popsystem.pop.medic.MutableMedicPopData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.min
import kotlin.math.pow

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
            val medicFactor: Double = computeMedicFactor(
                mutableCarrierData.allPopData.medicPopData,
                totalPopulation,
            )

            PopType.values().forEach { popType ->
                val commonPopData: MutableCommonPopData =
                    mutableCarrierData.allPopData.getCommonPopData(
                        popType
                    )

                val newPopulation: Double = computeNewPop(
                    gamma = gamma,
                    popType = popType,
                    medicFactor = medicFactor,
                    satisfaction = commonPopData.satisfaction,
                    educationLevel = commonPopData.educationLevel,
                    currentPopulation = commonPopData.adultPopulation,
                    currentTotalPopulation = totalPopulation,
                    idealTotalPopulation = mutableCarrierData.idealPopulation,
                )

                commonPopData.adultPopulation = newPopulation
            }
        }

        return listOf()
    }

    /**
     * Compute the effect ot medic pop
     */
    fun computeMedicFactor(
        medicPopData: MutableMedicPopData,
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

    /**
     * Compute new population
     */
    fun computeNewPop(
        gamma: Double,
        popType: PopType,
        medicFactor: Double,
        satisfaction: Double,
        educationLevel: Double,
        currentPopulation: Double,
        currentTotalPopulation: Double,
        idealTotalPopulation: Double,
    ): Double {
        // Maximum positive or negative change of the population
        val maxPopulationChange: Double = currentPopulation * 0.1

        // Always add 100 population to avoid 0 population
        val basePopulationGrowth: Double = 100.0

        val educationFactor: Double =
            if (popType == PopType.SCHOLAR || popType == PopType.ENGINEER) {
                (educationLevel - 0.5) * 2.0
            } else {
                1.0
            }

        val totalPopulationFactor: Double =
            if (currentTotalPopulation > idealTotalPopulation * 0.5) {
                (0.5).pow(currentPopulation / idealTotalPopulation - 1.0)
            } else {
                5.0 * (1.0 - currentPopulation / idealTotalPopulation)
            }

        // the new population compare to maxPopulationChange
        val relativeNewPopulation: Double = min(
            maxPopulationChange * 2.0,
            maxPopulationChange * educationFactor * totalPopulationFactor * medicFactor * satisfaction + basePopulationGrowth
        )

        // Adjusted by time dilation
        val populationChange: Double = (relativeNewPopulation - maxPopulationChange) / gamma

        return currentPopulation + populationChange
    }
}