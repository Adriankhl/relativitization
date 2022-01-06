package relativitization.universe.mechanisms.defaults.dilated.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.components.defaults.popsystem.pop.medic.MutableMedicPopData
import relativitization.universe.data.global.UniverseGlobalData
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

                val newPopulation: Double = if (commonPopData.adultPopulation < 0.0) {
                    computeNewPop(
                        popType = popType,
                        medicFactor = medicFactor,
                        satisfaction = commonPopData.satisfaction,
                        educationLevel = commonPopData.educationLevel,
                        currentPopulation = commonPopData.adultPopulation,
                        currentTotalPopulation = totalPopulation,
                        idealTotalPopulation = mutableCarrierData.carrierInternalData.idealPopulation,
                    )
                } else {
                    logger.error("Adult population of $popType < 0")
                    0.0
                }

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
        val constantPopulationGrowth: Double = 100.0

        val educationFactor: Double =
            if (popType == PopType.SCHOLAR || popType == PopType.ENGINEER) {
                (educationLevel - 0.5) * 2.0
            } else {
                1.0
            }

        val idealPopulationFactor: Double = when{
            currentTotalPopulation < idealTotalPopulation * 0.5 -> {
                // Encourage concentrating pop in one place by effective ratio
                val effectiveRatio: Double = (currentTotalPopulation / idealTotalPopulation).pow(1.1)
                // Plus 2.5 to make this function continuous
                7.5 * (1.0 - effectiveRatio) + 2.5
            }
            currentPopulation < idealTotalPopulation -> {
                5.0 * (1.0 - currentTotalPopulation / idealTotalPopulation)
            }
            else -> {
                (0.5).pow(currentTotalPopulation / idealTotalPopulation - 1.0)
            }
        }

        // Population increase if > 1.0, else population decrease
        val overallFactor: Double = educationFactor * idealPopulationFactor * medicFactor * satisfaction - 1.0

        val actualOverallFactor: Double = when {
            overallFactor > 1.0 -> {
                1.0
            }
            overallFactor < -1.0 -> {
                -1.0
            }
            else -> {
                overallFactor
            }
        }

        return maxPopulationChange * actualOverallFactor + constantPopulationGrowth
    }
}