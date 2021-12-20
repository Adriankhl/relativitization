package relativitization.universe.mechanisms.defaults.dilated.economy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object UpdatePrice : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val tradeNeedMap: Map<ResourceType, Map<ResourceQualityClass, Double>> =
            computeResourceTradeNeedMap(mutablePlayerData)

        return listOf()
    }

    /**
     * Compute the trading need of each resource type and quality class of this player
     */
    private fun computeResourceTradeNeedMap(
        playerData: MutablePlayerData
    ): Map<ResourceType, Map<ResourceQualityClass, Double>> {
        val tradeNeedMap: Map<ResourceType, MutableMap<ResourceQualityClass, Double>> =
            ResourceType.values().map { resourceType ->
                resourceType to ResourceQualityClass.values().map { resourceQualityClass ->
                    resourceQualityClass to 0.0
                }.toMap().toMutableMap()
            }.toMap()

        // Add pop desire
        playerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrierData ->
            PopType.values().forEach { popType ->
                val commonPopData: MutableCommonPopData =
                    carrierData.allPopData.getCommonPopData(popType)

                commonPopData.desireResourceMap.forEach { (resourceType, desireData) ->
                    val qualityClass: ResourceQualityClass =
                        playerData.playerInternalData.economyData().resourceData.tradeQualityClass(
                            resourceType = resourceType,
                            amount = desireData.desireAmount,
                            targetQuality = desireData.desireQuality,
                            budget = commonPopData.saving
                        )
                    val originalAmount: Double =
                        tradeNeedMap.getValue(resourceType).getValue(qualityClass)

                    tradeNeedMap.getValue(resourceType)[qualityClass] =
                        originalAmount + desireData.desireAmount
                }
            }
        }

        return tradeNeedMap
    }
}