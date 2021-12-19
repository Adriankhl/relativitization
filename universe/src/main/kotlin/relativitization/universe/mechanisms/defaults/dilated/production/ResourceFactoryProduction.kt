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
                factory.ownerPlayerId == mutablePlayerData.playerId
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
                    factory.ownerPlayerId != mutablePlayerData.playerId
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
     */
    fun computeInputResourceQualityClassMap(
        mutableResourceFactoryData: MutableResourceFactoryData,
        resourceData: MutableResourceData,
    ): Map<ResourceType, ResourceQualityClass> {
        return mutableResourceFactoryData.resourceFactoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
            val requiredAmount: Double =
                inputResourceData.amount * mutableResourceFactoryData.resourceFactoryInternalData.maxOutputAmount * mutableResourceFactoryData.numBuilding
            val requiredQuality: MutableResourceQualityData =
                inputResourceData.qualityData
            val qualityClass: ResourceQualityClass = resourceData.productionQualityClass(
                type,
                requiredAmount,
                requiredQuality,
            )
            type to qualityClass
        }.toMap()
    }

    /**
     * How much productivity is used due to the limitation of resource
     *
     * @param mutableResourceFactoryData the factory producing this resource
     * @param inputResourceQualityClassMap the quality of the input resource
     * @param physicsData the physics data of the player which the factory is located
     * @param resourceData the amount of resource of the player which the factory is located
     * @param buyResource buy resource from the player
     */
    fun productAmountFraction(
        mutableResourceFactoryData: MutableResourceFactoryData,
        mutableLabourerPopData: MutableLabourerPopData,
        inputResourceQualityClassMap: Map<ResourceType, ResourceQualityClass>,
        physicsData: MutablePhysicsData,
        resourceData: MutableResourceData,
        buyResource: Boolean = false,
    ): Double {
        val inputFractionList: List<Double> =
            mutableResourceFactoryData.resourceFactoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
                val requiredAmount: Double =
                    inputResourceData.amount * mutableResourceFactoryData.resourceFactoryInternalData.maxOutputAmount * mutableResourceFactoryData.numBuilding
                val qualityClass: ResourceQualityClass = inputResourceQualityClassMap.getValue(type)
                resourceData.getProductionResourceAmount(type, qualityClass) / requiredAmount
            }

        val inputFraction: Double = inputFractionList.minOrNull() ?: 1.0

        val employeeFraction: Double = mutableResourceFactoryData.employeeFraction()

        val fuelFraction: Double =
            physicsData.fuelRestMassData.production / (mutableResourceFactoryData.resourceFactoryInternalData.fuelRestMassConsumptionRate * mutableResourceFactoryData.numBuilding)

        val buyResourceFraction: Double = if (buyResource) {
            val totalPrice: Double =
                mutableResourceFactoryData.resourceFactoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
                    val requiredAmount: Double =
                        inputResourceData.amount * mutableResourceFactoryData.resourceFactoryInternalData.maxOutputAmount * mutableResourceFactoryData.numBuilding
                    val qualityClass: ResourceQualityClass =
                        inputResourceQualityClassMap.getValue(type)

                    resourceData.getResourcePrice(type, qualityClass) * requiredAmount
                }.sumOf { it }

            (mutableResourceFactoryData.storedFuelRestMass - mutableResourceFactoryData.resourceFactoryInternalData.fuelRestMassConsumptionRate * mutableResourceFactoryData.numBuilding) / totalPrice
        } else {
            1.0
        }

        val minFaction: Double = max(
            listOf(
                1.0,
                inputFraction,
                employeeFraction,
                fuelFraction,
                buyResourceFraction,
            ).minOf { it },
            0.0
        )

        // Modifier by education level
        val educationLevel: Double = (mutableLabourerPopData.commonPopData.educationLevel)
        val educationLevelMultiplier: Double = when {
            educationLevel > 1.0 -> 1.0
            educationLevel < 0.0 -> 0.0
            else -> educationLevel
        }

        // Prevent smaller than zero
        return max(minFaction, 0.0) * educationLevelMultiplier
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

        val r2: Double = if (
            (actualQuality.quality2 < requiredQuality.quality2) && (requiredQuality.quality2 > 0.0)
        ) {
            actualQuality.quality2 / requiredQuality.quality2
        } else {
            1.0
        }

        val r3: Double = if (
            (actualQuality.quality3 < requiredQuality.quality3) && (requiredQuality.quality3 > 0.0)
        ) {
            actualQuality.quality3 / requiredQuality.quality3
        } else {
            1.0
        }

        return (r1 + r2 + r3) / 3.0
    }

    /**
     * The quality of the output product
     *
     * @param mutableResourceFactoryData the factory producing this resource
     * @param resourceData the amount of resource owned by the player
     */
    fun productQuality(
        mutableResourceFactoryData: MutableResourceFactoryData,
        inputResourceQualityClassMap: Map<ResourceType, ResourceQualityClass>,
        resourceData: MutableResourceData,
    ): MutableResourceQualityData {
        val fractionList: List<Double> =
            mutableResourceFactoryData.resourceFactoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
                val requiredQuality: MutableResourceQualityData =
                    inputResourceData.qualityData
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

        return mutableResourceFactoryData.resourceFactoryInternalData.maxOutputResourceQualityData * avgFraction
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
            )

        val amountFraction: Double = productAmountFraction(
            mutableResourceFactoryData,
            mutableLabourerPopData,
            qualityClassMap,
            physicsData,
            resourceData,
        )

        val outputQuality: MutableResourceQualityData =
            productQuality(mutableResourceFactoryData, qualityClassMap, resourceData)
        mutableResourceFactoryData.lastOutputQuality = outputQuality


        // Consume resource
        mutableResourceFactoryData.resourceFactoryInternalData.inputResourceMap.forEach { (type, inputResourceData) ->
            val requiredAmount: Double =
                inputResourceData.amount * mutableResourceFactoryData.resourceFactoryInternalData.maxOutputAmount * mutableResourceFactoryData.numBuilding
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
        physicsData.fuelRestMassData.production -= mutableResourceFactoryData.resourceFactoryInternalData.fuelRestMassConsumptionRate * amountFraction * mutableResourceFactoryData.numBuilding

        // output amount
        val outputAmount: Double =
            mutableResourceFactoryData.resourceFactoryInternalData.maxOutputAmount * amountFraction * mutableResourceFactoryData.numBuilding
        mutableResourceFactoryData.lastOutputAmount = outputAmount

        // Produce resource
        resourceData.addNewResource(
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
            )

        val amountFraction: Double = productAmountFraction(
            mutableResourceFactoryData,
            mutableLabourerPopData,
            qualityClassMap,
            physicsData,
            resourceData,
            true
        )
        val outputQuality: MutableResourceQualityData =
            productQuality(mutableResourceFactoryData, qualityClassMap, resourceData)
        mutableResourceFactoryData.lastOutputQuality = outputQuality

        // Pay price
        val price: Double =
            mutableResourceFactoryData.resourceFactoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
                val requiredAmount: Double =
                    inputResourceData.amount * mutableResourceFactoryData.resourceFactoryInternalData.maxOutputAmount * mutableResourceFactoryData.numBuilding
                val qualityClass: ResourceQualityClass = qualityClassMap.getValue(type)

                resourceData.getResourcePrice(type, qualityClass) * requiredAmount
            }.sumOf { it } * amountFraction

        mutableResourceFactoryData.storedFuelRestMass -= price
        mutablePlayerData.playerInternalData.physicsData().addNewFuel(price)


        // Consume resource
        mutableResourceFactoryData.resourceFactoryInternalData.inputResourceMap.forEach { (type, inputResourceData) ->
            val requiredAmount: Double =
                inputResourceData.amount * mutableResourceFactoryData.resourceFactoryInternalData.maxOutputAmount * mutableResourceFactoryData.numBuilding
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
        physicsData.fuelRestMassData.production -= mutableResourceFactoryData.resourceFactoryInternalData.fuelRestMassConsumptionRate * amountFraction * mutableResourceFactoryData.numBuilding

        // output amount
        val outputAmount: Double =
            mutableResourceFactoryData.resourceFactoryInternalData.maxOutputAmount * amountFraction * mutableResourceFactoryData.numBuilding
        mutableResourceFactoryData.lastOutputAmount = outputAmount


        return SendResourceCommand(
            toId = toId,
            fromId = mutablePlayerData.playerId,
            fromInt4D = mutablePlayerData.int4D.toInt4D(),
            resourceType = mutableResourceFactoryData.resourceFactoryInternalData.outputResource,
            resourceQualityData = outputQuality.toResourceQualityData(),
            amount = outputAmount,
            senderResourceLossFractionPerDistance = mutablePlayerData.playerInternalData.playerScienceData().playerScienceApplicationData.resourceLogisticsLossFractionPerDistance,
        )
    }
}