package relativitization.universe.mechanisms.defaults.dilated.economy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.economy.MutableResourceData
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min

object UpdatePrice : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        // Parameters
        val needAvailableFactor: Double = 10.0

        val tradeNeedMap: Map<ResourceType, Map<ResourceQualityClass, Double>> =
            computeResourceTradeNeedMap(mutablePlayerData)

        // Use average salary to bound the price of resources
        val averageSalary: Double = mutablePlayerData.playerInternalData.popSystemData().averageSalary()

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
                    needAvailableFactor = needAvailableFactor,
                    averageSalary = averageSalary,
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
            ResourceType.values().map { resourceType ->
                resourceType to ResourceQualityClass.values().map { resourceQualityClass ->
                    resourceQualityClass to 0.0
                }.toMap().toMutableMap()
            }.toMap()

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

                    tradeNeedMap.getValue(resourceType)[qualityClass] =
                        originalAmount + desireData.desireAmount
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
                                .getValue(resourceType).amount * resourceFactory.numBuilding,
                            targetQuality = resourceFactory.resourceFactoryInternalData.inputResourceMap
                                .getValue(resourceType).qualityData,
                            budget = budgetPerResource,
                            preferHighQualityClass = false
                        )
                    val originalAmount: Double =
                        tradeNeedMap.getValue(resourceType).getValue(qualityClass)

                    tradeNeedMap.getValue(resourceType)[qualityClass] = originalAmount +
                            resourceFactory.resourceFactoryInternalData.inputResourceMap
                                .getValue(resourceType).amount * resourceFactory.numBuilding
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

                    tradeNeedMap.getValue(resourceType)[qualityClass] =
                        originalAmount + playerSingleExport.amountPerTime
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

                    tradeNeedMap.getValue(resourceType)[qualityClass] =
                        originalAmount + popSingleExport.amountPerTime
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
     * @param needAvailableFactor determine how need and available amount should be compared
     * @param averageSalary the average salary of the population
     */
    @Suppress("SameParameterValue")
    private fun computeNewPrice(
        oldPrice: Double,
        amountAvailable: Double,
        amountNeeded: Double,
        needAvailableFactor: Double,
        averageSalary: Double,
    ): Double {
        val resourceRatio: Double = if (amountAvailable > 0.0) {
            amountNeeded * needAvailableFactor / amountAvailable
        } else {
            // Set the ratio to a very high value
            Double.MAX_VALUE * 1E-10
        }

        // Use log2 to reduce price change
        val priceChangeFactor: Double = when {
            resourceRatio > 1.0 -> {
                log2(resourceRatio + 1.0)
            }
            resourceRatio > 0.0 -> {
                1.0 / log2((1.0 / resourceRatio) + 1.0)
            }
            else -> {
                0.0
            }
        }

        // Maximum price and minimum price are determined by average salary of the population
        // Unevenly scaled since one pop need several resources
        val maxPrice: Double = averageSalary * 10.0
        val minPrice: Double = averageSalary * 0.01

        val newPrice: Double = oldPrice * priceChangeFactor

        when {
            oldPrice > maxPrice -> oldPrice - (oldPrice - maxPrice) * 0.2
            oldPrice < minPrice -> oldPrice + (minPrice - oldPrice) * 0.2
        }

        // Try to keep price within range, while not allowing a big instant change
        return if ((newPrice >= minPrice) && (newPrice <= maxPrice)) {
            newPrice
        } else {
            when {
                (newPrice > maxPrice) && (oldPrice > maxPrice) -> {
                    val priceReference: Double = min(newPrice, oldPrice)
                    priceReference - (priceReference - maxPrice) * 0.2
                }
                (newPrice < minPrice) && (oldPrice < minPrice) -> {
                    val priceReference: Double = max(newPrice, oldPrice)
                    priceReference + (minPrice - priceReference) * 0.2
                }
                else -> {
                    if (newPrice > maxPrice) {
                        maxPrice
                    } else {
                        minPrice
                    }
                }
            }
        }
    }
}