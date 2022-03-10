package relativitization.universe.mechanisms.defaults.dilated.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.PopBuyResourceCommand
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
        val neighborList: List<PlayerData> = universeData3DAtPlayer.getNeighbour(1)

        val commandList: List<Command> = mutablePlayerData.playerInternalData.popSystemData()
            .carrierDataMap.values.map { mutableCarrierData ->
                PopType.values().map { popType ->
                    val commonPopData = mutableCarrierData.allPopData.getCommonPopData(popType)
                    commonPopData.desireResourceMap.keys.map { resourceType ->
                        buyResource(
                            resourceType,
                            commonPopData,
                            mutablePlayerData.playerInternalData.physicsData(),
                            mutablePlayerData.playerInternalData.economyData(),
                            neighborList,
                        )
                    }
                }
            }.flatten().flatten().flatten()

        return commandList
    }


    /**
     * Pop buy resource from this player or other player
     */
    fun buyResource(
        resourceType: ResourceType,
        commonPopData: MutableCommonPopData,
        physicsData: MutablePhysicsData,
        economyData: MutableEconomyData,
        neighborList: List<PlayerData>,
    ): List<PopBuyResourceCommand> {
        // Desire of this resource
        val desireAmount: Double =
            commonPopData.desireResourceMap.getValue(resourceType).desireAmount
        val desireQuality: MutableResourceQualityData =
            commonPopData.desireResourceMap.getValue(resourceType).desireQuality

        // available saving for this resource
        val numDesire: Int = commonPopData.desireResourceMap.size
        val availableFuel: Double = commonPopData.saving / numDesire

        val mustBuyFromThisPlayer: Boolean = mustBuyFromThisPlayer(
            resourceType = resourceType,
            desireAmount = desireAmount,
            desireQualityData = desireQuality,
            availableFuel = availableFuel,
            satisfaction = commonPopData.satisfaction,
            economyData = economyData,
        )

        return if (mustBuyFromThisPlayer) {
            buyFromThisPlayer(
                resourceType = resourceType,
                desireAmount = desireAmount,
                desireQualityData = desireQuality,
                availableFuel = availableFuel,
                commonPopData = commonPopData,
                physicsData = physicsData,
                economyData = economyData,
            )
            listOf()
        } else {
            // Compare this player and other players


            listOf()
        }
    }

    /**
     * Whether the pop must buy the resource from this player
     * True if satisfaction is low and there are enough resource
     */
    fun mustBuyFromThisPlayer(
        resourceType: ResourceType,
        desireAmount: Double,
        desireQualityData: MutableResourceQualityData,
        availableFuel: Double,
        satisfaction: Double,
        economyData: MutableEconomyData,
    ): Boolean {
        return if (satisfaction > 0.5) {
            false
        } else {
            val qualityClass: ResourceQualityClass = economyData.resourceData.tradeQualityClass(
                resourceType = resourceType,
                amount = desireAmount,
                targetQuality = desireQualityData,
                budget = availableFuel,
                preferHighQualityClass = true
            )
            val availableAmount: Double = economyData.resourceData.getTradeResourceAmount(
                resourceType,
                qualityClass
            )

            availableAmount >= desireAmount
        }
    }

    /**
     * Compute the resource quality class if the pop buy from this market
     *
     * @param commonPopData the pop to buy resource
     * @param economyData the market
     */
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
     * Compute the resource quality class if the pop buy from this market
     *
     * @param commonPopData the pop to buy resource
     * @param economyData the market
     */
    fun computeDesireResourceQualityClassMap(
        commonPopData: MutableCommonPopData,
        economyData: EconomyData,
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
                        desireData.desireQuality.toResourceQualityData(),
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

    /**
     * Compute the resource amount, without considering price
     */
    fun computeDesireResourceAmountMap(
        commonPopData: MutableCommonPopData,
        economyData: EconomyData,
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
        resourceType: ResourceType,
        desireAmount: Double,
        desireQualityData: MutableResourceQualityData,
        availableFuel: Double,
        commonPopData: MutableCommonPopData,
        physicsData: MutablePhysicsData,
        economyData: MutableEconomyData,
    ) {

        val qualityClass: ResourceQualityClass = economyData.resourceData.tradeQualityClass(
            resourceType = resourceType,
            amount = desireAmount,
            targetQuality = desireQualityData,
            budget = availableFuel,
            preferHighQualityClass = true
        )

        val resourceQuality: MutableResourceQualityData =
            economyData.resourceData.getResourceQuality(
                resourceType,
                qualityClass,
            )

        val availableAmount: Double = economyData.resourceData.getTradeResourceAmount(
            resourceType,
            qualityClass
        )

        // Amount to buy without considering the price
        val idealAmountToBuy: Double = min(availableAmount, desireAmount)

        val price: Double = economyData.resourceData.getResourcePrice(
            resourceType,
            qualityClass
        ) * idealAmountToBuy

        // If saving is smaller than total price, only buy a fraction
        val priceFraction: Double = if (price > 0.0) {
            min(commonPopData.saving / price, 1.0)
        } else {
            1.0
        }

        val amountToBuy: Double = idealAmountToBuy * priceFraction
        val priceToPay: Double = price * amountToBuy

        economyData.resourceData.getResourceAmountData(
            resourceType,
            qualityClass,
        ).trade -= amountToBuy

        commonPopData.addDesireResource(
            resourceType,
            resourceQuality,
            amountToBuy,
        )

        commonPopData.saving -= priceToPay
        physicsData.addInternalFuel(priceToPay)
    }
}