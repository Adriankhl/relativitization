package relativitization.universe.mechanisms.defaults.dilated.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.SendResourceCommand
import relativitization.universe.data.components.MutablePhysicsData
import relativitization.universe.data.components.defaults.economy.MutableResourceData
import relativitization.universe.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.economy.ResourceType
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.MutableLabourerPopData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableInputResourceData
import relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory.MutableResourceFactoryData
import relativitization.universe.data.components.economyData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.max

/**
 * Produce resources, fuel needs special treatments
 */
object ResourceFactoryProduction : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        // Do self factory production first
        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
            carrier.allPopData.labourerPopData.resourceFactoryMap.values.filter { factory ->
                (factory.ownerPlayerId == mutablePlayerData.playerId) && (factory.isOpened)
            }.forEach { factory ->
                updateResourceData(
                    factory,
                    carrier.allPopData.labourerPopData,
                    mutablePlayerData.playerInternalData.economyData().resourceData,
                    mutablePlayerData.playerInternalData.physicsData(),
                )
            }
        }

        // Production by factory owned by other
        val logisticCommandList: List<Command> =
            mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map { carrier ->
                carrier.allPopData.labourerPopData.resourceFactoryMap.values.filter { factory ->
                    (factory.ownerPlayerId != mutablePlayerData.playerId) && (factory.isOpened)
                }.map { factory ->
                    computeSendResourceCommand(
                        factory,
                        carrier.allPopData.labourerPopData,
                        mutablePlayerData,
                    )
                }
            }.flatten()



        return logisticCommandList
    }

    /**
     * Compute input resource quality class
     *
     * @param resourceFactoryData the factory data
     * @param resourceData the resource date in the economy data
     * @param buyResource buy resource from the player
     */
    fun computeInputResourceQualityClassMap(
        resourceFactoryData: MutableResourceFactoryData,
        resourceData: MutableResourceData,
        buyResource: Boolean,
    ): Map<ResourceType, ResourceQualityClass> {
        val numInputResource: Int = resourceFactoryData.resourceFactoryInternalData
            .inputResourceMap.size

        return if (numInputResource > 0) {
            resourceFactoryData.resourceFactoryInternalData.inputResourceMap.map { (type, inputResourceData) ->

                val requiredAmount: Double = inputResourceData.amount *
                        resourceFactoryData.resourceFactoryInternalData.maxOutputAmountPerEmployee *
                        resourceFactoryData.maxNumEmployee *
                        resourceFactoryData.employeeFraction()

                val requiredQuality: MutableResourceQualityData =
                    inputResourceData.qualityData

                val qualityClass: ResourceQualityClass = if (buyResource) {
                    val maxFuelConsumption: Double =
                            resourceFactoryData.resourceFactoryInternalData.fuelRestMassConsumptionRatePerEmployee *
                                    resourceFactoryData.maxNumEmployee *
                                    resourceFactoryData.employeeFraction()

                    // Approximate fuel per resource by taking the average
                    val availableFuelPerResource: Double =
                        (resourceFactoryData.storedFuelRestMass - maxFuelConsumption) / numInputResource

                    resourceData.tradeQualityClass(
                        resourceType = type,
                        amount = requiredAmount,
                        targetQuality = requiredQuality,
                        budget = availableFuelPerResource,
                        preferHighQualityClass = false,
                    )
                } else {
                    resourceData.productionQualityClass(
                        resourceType = type,
                        amount = requiredAmount,
                        targetQuality = requiredQuality,
                        preferHighQualityClass = false
                    )
                }
                type to qualityClass
            }.toMap()
        } else {
            mapOf()
        }
    }

    /**
     * How much productivity is used due to the limitation of resource
     *
     * @param resourceFactoryData the factory producing this resource
     * @param inputResourceQualityClassMap the quality of the input resource
     * @param physicsData the physics data of the player which the factory is located
     * @param resourceData the amount of resource of the player which the factory is located
     * @param buyResource buy resource from the player
     */
    fun productAmountFraction(
        resourceFactoryData: MutableResourceFactoryData,
        inputResourceQualityClassMap: Map<ResourceType, ResourceQualityClass>,
        physicsData: MutablePhysicsData,
        resourceData: MutableResourceData,
        buyResource: Boolean,
    ): Double {
        val employeeFraction: Double = resourceFactoryData.employeeFraction()

        val inputFractionList: List<Double> = if (buyResource) {
            resourceFactoryData.resourceFactoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
                // Don't adjust by employee fraction, it as already considered
                val requiredAmount: Double = inputResourceData.amount *
                        resourceFactoryData.resourceFactoryInternalData.maxOutputAmountPerEmployee *
                        resourceFactoryData.maxNumEmployee

                val qualityClass: ResourceQualityClass = inputResourceQualityClassMap.getValue(type)

                if (requiredAmount > 0.0) {
                    resourceData.getTradeResourceAmount(type, qualityClass) / requiredAmount
                } else {
                    1.0
                }
            }
        } else {
            resourceFactoryData.resourceFactoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
                // Don't adjust by employee fraction, it as already considered
                val requiredAmount: Double = inputResourceData.amount *
                        resourceFactoryData.resourceFactoryInternalData.maxOutputAmountPerEmployee *
                        resourceFactoryData.maxNumEmployee

                val qualityClass: ResourceQualityClass = inputResourceQualityClassMap.getValue(type)

                if (requiredAmount > 0.0) {
                    resourceData.getProductionResourceAmount(type, qualityClass) / requiredAmount
                } else {
                    1.0
                }
            }
        }

        val inputFraction: Double = inputFractionList.minOrNull() ?: 1.0

        val fuelFraction: Double = if (buyResource) {
            val totalResourcePrice: Double = resourceFactoryData.resourceFactoryInternalData
                .inputResourceMap.keys.fold(0.0) { acc, resourceType ->
                    val amount: Double = resourceFactoryData.resourceFactoryInternalData
                        .inputResourceMap.getValue(resourceType).amount

                    val price: Double = resourceData.getResourcePrice(
                        resourceType,
                        inputResourceQualityClassMap.getValue(resourceType)
                    )

                    acc + amount * price
                }

            // Don't adjust by employee fraction, it as already considered
            val totalPrice: Double = totalResourcePrice +
                    resourceFactoryData.resourceFactoryInternalData.fuelRestMassConsumptionRatePerEmployee *
                    resourceFactoryData.maxNumEmployee

            if (totalPrice > 0.0) {
                resourceFactoryData.storedFuelRestMass / totalPrice
            } else {
                1.0
            }
        } else {
            // Don't adjust by employee fraction, it as already considered
            val totalPrice: Double =
                    resourceFactoryData.resourceFactoryInternalData.fuelRestMassConsumptionRatePerEmployee *
                            resourceFactoryData.maxNumEmployee

            if (totalPrice > 0.0) {
                physicsData.fuelRestMassData.production / totalPrice
            } else {
                1.0
            }
        }

        val minFaction: Double = max(
            listOf(
                1.0,
                inputFraction,
                employeeFraction,
                fuelFraction,
            ).minOf { it },
            0.0
        )


        // Prevent smaller than zero
        return max(minFaction, 0.0)
    }

    /**
     * Compute the reduced quality if the input quality is lower than required
     */
    fun qualityReducedFaction(
        requiredQuality: MutableResourceQualityData,
        actualQuality: MutableResourceQualityData
    ): Double {
        val r1: Double = if (
            (actualQuality.quality1 < requiredQuality.quality1) && (requiredQuality.quality1 > 0.0)
        ) {
            actualQuality.quality1 / requiredQuality.quality1
        } else {
            1.0
        }

        return r1
    }

    /**
     * The quality of the output product
     *
     * @param resourceFactoryData the factory producing this resource
     * @param resourceData the amount of resource owned by the player
     */
    fun productQuality(
        resourceFactoryData: MutableResourceFactoryData,
        labourerPopData: MutableLabourerPopData,
        inputResourceQualityClassMap: Map<ResourceType, ResourceQualityClass>,
        resourceData: MutableResourceData,
    ): MutableResourceQualityData {
        val fractionList: List<Double> =
            resourceFactoryData.resourceFactoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
                val requiredQuality: MutableResourceQualityData = inputResourceData.qualityData

                val qualityClass: ResourceQualityClass = inputResourceQualityClassMap.getValue(type)
                qualityReducedFaction(
                    requiredQuality,
                    resourceData.getResourceQuality(type, qualityClass)
                )
            }

        val avgFraction: Double = if (fractionList.isEmpty()) {
            1.0
        } else {
            fractionList.average()
        }

        // Modifier by education level, range from 0.5 to 1.5
        val educationLevel: Double = (labourerPopData.commonPopData.educationLevel)
        val educationLevelMultiplier: Double = when {
            educationLevel > 1.0 -> 1.5
            educationLevel < 0.0 -> 0.5
            else -> educationLevel + 0.5
        }

        return resourceFactoryData.resourceFactoryInternalData.maxOutputResourceQualityData *
                avgFraction * educationLevelMultiplier
    }

    /**
     * Consume and produce resource
     *
     * @param mutableResourceFactoryData the factory producing this resource
     * @param resourceData the amount of resource owned by the player
     * @param physicsData physics data of the player
     */
    fun updateResourceData(
        mutableResourceFactoryData: MutableResourceFactoryData,
        mutableLabourerPopData: MutableLabourerPopData,
        resourceData: MutableResourceData,
        physicsData: MutablePhysicsData,
    ) {
        // Clear last input amount map
        mutableResourceFactoryData.lastInputResourceMap.clear()

        val qualityClassMap: Map<ResourceType, ResourceQualityClass> =
            computeInputResourceQualityClassMap(
                mutableResourceFactoryData,
                resourceData,
                false
            )

        val amountFraction: Double = productAmountFraction(
            mutableResourceFactoryData,
            qualityClassMap,
            physicsData,
            resourceData,
            false
        )

        // Output quality
        val outputQuality: MutableResourceQualityData = productQuality(
            mutableResourceFactoryData,
            mutableLabourerPopData,
            qualityClassMap,
            resourceData
        )
        mutableResourceFactoryData.lastOutputQuality = outputQuality

        // Output amount
        val outputAmount: Double = amountFraction * mutableResourceFactoryData.resourceFactoryInternalData
            .maxOutputAmountPerEmployee * mutableResourceFactoryData.maxNumEmployee
        mutableResourceFactoryData.lastOutputAmount = outputAmount

        // Consume resource
        mutableResourceFactoryData.resourceFactoryInternalData.inputResourceMap.forEach { (type, inputResourceData) ->
            val requiredAmount: Double = inputResourceData.amount *
                    mutableResourceFactoryData.resourceFactoryInternalData.maxOutputAmountPerEmployee *
                    mutableResourceFactoryData.maxNumEmployee
            val qualityClass: ResourceQualityClass = qualityClassMap.getValue(type)

            val inputAmount: Double = requiredAmount * amountFraction

            // Record input resource
            mutableResourceFactoryData.lastInputResourceMap[type] = MutableInputResourceData(
                qualityData = resourceData.getResourceQuality(type, qualityClass),
                amount = inputAmount,
            )

            resourceData.getResourceAmountData(
                type,
                qualityClass
            ).production -= inputAmount
        }

        // Consume fuel
        physicsData.fuelRestMassData.production -= amountFraction *
                mutableResourceFactoryData.resourceFactoryInternalData.fuelRestMassConsumptionRatePerEmployee *
                mutableResourceFactoryData.maxNumEmployee

        // Produce and add resource
        resourceData.addResource(
            mutableResourceFactoryData.resourceFactoryInternalData.outputResource,
            outputQuality,
            outputAmount
        )
    }

    /**
     * Produce resource and send resource to owner
     *
     * @param mutableResourceFactoryData the factory to produce resource
     * @param mutablePlayerData the player of that factory
     */
    fun computeSendResourceCommand(
        mutableResourceFactoryData: MutableResourceFactoryData,
        mutableLabourerPopData: MutableLabourerPopData,
        mutablePlayerData: MutablePlayerData,
    ): Command {
        // Clear last input amount map
        mutableResourceFactoryData.lastInputResourceMap.clear()

        val toId: Int = mutableResourceFactoryData.ownerPlayerId
        val resourceData = mutablePlayerData.playerInternalData.economyData().resourceData
        val physicsData = mutablePlayerData.playerInternalData.physicsData()

        val qualityClassMap: Map<ResourceType, ResourceQualityClass> =
            computeInputResourceQualityClassMap(
                mutableResourceFactoryData,
                resourceData,
                true
            )

        val amountFraction: Double = productAmountFraction(
            mutableResourceFactoryData,
            qualityClassMap,
            physicsData,
            resourceData,
            true
        )

        // Output quality
        val outputQuality: MutableResourceQualityData = productQuality(
            mutableResourceFactoryData,
            mutableLabourerPopData,
            qualityClassMap,
            resourceData
        )
        mutableResourceFactoryData.lastOutputQuality = outputQuality

        // Output amount
        val outputAmount: Double = amountFraction *
                mutableResourceFactoryData.resourceFactoryInternalData.maxOutputAmountPerEmployee *
                mutableResourceFactoryData.maxNumEmployee
        mutableResourceFactoryData.lastOutputAmount = outputAmount

        // Pay price
        val price: Double = mutableResourceFactoryData.resourceFactoryInternalData.inputResourceMap
            .map { (type, inputResourceData) ->
                val requiredAmount: Double = inputResourceData.amount *
                        mutableResourceFactoryData.resourceFactoryInternalData.maxOutputAmountPerEmployee *
                        mutableResourceFactoryData.maxNumEmployee
                val qualityClass: ResourceQualityClass = qualityClassMap.getValue(type)

                resourceData.getResourcePrice(type, qualityClass) * requiredAmount
            }.sumOf { it } * amountFraction

        mutableResourceFactoryData.storedFuelRestMass -= price
        mutablePlayerData.playerInternalData.physicsData().addFuel(price)


        // Consume resource
        mutableResourceFactoryData.resourceFactoryInternalData.inputResourceMap.forEach { (type, inputResourceData) ->
            val requiredAmount: Double = inputResourceData.amount *
                    mutableResourceFactoryData.resourceFactoryInternalData.maxOutputAmountPerEmployee *
                    mutableResourceFactoryData.maxNumEmployee

            val qualityClass: ResourceQualityClass = qualityClassMap.getValue(type)

            val inputAmount: Double = requiredAmount * amountFraction

            // Record input resource
            mutableResourceFactoryData.lastInputResourceMap[type] = MutableInputResourceData(
                qualityData = resourceData.getResourceQuality(type, qualityClass),
                amount = inputAmount,
            )

            resourceData.getResourceAmountData(
                type,
                qualityClass
            ).trade -= inputAmount
        }

        // Consume fuel
        mutableResourceFactoryData.storedFuelRestMass -= amountFraction *
                mutableResourceFactoryData.resourceFactoryInternalData.fuelRestMassConsumptionRatePerEmployee *
                mutableResourceFactoryData.maxNumEmployee


        return SendResourceCommand(
            toId = toId,
            fromId = mutablePlayerData.playerId,
            fromInt4D = mutablePlayerData.int4D.toInt4D(),
            resourceType = mutableResourceFactoryData.resourceFactoryInternalData.outputResource,
            resourceQualityData = outputQuality.toResourceQualityData(),
            amount = outputAmount,
            senderResourceLossFractionPerDistance = mutablePlayerData.playerInternalData
                .playerScienceData().playerScienceApplicationData
                .resourceLogisticsLossFractionPerDistance,
        )
    }
}