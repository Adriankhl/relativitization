package relativitization.universe.mechanisms.defaults.dilated.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.PopBuyResourceCommand
import relativitization.universe.data.components.*
import relativitization.universe.data.components.defaults.economy.*
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.algebra.Logistic
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.sampling.WeightedReservoir
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.min
import kotlin.math.pow

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
            .carrierDataMap.map { (carrierId, mutableCarrierData) ->
                PopType.values().map { popType ->
                    val commonPopData = mutableCarrierData.allPopData.getCommonPopData(popType)

                    // available saving for this resource, compute before buying resource
                    val numDesire: Int = min(commonPopData.desireResourceMap.size, 1)
                    val availableFuel: Double = commonPopData.saving / numDesire

                    commonPopData.desireResourceMap.keys.map { resourceType ->
                        buyResource(
                            resourceType = resourceType,
                            availableFuel = availableFuel,
                            carrierId = carrierId,
                            popType = popType,
                            commonPopData = commonPopData,
                            playerId = mutablePlayerData.playerId,
                            int4D = mutablePlayerData.int4D.toInt4D(),
                            topLeaderId = mutablePlayerData.topLeaderId(),
                            physicsData = mutablePlayerData.playerInternalData.physicsData(),
                            economyData = mutablePlayerData.playerInternalData.economyData(),
                            playerScienceData = mutablePlayerData.playerInternalData.playerScienceData(),
                            neighborList = neighborList,
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
        availableFuel: Double,
        carrierId: Int,
        popType: PopType,
        commonPopData: MutableCommonPopData,
        playerId: Int,
        int4D: Int4D,
        topLeaderId: Int,
        physicsData: MutablePhysicsData,
        economyData: MutableEconomyData,
        playerScienceData: MutablePlayerScienceData,
        neighborList: List<PlayerData>,
    ): List<PopBuyResourceCommand> {
        // Desire of this resource
        val desireAmount: Double =
            commonPopData.desireResourceMap.getValue(resourceType).desireAmount
        val desireQuality: MutableResourceQualityData =
            commonPopData.desireResourceMap.getValue(resourceType).desireQuality

        val mustBuyFromThisPlayer: Boolean = mustBuyFromThisPlayer(
            resourceType = resourceType,
            desireAmount = desireAmount,
            desireQualityData = desireQuality,
            availableFuel = availableFuel,
            satisfaction = commonPopData.satisfaction,
            economyData = economyData,
        )

        return if (mustBuyFromThisPlayer || neighborList.isEmpty()) {
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
            val neighborScoreMap: Map<PlayerData, Double> = neighborList.associateWith {
                buyResourcePlayerScore(
                    resourceType = resourceType,
                    desireAmount = desireAmount,
                    desireQualityData = desireQuality,
                    availableFuel = availableFuel,
                    thisInt4D = int4D,
                    thisTopPlayerId = topLeaderId,
                    thisPlayerScienceData = playerScienceData,
                    thisTaxRateData = economyData.taxData.taxRateData,
                    otherInt4D = it.int4D,
                    otherTopPlayerId = it.topLeaderId(),
                    otherPlayerScienceData = it.playerInternalData.playerScienceData(),
                    otherEconomyData = it.playerInternalData.economyData(),
                )
            }

            val (otherPlayer, otherScore) = neighborScoreMap.maxByOrNull { it.value }!!

            val thisScore: Double = buyResourceThisPlayerScore(
                resourceType = resourceType,
                desireAmount = desireAmount,
                desireQualityData = desireQuality,
                availableFuel = availableFuel,
                thisEconomyData = economyData,
            )

            if (thisScore >= otherScore) {
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
                listOf(
                    computePopBuyResourceCommand(
                        resourceType = resourceType,
                        desireAmount = desireAmount,
                        desireQualityData = desireQuality,
                        availableFuel = availableFuel,
                        carrierId = carrierId,
                        popType = popType,
                        commonPopData = commonPopData,
                        thisPlayerId = playerId,
                        thisInt4D = int4D,
                        thisTopPlayerId = topLeaderId,
                        thisEconomyData = economyData,
                        thisPlayerScienceData = playerScienceData,
                        otherPlayerData = otherPlayer,
                    )
                )
            }
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
     * A score to rank player to buy resource from
     */
    fun buyResourcePlayerScore(
        resourceType: ResourceType,
        desireAmount: Double,
        desireQualityData: MutableResourceQualityData,
        availableFuel: Double,
        thisInt4D: Int4D,
        thisTopPlayerId: Int,
        thisPlayerScienceData: MutablePlayerScienceData,
        thisTaxRateData: MutableTaxRateData,
        otherInt4D: Int4D,
        otherTopPlayerId: Int,
        otherPlayerScienceData: PlayerScienceData,
        otherEconomyData: EconomyData,
    ): Double {
        val distance: Int = Intervals.intDistance(thisInt4D, otherInt4D)
        val fuelLossFractionPerDistance: Double =
            (thisPlayerScienceData.playerScienceApplicationData.fuelLogisticsLossFractionPerDistance +
                    otherPlayerScienceData.playerScienceApplicationData
                        .fuelLogisticsLossFractionPerDistance) * 0.5
        val resourceLossFractionPerDistance: Double =
            (thisPlayerScienceData.playerScienceApplicationData.resourceLogisticsLossFractionPerDistance +
                    otherPlayerScienceData.playerScienceApplicationData
                        .resourceLogisticsLossFractionPerDistance) * 0.5

        val sameTopLeaderId: Boolean = thisTopPlayerId == otherTopPlayerId

        val importTariffFactor: Double = if (sameTopLeaderId) {
            1.0
        } else {
            1.0 + thisTaxRateData.importTariff.getResourceTariffRate(
                otherTopPlayerId,
                resourceType,
            )
        }

        val exportTariffFactor: Double = if (sameTopLeaderId) {
            1.0
        } else {
            1.0 + otherEconomyData.taxData.taxRateData.exportTariff.getResourceTariffRate(
                thisTopPlayerId,
                resourceType,
            )
        }

        val fuelRemainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - fuelLossFractionPerDistance).pow(distance)
        }

        val resourceRemainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - resourceLossFractionPerDistance).pow(distance)
        }

        // Desire adjusted by logistic loss
        val actualDesireAmount: Double = desireAmount / resourceRemainFraction

        val qualityClass: ResourceQualityClass = otherEconomyData.resourceData.tradeQualityClass(
            resourceType = resourceType,
            amount = actualDesireAmount,
            targetQuality = desireQualityData.toResourceQualityData(),
            budget = availableFuel,
            preferHighQualityClass = true,
            tariffFactor = importTariffFactor * exportTariffFactor,
        )

        val availableAmount: Double = otherEconomyData.resourceData.getTradeResourceAmount(
            resourceType,
            qualityClass
        )

        val resourceQuality: ResourceQualityData = otherEconomyData.resourceData
            .getResourceQuality(resourceType, qualityClass)

        val price: Double = otherEconomyData.resourceData.getResourcePrice(
            resourceType,
            qualityClass
        )

        // price adjusted by logistic loss and tariff
        val actualPrice: Double = price * exportTariffFactor * importTariffFactor /
                fuelRemainFraction

        val isResourceSufficient: Boolean = availableAmount >= actualDesireAmount
        val isFuelSufficient: Boolean = availableFuel >= actualPrice * actualDesireAmount

        val sufficientResourceScore: Double = if (isResourceSufficient) {
            100.0
        } else {
            0.0
        }

        val sufficientFuelScore: Double = if (isFuelSufficient) {
            100.0
        } else {
            0.0
        }

        val resourceQualityScore: Double =
            Logistic.standardLogistic(resourceQuality.quality * 0.001)

        return sufficientResourceScore + sufficientFuelScore + resourceQualityScore
    }

    /**
     * A score to rank (this) player to buy resource from
     */
    fun buyResourceThisPlayerScore(
        resourceType: ResourceType,
        desireAmount: Double,
        desireQualityData: MutableResourceQualityData,
        availableFuel: Double,
        thisEconomyData: MutableEconomyData,
    ): Double {
        val qualityClass: ResourceQualityClass = thisEconomyData.resourceData.tradeQualityClass(
            resourceType = resourceType,
            amount = desireAmount,
            targetQuality = desireQualityData,
            budget = availableFuel,
            preferHighQualityClass = true,
        )

        val availableAmount: Double = thisEconomyData.resourceData.getTradeResourceAmount(
            resourceType,
            qualityClass
        )

        val resourceQuality: MutableResourceQualityData = thisEconomyData.resourceData
            .getResourceQuality(resourceType, qualityClass)

        val price: Double = thisEconomyData.resourceData.getResourcePrice(
            resourceType,
            qualityClass
        )

        val isResourceSufficient: Boolean = availableAmount >= desireAmount
        val isFuelSufficient: Boolean = availableFuel >= price * desireAmount

        val sufficientResourceScore: Double = if (isResourceSufficient) {
            100.0
        } else {
            0.0
        }

        val sufficientFuelScore: Double = if (isFuelSufficient) {
            100.0
        } else {
            0.0
        }

        val resourceQualityScore: Double =
            Logistic.standardLogistic(resourceQuality.quality * 0.001)

        return sufficientResourceScore + sufficientFuelScore + resourceQualityScore
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

        val idealTotalPrice: Double = economyData.resourceData.getResourcePrice(
            resourceType,
            qualityClass
        ) * idealAmountToBuy

        // If saving is smaller than total price, only buy a fraction
        val priceFraction: Double = if (idealTotalPrice > 0.0) {
            min(commonPopData.saving / idealTotalPrice, 1.0)
        } else {
            1.0
        }

        val amountToBuy: Double = idealAmountToBuy * priceFraction
        val totalPriceToPay: Double = idealTotalPrice * amountToBuy

        economyData.resourceData.getResourceAmountData(
            resourceType,
            qualityClass,
        ).trade -= amountToBuy

        commonPopData.addDesireResource(
            resourceType,
            resourceQuality,
            amountToBuy,
        )
        commonPopData.saving -= totalPriceToPay
        physicsData.addInternalFuel(totalPriceToPay)
    }

    fun computePopBuyResourceCommand(
        resourceType: ResourceType,
        desireAmount: Double,
        desireQualityData: MutableResourceQualityData,
        availableFuel: Double,
        carrierId: Int,
        popType: PopType,
        commonPopData: MutableCommonPopData,
        thisPlayerId: Int,
        thisInt4D: Int4D,
        thisTopPlayerId: Int,
        thisEconomyData: MutableEconomyData,
        thisPlayerScienceData: MutablePlayerScienceData,
        otherPlayerData: PlayerData,
    ): PopBuyResourceCommand {

        val distance: Int = Intervals.intDistance(thisInt4D, otherPlayerData.int4D)
        val fuelLossFractionPerDistance: Double =
            (thisPlayerScienceData.playerScienceApplicationData.fuelLogisticsLossFractionPerDistance +
                    otherPlayerData.playerInternalData.playerScienceData().playerScienceApplicationData
                        .fuelLogisticsLossFractionPerDistance) * 0.5
        val resourceLossFractionPerDistance: Double =
            (thisPlayerScienceData.playerScienceApplicationData.resourceLogisticsLossFractionPerDistance +
                    otherPlayerData.playerInternalData.playerScienceData().playerScienceApplicationData
                        .resourceLogisticsLossFractionPerDistance) * 0.5

        val sameTopLeaderId: Boolean = thisTopPlayerId == otherPlayerData.topLeaderId()

        val importTariffFactor: Double = if (sameTopLeaderId) {
            1.0
        } else {
            1.0 + thisEconomyData.taxData.taxRateData.importTariff.getResourceTariffRate(
                otherPlayerData.topLeaderId(),
                resourceType,
            )
        }

        val exportTariffFactor: Double = if (sameTopLeaderId) {
            1.0
        } else {
            1.0 + otherPlayerData.playerInternalData.economyData().taxData.taxRateData.exportTariff
                .getResourceTariffRate(
                    thisTopPlayerId,
                    resourceType,
                )
        }

        val fuelRemainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - fuelLossFractionPerDistance).pow(distance)
        }

        val resourceRemainFraction: Double = if (distance <= Intervals.sameCubeIntDistance()) {
            1.0
        } else {
            (1.0 - resourceLossFractionPerDistance).pow(distance)
        }

        // Desire adjusted by logistic loss
        val actualDesireAmount: Double = desireAmount / resourceRemainFraction

        val qualityClass: ResourceQualityClass = thisEconomyData.resourceData.tradeQualityClass(
            resourceType = resourceType,
            amount = actualDesireAmount,
            targetQuality = desireQualityData,
            budget = availableFuel,
            preferHighQualityClass = true,
            tariffFactor = importTariffFactor * exportTariffFactor,
        )

        // price adjusted by logistic loss and tariff
        val idealTotalPrice: Double = thisEconomyData.resourceData.getResourcePrice(
            resourceType,
            qualityClass
        ) * actualDesireAmount * exportTariffFactor * importTariffFactor / fuelRemainFraction

        val totalPriceToPay: Double = min(idealTotalPrice, availableFuel)

        // Pick carrier id by service pop
        val targetCarrierId: Int = WeightedReservoir.aRes(
            1,
            otherPlayerData.playerInternalData.popSystemData().carrierDataMap.keys.toList(),
        ) {
            otherPlayerData.playerInternalData.popSystemData().carrierDataMap.getValue(it)
                .allPopData.servicePopData.commonPopData.adultPopulation
        }.first()

        // pay the price
        commonPopData.saving -= totalPriceToPay

        // add import tariff
        thisEconomyData.taxData.storedFuelRestMass += totalPriceToPay *
                (importTariffFactor - 1.0) / importTariffFactor

        return PopBuyResourceCommand(
            toId = otherPlayerData.playerId,
            fromId = thisPlayerId,
            fromInt4D = thisInt4D,
            fromCarrierId = carrierId,
            fromPopType = popType,
            targetTopLeaderId = otherPlayerData.topLeaderId(),
            targetCarrierId = targetCarrierId,
            resourceType = resourceType,
            resourceQualityClass = qualityClass,
            fuelRestMassAmount = totalPriceToPay / importTariffFactor,
            amountPerTime = actualDesireAmount,
            senderFuelLossFractionPerDistance = thisPlayerScienceData.playerScienceApplicationData
                .fuelLogisticsLossFractionPerDistance,
        )
    }
}