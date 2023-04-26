package relativitization.universe.game.mechanisms.defaults.dilated.economy

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.game.data.components.defaults.economy.MutableResourceData
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.economy.getResourcePrice
import relativitization.universe.game.data.components.defaults.economy.getSingleResourceData
import relativitization.universe.game.data.components.defaults.economy.getTradeResourceAmount
import relativitization.universe.game.data.components.defaults.economy.tradeQualityClass
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.game.data.components.defaults.popsystem.pop.getCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.employeeFraction
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.components.popSystemData
import kotlin.random.Random

object UpdatePrice : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()

    // Parameters
    // Determine how need and available amount should be compared
    private const val needToAvailableFactor: Double = 10.0

    // Determine maximum price change
    private const val maxPriceIncreaseFactor: Double = 1.25
    private const val maxPriceDecreaseFactor: Double = 0.8

    // Determine the maximum and minimum price relative to base salary
    private const val maxPriceFactor: Double = 10.0
    private const val minPriceFactor: Double = 0.05

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        val tradeNeedMap: Map<ResourceType, Map<ResourceQualityClass, Double>> =
            computeResourceTradeNeedMap(mutablePlayerData)

        // Use base salary to bound the price
        val baseSalaryPerEmployee: Double =
            mutablePlayerData.playerInternalData.popSystemData().generalPopSystemData.baseSalaryPerEmployee

        ResourceType.values().forEach { resourceType ->
            ResourceQualityClass.values().forEach { resourceQualityClass ->
                val resourceData: MutableResourceData =
                    mutablePlayerData.playerInternalData.economyData().resourceData

                val newPrice: Double = computeNewPrice(
                    oldPrice = resourceData.getResourcePrice(resourceType, resourceQualityClass),
                    amountAvailable = resourceData.getTradeResourceAmount(
                        resourceType,
                        resourceQualityClass
                    ),
                    amountNeeded = tradeNeedMap.getValue(resourceType)
                        .getValue(resourceQualityClass),
                    baseSalaryPerEmployee = baseSalaryPerEmployee,
                )

                resourceData.getSingleResourceData(
                    resourceType,
                    resourceQualityClass
                ).resourcePrice = newPrice
            }
        }

        return listOf()
    }

    /**
     * Compute the trading need of each resource type and quality class of this player
     */
    private fun computeResourceTradeNeedMap(
        playerData: MutablePlayerData
    ): Map<ResourceType, Map<ResourceQualityClass, Double>> {
        val tradeNeedMap: Map<ResourceType, MutableMap<ResourceQualityClass, Double>> =
            ResourceType.values().associateWith {
                ResourceQualityClass.values().associateWith {
                    0.0
                }.toMutableMap()
            }

        // Add trade needed by pop desire
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
                            budget = commonPopData.saving,
                            preferHighQualityClass = true
                        )

                    val originalAmount: Double =
                        tradeNeedMap.getValue(resourceType).getValue(qualityClass)

                    val amountToAdd: Double = desireData.desireAmount
                    if (amountToAdd < 0.0) {
                        logger.error("pop desire smaller than 0.0")
                    }

                    tradeNeedMap.getValue(resourceType)[qualityClass] = originalAmount + amountToAdd
                }
            }
        }

        // Add trade needed by foreign factory
        playerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrierData ->
            carrierData.allPopData.labourerPopData.resourceFactoryMap.values.filter {
                it.ownerPlayerId != playerData.playerId
            }.forEach { resourceFactory ->
                val numInputResource: Int =
                    resourceFactory.resourceFactoryInternalData.inputResourceMap.size
                // Approximate budge perR
                val budgetPerResource: Double = if (numInputResource > 0) {
                    resourceFactory.storedFuelRestMass / numInputResource
                } else {
                    0.0
                }

                resourceFactory.resourceFactoryInternalData.inputResourceMap.keys.forEach { resourceType ->
                    val qualityClass: ResourceQualityClass =
                        playerData.playerInternalData.economyData().resourceData.tradeQualityClass(
                            resourceType = resourceType,
                            amount = resourceFactory.resourceFactoryInternalData.inputResourceMap
                                .getValue(resourceType).amountPerOutput *
                                    resourceFactory.resourceFactoryInternalData
                                        .maxOutputAmountPerEmployee *
                                    resourceFactory.maxNumEmployee *
                                    resourceFactory.employeeFraction(),
                            targetQuality = resourceFactory.resourceFactoryInternalData
                                .inputResourceMap.getValue(resourceType).qualityData,
                            budget = budgetPerResource,
                            preferHighQualityClass = false
                        )
                    val originalAmount: Double =
                        tradeNeedMap.getValue(resourceType).getValue(qualityClass)

                    val amountToAdd: Double = resourceFactory.resourceFactoryInternalData
                        .inputResourceMap.getValue(resourceType).amountPerOutput *
                            resourceFactory.resourceFactoryInternalData.maxOutputAmountPerEmployee *
                            resourceFactory.maxNumEmployee *
                            resourceFactory.employeeFraction()
                    if (amountToAdd < 0.0) {
                        logger.error("resource factory input desire smaller than 0.0")
                    }

                    tradeNeedMap.getValue(resourceType)[qualityClass] = originalAmount + amountToAdd
                }
            }
        }

        // Add trade needed by export center
        playerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrierData ->
            carrierData.allPopData.servicePopData.exportData.playerExportCenterMap.values.forEach { playerExportCenter ->
                playerExportCenter.exportDataList.forEach { playerSingleExport ->
                    val resourceType: ResourceType = playerSingleExport.resourceType
                    val qualityClass: ResourceQualityClass = playerSingleExport.resourceQualityClass

                    val originalAmount: Double =
                        tradeNeedMap.getValue(resourceType).getValue(qualityClass)


                    val amountToAdd: Double = playerSingleExport.amountPerTime
                    if (amountToAdd < 0.0) {
                        logger.error("player export desire smaller than 0.0")
                    }

                    tradeNeedMap.getValue(resourceType)[qualityClass] = originalAmount + amountToAdd
                }
            }

            carrierData.allPopData.servicePopData.exportData.popExportCenterMap.values.forEach { popExportCenter ->
                popExportCenter.exportDataMap.values.flatMap {
                    it.values
                }.flatten().forEach { popSingleExport ->
                    val resourceType: ResourceType = popSingleExport.resourceType
                    val qualityClass: ResourceQualityClass = popSingleExport.resourceQualityClass

                    val originalAmount: Double =
                        tradeNeedMap.getValue(resourceType).getValue(qualityClass)

                    val amountToAdd: Double = popSingleExport.amountPerTime
                    if (amountToAdd < 0.0) {
                        logger.error("pop export desire smaller than 0.0")
                    }

                    tradeNeedMap.getValue(resourceType)[qualityClass] = originalAmount + amountToAdd
                }
            }
        }

        return tradeNeedMap
    }

    /**
     * Compute the new price for a resource
     *
     * @param oldPrice the old price of the resource
     * @param amountAvailable the available amount of the resource for trading
     * @param amountNeeded the amount of the resource needed
     * @param baseSalaryPerEmployee the base salary of the population
     */
    private fun computeNewPrice(
        oldPrice: Double,
        amountAvailable: Double,
        amountNeeded: Double,
        baseSalaryPerEmployee: Double
    ): Double {
        // Maximum price and minimum price are determined by average salary of the population
        // Unevenly scaled since one pop need several resources
        val maxPrice: Double = baseSalaryPerEmployee * maxPriceFactor
        val minPrice: Double = baseSalaryPerEmployee * minPriceFactor

        return when {
            oldPrice > maxPrice -> oldPrice * maxPriceDecreaseFactor
            oldPrice < minPrice -> oldPrice * maxPriceIncreaseFactor
            else -> {
                val needToAvailableRatio: Double = if (amountAvailable > 0.0) {
                    amountNeeded * needToAvailableFactor / amountAvailable
                } else {
                    // Set the ratio to a very high value
                    Double.MAX_VALUE * 1E-10
                }

                // Compute the price change factor to modify the old price
                val priceChangeFactor: Double = when {
                    needToAvailableRatio > 5.0 -> maxPriceIncreaseFactor
                    (needToAvailableRatio > 1.0) -> {
                        ((needToAvailableRatio - 1.0) / 4.0) * (maxPriceIncreaseFactor - 1.0) + 1.0
                    }
                    (needToAvailableRatio > 0.2) -> {
                        ((needToAvailableRatio - 0.2) / 0.8) *
                                (1.0 - maxPriceDecreaseFactor) +
                                maxPriceDecreaseFactor
                    }
                    needToAvailableRatio >= 0.0 -> {
                        maxPriceDecreaseFactor
                    }
                    else -> {
                        logger.error("Need is smaller than 0")
                        1.0
                    }
                }

                val targetPrice: Double = oldPrice * priceChangeFactor

                when {
                    targetPrice > maxPrice -> maxPrice
                    targetPrice < minPrice -> minPrice
                    else -> targetPrice
                }
            }
        }
    }
}