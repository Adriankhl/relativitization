package relativitization.universe.mechanisms.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.component.economy.*
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryData
import relativitization.universe.data.component.science.UniverseScienceData
import relativitization.universe.mechanisms.Mechanism

object FactoryProduction : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeScienceData: UniverseScienceData
    ): List<Command> {
        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
           carrier.allPopData.labourerPopData.factoryMap.values.forEach { factory ->
               updateResourceData(
                   factory,
                   mutablePlayerData.playerInternalData.economyData().resourceData
               )
           }
        }

        return listOf()
    }

    /**
     * How much productivity is used due to the limitation of resource
     *
     * @param mutableFactoryData the factory producing this resource
     * @param resourceData the amount of resource owned by the player
     */
    fun productAmountFraction(
        mutableFactoryData: MutableFactoryData,
        resourceData: MutableResourceData,
    ): Double {
        val fractionList: List<Double> = mutableFactoryData.inputResourceMap.map { (type, inputResourceData) ->
            val requiredAmount: Double = inputResourceData.amountPerOutputUnit * mutableFactoryData.maxOutputAmount
            val requiredQuality: MutableResourceQualityData = inputResourceData.maxInputResourceQualityData
            val qualityClass: ResourceQualityClass = resourceData.productionQualityClass(
                type,
                requiredAmount,
                requiredQuality
            )
            resourceData.getProductionResourceAmount(type, qualityClass) / requiredAmount
        }
        return fractionList.maxOrNull() ?: 0.0
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
        resourceData: MutableResourceData,
    ): MutableResourceQualityData {
        val fractionList: List<Double> = mutableFactoryData.inputResourceMap.map { (type, inputResourceData) ->
            val requiredAmount: Double = inputResourceData.amountPerOutputUnit * mutableFactoryData.maxOutputAmount
            val requiredQuality: MutableResourceQualityData = inputResourceData.maxInputResourceQualityData
            val qualityClass: ResourceQualityClass = resourceData.productionQualityClass(
                type,
                requiredAmount,
                requiredQuality
            )
            qualityReducedFaction(
                requiredQuality,
                resourceData.getResourceQuality(type, qualityClass)
            )
        }

        val avgFraction: Double = fractionList.average()
        return mutableFactoryData.maxOutputResourceQualityData * avgFraction
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
    ) {
        val amountFraction: Double = productAmountFraction(mutableFactoryData, resourceData)
        val outputQuality: MutableResourceQualityData = productQuality(mutableFactoryData, resourceData)

        // Consume resource
        mutableFactoryData.inputResourceMap.forEach { (type, inputResourceData) ->
            val requiredAmount: Double = inputResourceData.amountPerOutputUnit * mutableFactoryData.maxOutputAmount
            val requiredQuality: MutableResourceQualityData = inputResourceData.maxInputResourceQualityData
            val qualityClass: ResourceQualityClass = resourceData.productionQualityClass(
                type,
                requiredAmount,
                requiredQuality
            )
            resourceData.getResourceAmountData(type, qualityClass).production -= requiredAmount * amountFraction
        }

        // Produce resource
        resourceData.addNewResource(
            mutableFactoryData.outputResource,
            outputQuality,
            mutableFactoryData.maxOutputAmount * amountFraction
        )
    }
}