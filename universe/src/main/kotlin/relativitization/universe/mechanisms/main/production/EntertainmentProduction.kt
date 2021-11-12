package relativitization.universe.mechanisms.main.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.economy.MutableResourceQualityData
import relativitization.universe.data.components.economy.ResourceType
import relativitization.universe.data.components.popsystem.pop.entertainer.MutableEntertainerPopData
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

        val gamma: Double = Relativistic.gamma(
            universeData3DAtPlayer.getCurrentPlayerData().velocity,
            universeSettings.speedOfLight
        )

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            val entertainerPopData: MutableEntertainerPopData = mutableCarrierData.allPopData.entertainerPopData

            val amount: Double = entertainerPopData.commonPopData.adultPopulation * 20.0 / gamma

            val qualityData: MutableResourceQualityData = computeEntertainmentQuality(
                entertainerPopData,
                mutablePlayerData.playerInternalData.playerScienceData().playerScienceProductData.idealEntertainmentQuality
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