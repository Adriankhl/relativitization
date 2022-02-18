package relativitization.universe.mechanisms.defaults.dilated.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.*
import relativitization.universe.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.min

/**
 * Pop buy resource to fulfill their desire
 * Can buy from the player which the pop belongs to only
 * Will implement buying from other player in the future
 */
object PopBuyResource : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { mutableCarrierData ->
            PopType.values().forEach { popType ->
                val commonPopData = mutableCarrierData.allPopData.getCommonPopData(popType)
                buyFromThisPlayer(
                    mutablePlayerData.playerInternalData.physicsData(),
                    commonPopData,
                    mutablePlayerData.playerInternalData.economyData(),
                )
            }
        }

        return listOf()
    }

    fun computeDesireResourceQualityClassMap(
        commonPopData: MutableCommonPopData,
        economyData: MutableEconomyData,
    ): Map<ResourceType, ResourceQualityClass> {
        val numDesire: Int = commonPopData.desireResourceMap.size

        return if (numDesire > 0) {
            // Approximate the available fuel per resource by the average
            val availableFuelPerResource: Double = commonPopData.saving / numDesire

            commonPopData.desireResourceMap.map { (resourceType, desireData) ->
                val selectedClass: ResourceQualityClass =
                    economyData.resourceData.tradeQualityClass(
                        resourceType,
                        desireData.desireAmount,
                        desireData.desireQuality,
                        availableFuelPerResource,
                        true,
                    )
                resourceType to selectedClass
            }.toMap()
        } else {
            mapOf()
        }
    }

    /**
     * Compute the resource amount, without considering price
     */
    fun computeDesireResourceAmountMap(
        commonPopData: MutableCommonPopData,
        economyData: MutableEconomyData,
        desireResourceClassMap: Map<ResourceType, ResourceQualityClass>
    ): Map<ResourceType, Double> {

        return commonPopData.desireResourceMap.map { (resourceType, desireData) ->
            val selectedClass: ResourceQualityClass = desireResourceClassMap.getValue(resourceType)
            resourceType to min(
                economyData.resourceData.getTradeResourceAmount(
                    resourceType,
                    selectedClass
                ),
                desireData.desireAmount
            )
        }.toMap()
    }


    fun buyFromThisPlayer(
        physicsData: MutablePhysicsData,
        commonPopData: MutableCommonPopData,
        economyData: MutableEconomyData,
    ) {

        val desireResourceClassMap: Map<ResourceType, ResourceQualityClass> =
            computeDesireResourceQualityClassMap(commonPopData, economyData)

        val desireResourceAmountMap: Map<ResourceType, Double> =
            computeDesireResourceAmountMap(
                commonPopData,
                economyData,
                desireResourceClassMap
            )

        val totalPrice: Double = desireResourceAmountMap.keys.fold(0.0) { acc, resourceType ->
            val amount: Double = desireResourceAmountMap.getValue(resourceType)
            val qualityClass: ResourceQualityClass = desireResourceClassMap.getValue(resourceType)
            val price: Double = economyData.resourceData.getResourcePrice(resourceType, qualityClass)

            acc + amount * price
        }

        // If saving is smaller than total price, only buy a fraction
        val priceFraction: Double = if (totalPrice > 0.0) {
            min(commonPopData.saving / totalPrice, 1.0)
        } else {
            1.0
        }

        commonPopData.desireResourceMap.forEach { (resourceType, _) ->
            val selectedClass: ResourceQualityClass = desireResourceClassMap.getValue(resourceType)

            val selectedQuality: MutableResourceQualityData =
                economyData.resourceData.getResourceQuality(
                    resourceType = resourceType,
                    resourceQualityClass = selectedClass,
                )

            val buyAmount: Double = desireResourceAmountMap.getValue(resourceType) * priceFraction

            // Buy resource
            economyData.resourceData.getResourceAmountData(
                resourceType,
                selectedClass
            ).trade -= buyAmount
            commonPopData.addDesireResource(
                resourceType,
                selectedQuality,
                buyAmount
            )
        }

        commonPopData.saving -= totalPrice * priceFraction
        physicsData.addFuel(totalPrice * priceFraction)
    }
}