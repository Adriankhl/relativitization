package relativitization.universe.mechanisms.default.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.min

object Educate : Mechanism() {
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

            val educatorPopulation: Double = mutableCarrierData.allPopData.getCommonPopData(
                relativitization.universe.data.components.default.popsystem.pop.PopType.EDUCATOR
            ).adultPopulation

            val educatorSatisfaction: Double = mutableCarrierData.allPopData.getCommonPopData(
                relativitization.universe.data.components.default.popsystem.pop.PopType.EDUCATOR
            ).satisfaction

            relativitization.universe.data.components.default.popsystem.pop.PopType.values().forEach { popType ->
                val commonPopData: relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData = mutableCarrierData.allPopData.getCommonPopData(
                    popType
                )
                val newEducationLevel: Double = computeNewEducationLevel(
                    gamma = gamma,
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
        gamma: Double,
        originalEducationLevel: Double,
        educatorSatisfaction: Double,
        educatorPopulation: Double,
        totalPopulation: Double,
    ): Double {
        val maxDeltaEducationLevel: Double = 0.05

        val educatorFactor: Double = if (totalPopulation > 0.0) {
            educatorPopulation * 10.0 / totalPopulation
        } else {
            1.0
        }

        val relativeNewEducationLevel: Double = min(
            maxDeltaEducationLevel * 2.0,
            maxDeltaEducationLevel * educatorFactor * educatorSatisfaction
        )

        // Adjusted by time dilation
        val educationLevelChange: Double = (relativeNewEducationLevel - maxDeltaEducationLevel) / gamma

        val tempEducationLevel: Double = originalEducationLevel + educationLevelChange

        return when {
            tempEducationLevel < 0.0 -> 0.0
            tempEducationLevel < 1.0 -> tempEducationLevel
            else -> 1.0
        }
    }
}