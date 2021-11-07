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
        val desireQualityUpdateDiff: Double = 0.2

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
                        desireQualityUpdateMinDiff = desireQualityUpdateDiff,
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
        desireQualityUpdateMinDiff: Double,
    ): MutableResourceQualityData {
        val originalQuality: MutableResourceDesireData =
            mutableCommonPopData.desireResourceMap.getOrDefault(
                resourceType,
                MutableResourceDesireData()
            )

        val inputQuality: MutableResourceDesireData =
            mutableCommonPopData.resourceInputMap.getOrDefault(
                resourceType,
                MutableResourceDesireData()
            )

        // If sufficient amount, get close to the input quality
        // else decrease the desire quality
        return if (inputQuality.desireAmount > originalQuality.desireAmount) {
            originalQuality.desireQuality.changeTo(
                inputQuality.desireQuality,
                desireQualityUpdateFactor,
                desireQualityUpdateMinDiff,
            )
        } else {
            originalQuality.desireQuality.changeTo(
                MutableResourceQualityData(0.0, 0.0, 0.0),
                desireQualityUpdateFactor,
                desireQualityUpdateMinDiff,
            )
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