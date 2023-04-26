package relativitization.universe.game.mechanisms.defaults.dilated.pop

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.game.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableResourceDesireData
import relativitization.universe.game.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.components.defaults.popsystem.pop.getCommonPopData
import kotlin.random.Random

object UpdateDesire : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        // Parameters
        val desireQualityUpdateFactor = 0.2
        val desireQualityUpdateDiff = 0.2

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
            PopType.values().forEach { popType ->
                val mutableCommonPopData: MutableCommonPopData =
                    carrier.allPopData.getCommonPopData(popType)

                val desireAmount: Double = computeDesireResourceAmount(
                    mutableCommonPopData = mutableCommonPopData
                )

                val desireResourceTypeList: List<ResourceType> = computeDesireResourceType(
                    popType
                )

                val desireResourceMap: Map<ResourceType, MutableResourceDesireData> =
                    desireResourceTypeList.associateWith {
                        val desireQualityData: MutableResourceQualityData =
                            computeDesireResourceQuality(
                                mutableCommonPopData = mutableCommonPopData,
                                resourceType = it,
                                desireQualityUpdateFactor = desireQualityUpdateFactor,
                                desireQualityUpdateMinDiff = desireQualityUpdateDiff,
                            )

                        MutableResourceDesireData(
                            desireAmount,
                            desireQualityData
                        )
                    }


                // Update desire
                mutableCommonPopData.desireResourceMap.clear()
                mutableCommonPopData.desireResourceMap.putAll(desireResourceMap)
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
            PopType.SCHOLAR -> listOf(
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

    /**
     * Compute the desire amount without the effect of time dilation
     */
    fun computeDesireResourceAmount(
        mutableCommonPopData: MutableCommonPopData
    ): Double {
        return mutableCommonPopData.adultPopulation
    }

    /**
     * Compute the new desire resource quality, adjusted by time dilation
     */
    fun computeDesireResourceQuality(
        mutableCommonPopData: MutableCommonPopData,
        resourceType: ResourceType,
        desireQualityUpdateFactor: Double,
        desireQualityUpdateMinDiff: Double,
    ): MutableResourceQualityData {
        val originalDesire: MutableResourceDesireData =
            mutableCommonPopData.desireResourceMap.getOrDefault(
                resourceType,
                MutableResourceDesireData()
            )

        val inputDesire: MutableResourceDesireData =
            mutableCommonPopData.resourceInputMap.getOrDefault(
                resourceType,
                MutableResourceDesireData()
            )

        // If sufficient amount, get close to the input quality
        // else decrease the desire quality
        // Adjusted by time dilation
        return if (inputDesire.desireAmount > (originalDesire.desireAmount)) {
            originalDesire.desireQuality.changeTo(
                inputDesire.desireQuality,
                desireQualityUpdateFactor,
                desireQualityUpdateMinDiff,
            )
        } else {
            originalDesire.desireQuality.changeTo(
                MutableResourceQualityData(0.0),
                desireQualityUpdateFactor,
                desireQualityUpdateMinDiff,
            )
        }
    }
}