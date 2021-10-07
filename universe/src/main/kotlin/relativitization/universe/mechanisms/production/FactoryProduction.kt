package relativitization.universe.mechanisms.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.component.MutablePhysicsData
import relativitization.universe.data.component.economy.*
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryData
import relativitization.universe.data.UniverseScienceData
import relativitization.universe.data.commands.SendFuelCommand
import relativitization.universe.data.commands.SendResourceCommand
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism

/**
 * Produce resources, fuel needs special treatments
 */
object FactoryProduction : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeScienceData: UniverseScienceData
    ): List<Command> {
        val gamma: Double = Relativistic.gamma(
            mutablePlayerData.velocity.toVelocity(),
            universeSettings.speedOfLight
        )

        // Clean up fuel in resource data
        mutablePlayerData.playerInternalData.economyData().resourceData.removeFuel()

        // Do self factory production first
        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
            carrier.allPopData.labourerPopData.factoryMap.values.filter { factory ->
                factory.ownerPlayerId == mutablePlayerData.playerId
            }.forEach { factory ->
                updateResourceData(
                    factory,
                    mutablePlayerData.playerInternalData.economyData().resourceData,
                    mutablePlayerData.playerInternalData.physicsData(),
                    gamma,
                )
            }
        }


        // Store fuel to physics data
        if (mutablePlayerData.playerInternalData.modifierData(
            ).physicsModifierData.disableRestMassIncreaseTimeLimit <= 0
        ) {
            mutablePlayerData.playerInternalData.physicsData().addFuel(
                mutablePlayerData.playerInternalData.economyData().resourceData.getFuelAmount()
            )
        }

        // Clean up fuel in resource data
        mutablePlayerData.playerInternalData.economyData().resourceData.removeFuel()

        // Production by factory owned by other
        val logisticCommandList: List<Command> =
            mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map { carrier ->
                carrier.allPopData.labourerPopData.factoryMap.values.filter { factory ->
                    factory.ownerPlayerId != mutablePlayerData.playerId
                }.map { factory ->
                    computeSendResourceCommand(
                        factory,
                        mutablePlayerData,
                        gamma
                    )
                }
            }.flatten()



        return logisticCommandList
    }

    /**
     * Compute input resource quality class
     */
    fun computeInputResourceQualityClassMap(
        mutableFactoryData: MutableFactoryData,
        resourceData: MutableResourceData,
        gamma: Double,
    ): Map<ResourceType, ResourceQualityClass> {
        return mutableFactoryData.factoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
            val requiredAmount: Double =
                inputResourceData.amountPerOutputUnit * mutableFactoryData.factoryInternalData.maxOutputAmount * mutableFactoryData.numBuilding / gamma
            val requiredQuality: MutableResourceQualityData =
                inputResourceData.maxInputResourceQualityData
            val qualityClass: ResourceQualityClass = resourceData.productionQualityClass(
                type,
                requiredAmount,
                requiredQuality
            )
            type to qualityClass
        }.toMap()
    }

    /**
     * How much productivity is used due to the limitation of resource
     *
     * @param mutableFactoryData the factory producing this resource
     * @param inputResourceQualityClassMap the quality of the input resource
     * @param physicsData the physics data of the player which the factory is located
     * @param resourceData the amount of resource of the player which the factory is located
     * @param gamma Lorentz factor
     * @param buyResource buy resource from the player
     */
    fun productAmountFraction(
        mutableFactoryData: MutableFactoryData,
        inputResourceQualityClassMap: Map<ResourceType, ResourceQualityClass>,
        physicsData: MutablePhysicsData,
        resourceData: MutableResourceData,
        gamma: Double,
        buyResource: Boolean = false,
    ): Double {
        val inputFractionList: List<Double> =
            mutableFactoryData.factoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
                val requiredAmount: Double =
                    inputResourceData.amountPerOutputUnit * mutableFactoryData.factoryInternalData.maxOutputAmount * mutableFactoryData.numBuilding / gamma
                val qualityClass: ResourceQualityClass = inputResourceQualityClassMap.getValue(type)
                resourceData.getProductionResourceAmount(type, qualityClass) / requiredAmount
            }

        val inputFraction: Double = inputFractionList.minOrNull() ?: 1.0

        val employeeFraction: Double = mutableFactoryData.employeeFraction()

        val fuelFraction: Double =
            physicsData.fuelRestMassData.production / (mutableFactoryData.factoryInternalData.fuelRestMassConsumptionRate * mutableFactoryData.numBuilding / gamma)

        val buyResourceFraction: Double = if (buyResource) {
            val totalPrice: Double =
                mutableFactoryData.factoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
                    val requiredAmount: Double =
                        inputResourceData.amountPerOutputUnit * mutableFactoryData.factoryInternalData.maxOutputAmount * mutableFactoryData.numBuilding / gamma
                    val qualityClass: ResourceQualityClass =
                        inputResourceQualityClassMap.getValue(type)

                    resourceData.getResourcePrice(type, qualityClass) * requiredAmount
                }.sumOf { it }

            mutableFactoryData.storedFuelRestMass / totalPrice
        } else {
            1.0
        }

        return listOf(
            1.0,
            inputFraction,
            employeeFraction,
            fuelFraction,
            buyResourceFraction,
        ).minOrNull() ?: 0.0
    }

    /**
     * Compute the reduced quality if the input quality is lower than required
     */
    fun qualityReducedFaction(
        requiredQuality: MutableResourceQualityData,
        actualQuality: MutableResourceQualityData
    ): Double {
        val r1: Double = if (actualQuality.quality1 < requiredQuality.quality1) {
            actualQuality.quality1 / requiredQuality.quality1
        } else {
            1.0
        }

        val r2: Double = if (actualQuality.quality2 < requiredQuality.quality2) {
            actualQuality.quality2 / requiredQuality.quality2
        } else {
            1.0
        }

        val r3: Double = if (actualQuality.quality3 < requiredQuality.quality3) {
            actualQuality.quality3 / requiredQuality.quality3
        } else {
            1.0
        }

        return (r1 + r2 + r3) / 3.0
    }

    /**
     * The quality of the output product
     *
     * @param mutableFactoryData the factory producing this resource
     * @param resourceData the amount of resource owned by the player
     */
    fun productQuality(
        mutableFactoryData: MutableFactoryData,
        inputResourceQualityClassMap: Map<ResourceType, ResourceQualityClass>,
        resourceData: MutableResourceData,
    ): MutableResourceQualityData {
        val fractionList: List<Double> =
            mutableFactoryData.factoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
                val requiredQuality: MutableResourceQualityData =
                    inputResourceData.maxInputResourceQualityData
                val qualityClass: ResourceQualityClass = inputResourceQualityClassMap.getValue(type)
                qualityReducedFaction(
                    requiredQuality,
                    resourceData.getResourceQuality(type, qualityClass)
                )
            }

        val avgFraction: Double = fractionList.average()
        return mutableFactoryData.factoryInternalData.maxOutputResourceQualityData * avgFraction
    }

    /**
     * Consume and produce resource
     *
     * @param mutableFactoryData the factory producing this resource
     * @param resourceData the amount of resource owned by the player
     * @param physicsData physics data of the player
     * @param gamma Lorentz factor
     */
    fun updateResourceData(
        mutableFactoryData: MutableFactoryData,
        resourceData: MutableResourceData,
        physicsData: MutablePhysicsData,
        gamma: Double
    ) {
        val qualityClassMap: Map<ResourceType, ResourceQualityClass> =
            computeInputResourceQualityClassMap(
                mutableFactoryData,
                resourceData,
                gamma,
            )

        val amountFraction: Double = productAmountFraction(
            mutableFactoryData,
            qualityClassMap,
            physicsData,
            resourceData,
            gamma
        )

        val outputQuality: MutableResourceQualityData =
            productQuality(mutableFactoryData, qualityClassMap, resourceData)


        // Consume resource
        mutableFactoryData.factoryInternalData.inputResourceMap.forEach { (type, inputResourceData) ->
            val requiredAmount: Double =
                inputResourceData.amountPerOutputUnit * mutableFactoryData.factoryInternalData.maxOutputAmount * mutableFactoryData.numBuilding
            val qualityClass: ResourceQualityClass = qualityClassMap.getValue(type)
            resourceData.getResourceAmountData(
                type,
                qualityClass
            ).production -= requiredAmount * amountFraction / gamma
        }

        // Consume fuel
        physicsData.fuelRestMassData.production -= mutableFactoryData.factoryInternalData.fuelRestMassConsumptionRate * amountFraction * mutableFactoryData.numBuilding / gamma

        // Produce resource
        resourceData.addNewResource(
            mutableFactoryData.factoryInternalData.outputResource,
            outputQuality,
            mutableFactoryData.factoryInternalData.maxOutputAmount * amountFraction * mutableFactoryData.numBuilding / gamma
        )
    }

    /**
     * Produce resource and send resource to owner
     *
     * @param mutableFactoryData the factory to produce resource
     * @param mutablePlayerData the player of that factory
     */
    fun computeSendResourceCommand(
        mutableFactoryData: MutableFactoryData,
        mutablePlayerData: MutablePlayerData,
        gamma: Double,
    ): Command {
        val toId: Int = mutableFactoryData.ownerPlayerId
        val resourceData = mutablePlayerData.playerInternalData.economyData().resourceData
        val physicsData = mutablePlayerData.playerInternalData.physicsData()

        val qualityClassMap: Map<ResourceType, ResourceQualityClass> =
            computeInputResourceQualityClassMap(
                mutableFactoryData,
                resourceData,
                gamma,
            )

        val amountFraction: Double = productAmountFraction(
            mutableFactoryData,
            qualityClassMap,
            physicsData,
            resourceData,
            gamma,
            true
        )
        val outputQuality: MutableResourceQualityData =
            productQuality(mutableFactoryData, qualityClassMap, resourceData)

        // Pay price
        val price: Double =
            mutableFactoryData.factoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
                val requiredAmount: Double =
                    inputResourceData.amountPerOutputUnit * mutableFactoryData.factoryInternalData.maxOutputAmount * mutableFactoryData.numBuilding
                val qualityClass: ResourceQualityClass = qualityClassMap.getValue(type)

                resourceData.getResourcePrice(type, qualityClass) * requiredAmount
            }.sumOf { it } * amountFraction

        mutableFactoryData.storedFuelRestMass -= price / gamma
        mutablePlayerData.playerInternalData.physicsData().fuelRestMassData.trade += price / gamma


        // Consume resource
        mutableFactoryData.factoryInternalData.inputResourceMap.forEach { (type, inputResourceData) ->
            val requiredAmount: Double =
                inputResourceData.amountPerOutputUnit * mutableFactoryData.factoryInternalData.maxOutputAmount * mutableFactoryData.numBuilding / gamma
            val qualityClass: ResourceQualityClass = qualityClassMap.getValue(type)

            resourceData.getResourceAmountData(
                type,
                qualityClass
            ).production -= requiredAmount * amountFraction / gamma
        }

        // Consume fuel
        physicsData.fuelRestMassData.production -= mutableFactoryData.factoryInternalData.fuelRestMassConsumptionRate * amountFraction * mutableFactoryData.numBuilding / gamma

        return if (mutableFactoryData.factoryInternalData.outputResource == ResourceType.FUEL) {
            SendFuelCommand(
                toId = toId,
                fromId = mutablePlayerData.playerId,
                fromInt4D = mutablePlayerData.int4D.toInt4D(),
                amount = mutableFactoryData.factoryInternalData.maxOutputAmount * amountFraction * mutableFactoryData.numBuilding / gamma,
                senderFuelLossFractionPerDistance = mutablePlayerData.playerInternalData.playerScienceData().playerScienceProductData.fuelLogisticsLossFractionPerDistance,
            )
        } else {
            SendResourceCommand(
                toId = toId,
                fromId = mutablePlayerData.playerId,
                fromInt4D = mutablePlayerData.int4D.toInt4D(),
                resourceType = mutableFactoryData.factoryInternalData.outputResource,
                resourceQualityData = outputQuality.toResourceQualityData(),
                amount = mutableFactoryData.factoryInternalData.maxOutputAmount * amountFraction * mutableFactoryData.numBuilding / gamma,
                senderResourceLossFractionPerDistance = mutablePlayerData.playerInternalData.playerScienceData().playerScienceProductData.resourceLogisticsLossFractionPerDistance,
            )
        }
    }
}