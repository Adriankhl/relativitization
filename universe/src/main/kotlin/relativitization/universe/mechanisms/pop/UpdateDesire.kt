package relativitization.universe.mechanisms.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.economy.MutableResourceQualityData
import relativitization.universe.data.components.economy.ResourceQualityData
import relativitization.universe.data.components.economy.ResourceType
import relativitization.universe.data.components.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.popsystem.pop.MutableResourceDesireData
import relativitization.universe.data.components.popsystem.pop.PopType
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object UpdateDesire : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
            PopType.values().forEach { popType ->
                carrier.allPopData
            }
        }

        return listOf()
    }

    /**
     * Compute desire resource type of pop
     *
     * @param popType the desire of this pop
     */
    fun computeDesireResourceType(
        popType: PopType
    ): List<ResourceType> {
        return when (popType) {
            PopType.LABOURER -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
            )
            PopType.ENGINEER -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
            )
            PopType.SCHOLAR -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
            )
            PopType.EDUCATOR -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
            )
            PopType.MEDIC -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
                ResourceType.MEDICINE,
            )
            PopType.SERVICE_WORKER -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
            )
            PopType.ENTERTAINER -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
            )
            PopType.SOLDIER -> listOf(
                ResourceType.FOOD,
                ResourceType.CLOTH,
                ResourceType.HOUSEHOLD_GOOD,
                ResourceType.ENTERTAINMENT,
                ResourceType.AMMUNITION,
            )
        }
    }

    fun computeDesireResourceAmount(
        mutableCommonPopData: MutableCommonPopData
    ): Double {
        return mutableCommonPopData.adultPopulation
    }

    /**
     * Compute the new desire resource quality
     */
    fun computeDesireResourceQuality(
        mutableCommonPopData: MutableCommonPopData,
        resourceType: ResourceType,
        changeFactor: Double,
    ): MutableResourceQualityData {
        return if (mutableCommonPopData.desireResourceMap.containsKey(resourceType)) {
            if (mutableCommonPopData.lastDesireResourceMap.containsKey(resourceType)) {
                val oldQuality: MutableResourceQualityData = mutableCommonPopData.desireResourceMap.getValue(resourceType).desireQuality
                val newQuality: MutableResourceQualityData = mutableCommonPopData.lastDesireResourceMap.getValue(resourceType).desireQuality

                oldQuality + (newQuality - oldQuality) * changeFactor
            } else {
                mutableCommonPopData.desireResourceMap.getValue(resourceType).desireQuality * (1.0 - changeFactor)
            }
        } else {
            if (mutableCommonPopData.lastDesireResourceMap.containsKey(resourceType)) {
                mutableCommonPopData.lastDesireResourceMap.getValue(resourceType).desireQuality * changeFactor
            } else {
                MutableResourceQualityData()
            }
        }
    }
}