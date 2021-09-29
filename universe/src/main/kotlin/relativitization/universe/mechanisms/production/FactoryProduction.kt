package relativitization.universe.mechanisms.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.component.MutablePhysicsData
import relativitization.universe.data.component.economy.*
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryData
import relativitization.universe.data.UniverseScienceData
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
        // Clean up fuel in resource data
        mutablePlayerData.playerInternalData.economyData().resourceData.removeFuel()

        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
            carrier.allPopData.labourerPopData.factoryMap.values.forEach { factory ->
                updateResourceData(
                    factory,
                    mutablePlayerData.playerInternalData.economyData().resourceData,
                    mutablePlayerData.playerInternalData.physicsData(),
                )
            }
        }

        // Store fuel to physics data
        if (mutablePlayerData.playerInternalData.modifierData(
        ).physicsModifierData.disableRestMassIncreaseTimeLimit <= 0) {
            mutablePlayerData.playerInternalData.physicsData().addFuel(
                mutablePlayerData.playerInternalData.economyData().resourceData.getFuelAmount()
            )
        }

        // Clean up fuel in resource data
        mutablePlayerData.playerInternalData.economyData().resourceData.removeFuel()


        return listOf()
    }

    /**
     * Compute input resource quality class
     */
    fun computeInputResourceQualityClassMap(
        mutableFactoryData: MutableFactoryData,
        resourceData: MutableResourceData,
    ): Map<ResourceType, ResourceQualityClass> {
        return mutableFactoryData.factoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
            val requiredAmount: Double =
                inputResourceData.amountPerOutputUnit * mutableFactoryData.factoryInternalData.maxOutputAmount * mutableFactoryData.numBuilding
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
     * @param resourceData the amount of resource owned by the player
     */
    fun productAmountFraction(
        mutableFactoryData: MutableFactoryData,
        inputResourceQualityClassMap: Map<ResourceType, ResourceQualityClass>,
        physicsData: MutablePhysicsData,
        resourceData: MutableResourceData,
    ): Double {
        val inputFractionList: List<Double> =
            mutableFactoryData.factoryInternalData.inputResourceMap.map { (type, inputResourceData) ->
                val requiredAmount: Double =
                    inputResourceData.amountPerOutputUnit * mutableFactoryData.factoryInternalData.maxOutputAmount * mutableFactoryData.numBuilding
                val qualityClass: ResourceQualityClass = inputResourceQualityClassMap.getValue(type)
                resourceData.getProductionResourceAmount(type, qualityClass) / requiredAmount
            }

        val inputFraction: Double = inputFractionList.minOrNull() ?: 1.0

        val employeeFraction: Double = mutableFactoryData.employeeFraction()

        val fuelFraction: Double = physicsData.fuelRestMassData.production / (mutableFactoryData.factoryInternalData.fuelRestMassConsumptionRate * mutableFactoryData.numBuilding)

        return listOf(inputFraction, employeeFraction, fuelFraction).minOrNull() ?: 0.0
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
     */
    fun updateResourceData(
        mutableFactoryData: MutableFactoryData,
        resourceData: MutableResourceData,
        physicsData: MutablePhysicsData,
    ) {
        val qualityClassMap: Map<ResourceType, ResourceQualityClass> =
            computeInputResourceQualityClassMap(
                mutableFactoryData,
                resourceData,
            )
        val amountFraction: Double = productAmountFraction(
            mutableFactoryData,
            qualityClassMap,
            physicsData,
            resourceData
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
            ).production -= requiredAmount * amountFraction
        }

        // Consume fuel
        physicsData.fuelRestMassData.production -= mutableFactoryData.factoryInternalData.fuelRestMassConsumptionRate * amountFraction * mutableFactoryData.numBuilding

        // Produce resource
        resourceData.addNewResource(
            mutableFactoryData.factoryInternalData.outputResource,
            outputQuality,
            mutableFactoryData.factoryInternalData.maxOutputAmount * amountFraction * mutableFactoryData.numBuilding
        )
    }
}