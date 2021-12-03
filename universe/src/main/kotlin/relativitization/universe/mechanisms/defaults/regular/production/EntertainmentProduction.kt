package relativitization.universe.mechanisms.defaults.regular.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.entertainer.MutableEntertainerPopData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism

object EntertainmentProduction : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            val entertainerPopData: MutableEntertainerPopData =
                mutableCarrierData.allPopData.entertainerPopData

            val amount: Double = entertainerPopData.commonPopData.adultPopulation * 20.0

            val qualityData: MutableResourceQualityData = computeEntertainmentQuality(
                entertainerPopData,
                mutablePlayerData.playerInternalData.playerScienceData().playerScienceApplicationData.idealEntertainmentQuality
            )

            mutablePlayerData.playerInternalData.economyData().resourceData.addNewResource(
                resourceType = ResourceType.ENTERTAINMENT,
                newResourceQuality = qualityData,
                amount = amount,
            )
        }


        return listOf()
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

        val educationFactor: Double = entertainerPopData.commonPopData.educationLevel

        return idealEntertainmentQuality * satisfactionFactor * educationFactor
    }
}