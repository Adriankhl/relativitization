package relativitization.universe.game.mechanisms.defaults.dilated.pop

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.game.data.components.defaults.popsystem.pop.getCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.medic.MutableMedicPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.totalAdultPopulation
import relativitization.universe.game.data.components.popSystemData
import kotlin.math.pow
import kotlin.random.Random

object PopulationGrowth : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            val totalPopulation: Double = mutableCarrierData.allPopData.totalAdultPopulation()

            val averageSalaryFactor: Double = if (totalPopulation > 0.0) {
                PopType.entries.sumOf { popType ->
                    mutableCarrierData.allPopData.getCommonPopData(popType).adultPopulation *
                            mutableCarrierData.allPopData.getCommonPopData(popType).salaryFactor
                } / totalPopulation
            } else {
                1.0
            }

            val medicFactor: Double = computeMedicFactor(
                mutableCarrierData.allPopData.medicPopData,
                totalPopulation,
            )

            PopType.entries.forEach { popType ->
                val commonPopData: MutableCommonPopData =
                    mutableCarrierData.allPopData.getCommonPopData(
                        popType
                    )

                val newPopulation: Double = if (commonPopData.adultPopulation >= 0.0) {
                    computeNewPop(
                        popType = popType,
                        medicFactor = medicFactor,
                        satisfaction = commonPopData.satisfaction,
                        educationLevel = commonPopData.educationLevel,
                        currentSalaryFactor = commonPopData.salaryFactor,
                        averageSalaryFactor = averageSalaryFactor,
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

        val medicLevel: Double = if (totalPopulation > 0.0) {
            effectiveMedicPopulation * 10.0 / totalPopulation
        } else {
            logger.debug("total population $totalPopulation <= 0.0")
            1.0
        }

        return when {
            medicLevel > 1.0 -> 1.25
            medicLevel < 0.0 -> 0.8
            else -> medicLevel * 0.45 + 0.8
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
        currentSalaryFactor: Double,
        averageSalaryFactor: Double,
        currentPopulation: Double,
        currentTotalPopulation: Double,
        idealTotalPopulation: Double,
    ): Double {
        // Maximum positive or negative change of the population
        val maxPopulationChange: Double = currentPopulation * 0.1

        // Always add 100 population to avoid 0 population
        val constantPopulationGrowth = 100.0

        val educationFactor: Double =
            if (popType == PopType.SCHOLAR || popType == PopType.ENGINEER) {
                // Modifier by education level, range from 0.5 to 1.5
                when {
                    educationLevel > 1.0 -> 1.25
                    educationLevel < 0.0 -> 0.8
                    else -> educationLevel * 0.45 + 0.8
                }
            } else {
                1.0
            }

        val idealPopulationFactor: Double = when{
            currentTotalPopulation < idealTotalPopulation * 0.5 -> {
                // Encourage concentrating pop in one place by effective ratio
                val effectiveRatio: Double = (currentTotalPopulation / idealTotalPopulation).pow(0.9)
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

        // Add bonus if salary factor is higher than average
        val salaryBonus: Double = (currentSalaryFactor - averageSalaryFactor) * 0.01

        // Population increase if > 0.0, else population decrease
        val overallChangeFactor: Double = educationFactor *
                idealPopulationFactor *
                medicFactor *
                satisfaction -
                1.0 +
                salaryBonus

        val actualOverallChangeFactor: Double = when {
            overallChangeFactor > 1.0 -> {
                1.0
            }
            overallChangeFactor < -1.0 -> {
                -1.0
            }
            else -> {
                overallChangeFactor
            }
        }

        return currentPopulation + maxPopulationChange * actualOverallChangeFactor + constantPopulationGrowth
    }
}