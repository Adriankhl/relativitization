package relativitization.universe.game.mechanisms.defaults.dilated.pop

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.game.data.components.defaults.popsystem.pop.getCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.totalAdultPopulation
import relativitization.universe.game.data.components.popSystemData
import kotlin.random.Random

object Educate : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            val totalPopulation: Double = mutableCarrierData.allPopData.totalAdultPopulation()

            val educatorPopulation: Double = mutableCarrierData.allPopData.getCommonPopData(
                PopType.EDUCATOR
            ).adultPopulation

            val educatorSatisfaction: Double = mutableCarrierData.allPopData.getCommonPopData(
                PopType.EDUCATOR
            ).satisfaction

            PopType.entries.forEach { popType ->
                val commonPopData: MutableCommonPopData =
                    mutableCarrierData.allPopData.getCommonPopData(
                        popType
                    )
                val newEducationLevel: Double = computeNewEducationLevel(
                    originalEducationLevel = commonPopData.educationLevel,
                    educatorSatisfaction = educatorSatisfaction,
                    educatorPopulation = educatorPopulation,
                    totalPopulation = totalPopulation
                )

                commonPopData.educationLevel = newEducationLevel
            }
        }


        return listOf()
    }

    fun computeNewEducationLevel(
        originalEducationLevel: Double,
        educatorSatisfaction: Double,
        educatorPopulation: Double,
        totalPopulation: Double,
    ): Double {
        val maxDeltaEducationLevel = 0.05

        val idealEducatorLevel: Double = if (totalPopulation > 0.0) {
            educatorPopulation * 10.0 * educatorSatisfaction / totalPopulation
        } else {
            0.0
        }

        val educationDiff: Double = idealEducatorLevel - originalEducationLevel

        val educationLevelChange: Double = when {
            educationDiff > maxDeltaEducationLevel -> maxDeltaEducationLevel
            educationDiff < -maxDeltaEducationLevel -> -maxDeltaEducationLevel
            else -> educationDiff
        }

        val tempEducationLevel: Double = originalEducationLevel + educationLevelChange

        return when {
            tempEducationLevel < 0.0 -> 0.0
            tempEducationLevel < 1.0 -> tempEducationLevel
            else -> 1.0
        }
    }
}