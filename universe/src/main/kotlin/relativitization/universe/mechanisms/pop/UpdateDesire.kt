package relativitization.universe.mechanisms.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.economy.MutableResourceQualityData
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
        val desireQualityUpdateFactor: Double = 0.2
        val desireQualityUpdateMinInterval: Double = 0.2

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
            PopType.values().forEach { popType ->
                val mutableCommonPopData: MutableCommonPopData =
                    carrier.allPopData.getCommonPopData(popType)

                val desireAmount: Double = computeDesireResourceAmount(
                    mutableCommonPopData
                )

                val desireResourceTypeList: List<ResourceType> = computeDesireResourceType(
                    popType
                )

                val desireResourceMap: Map<ResourceType, MutableResourceDesireData> = desireResourceTypeList.map {
                    val desireQualityData: MutableResourceQualityData = computeDesireResourceQuality(
                        mutableCommonPopData = mutableCommonPopData,
                        resourceType = it,
                        desireQualityUpdateFactor = desireQualityUpdateFactor,
                        desireQualityUpdateMinInterval = desireQualityUpdateMinInterval,
                    )

                    it to MutableResourceDesireData(desireAmount, desireQualityData)
                }.toMap()
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
        desireQualityUpdateFactor: Double,
        desireQualityUpdateMinInterval: Double,
    ): MutableResourceQualityData {
        // Four situations
        return if (mutableCommonPopData.desireResourceMap.containsKey(resourceType)) {
            if (mutableCommonPopData.resourceInputMap.containsKey(resourceType)) {
                val originalQuality: MutableResourceQualityData = mutableCommonPopData.desireResourceMap.getValue(resourceType).desireQuality
                val inputQuality: MutableResourceQualityData = mutableCommonPopData.resourceInputMap.getValue(resourceType).desireQuality

                originalQuality + (inputQuality - originalQuality) * desireQualityUpdateFactor
            } else {
                mutableCommonPopData.desireResourceMap.getValue(resourceType).desireQuality * (1.0 - desireQualityUpdateFactor)
            }
        } else {
            if (mutableCommonPopData.resourceInputMap.containsKey(resourceType)) {
                mutableCommonPopData.resourceInputMap.getValue(resourceType).desireQuality * desireQualityUpdateFactor
            } else {
                MutableResourceQualityData()
            }
        }
    }

    /**
     * Compute satisfaction
     */
    fun updateSatisfaction(
        mutableCommonPopData: MutableCommonPopData,
        desireResourceTypeList: List<ResourceType>
    ) {
        val amountFraction: List<Double> = desireResourceTypeList.map { resourceType ->
            if (mutableCommonPopData.desireResourceMap.containsKey(resourceType)) {
                if (mutableCommonPopData.resourceInputMap.containsKey(resourceType)) {
                    val originalAmount: Double = mutableCommonPopData.desireResourceMap.getValue(resourceType).desireAmount
                    val inputAmount: Double = mutableCommonPopData.resourceInputMap.getValue(resourceType).desireAmount

                    if (originalAmount > 0.0) {
                        inputAmount / originalAmount
                    } else {
                        1.0
                    }
                } else {
                    0.0
                }
            } else {
                if (mutableCommonPopData.resourceInputMap.containsKey(resourceType)) {
                    1.0
                } else {
                    0.0
                }
            }
        }
    }
}