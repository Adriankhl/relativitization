package relativitization.universe.game.mechanisms.defaults.dilated.production

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.economy.times
import relativitization.universe.game.data.components.defaults.popsystem.pop.entertainer.MutableEntertainerPopData
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.components.popSystemData
import kotlin.random.Random

object EntertainmentProduction : Mechanism() {
    // Parameters
    private const val entertainmentOutputFactor: Double = 20.0

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            val entertainerPopData: MutableEntertainerPopData =
                mutableCarrierData.allPopData.entertainerPopData

            val amount: Double = computeEntertainmentAmount(
                entertainerPopData.commonPopData.adultPopulation *
                        entertainerPopData.commonPopData.employmentRate
            )

            val qualityData: MutableResourceQualityData = computeEntertainmentQuality(
                entertainerPopData,
                mutablePlayerData.playerInternalData.playerScienceData().playerScienceApplicationData
                    .idealEntertainmentQuality
            )

            mutablePlayerData.playerInternalData.economyData().resourceData.addResource(
                newResourceType = ResourceType.ENTERTAINMENT,
                newResourceQuality = qualityData,
                newResourceAmount = amount,
            )
        }


        return listOf()
    }

    fun computeEntertainmentAmount(
        numEmployee: Double,
    ): Double {
        return numEmployee * entertainmentOutputFactor
    }

    /**
     * Compute the entertainment quality, affected by satisfaction and education level
     */
    fun computeEntertainmentQuality(
        entertainerPopData: MutableEntertainerPopData,
        idealEntertainmentQuality: MutableResourceQualityData
    ): MutableResourceQualityData {
        val satisfactionFactor: Double = if (entertainerPopData.commonPopData.satisfaction > 1.0) {
            1.0
        } else {
            entertainerPopData.commonPopData.satisfaction
        }

        // Modifier by education level, range from 0.5 to 1.5
        val educationLevel: Double = (entertainerPopData.commonPopData.educationLevel)
        val educationLevelMultiplier: Double = when {
            educationLevel > 1.0 -> 1.5
            educationLevel < 0.0 -> 0.5
            else -> educationLevel + 0.5
        }

        return idealEntertainmentQuality * satisfactionFactor * educationLevelMultiplier
    }
}