package relativitization.universe.mechanisms.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.component.economy.*
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableInputResourceData
import relativitization.universe.data.component.science.UniverseScienceData
import relativitization.universe.mechanisms.Mechanism

object FactoryProduction : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeScienceData: UniverseScienceData
    ): List<Command> {
        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach {
           it.allPopData.labourerPopData.factoryMap.values.forEach {
               it.outputResource
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
    fun productivityFraction(
        mutableFactoryData: MutableFactoryData,
        resourceData: MutableResourceData,
    ): Double {
        val fractionList: List<Double> = mutableFactoryData.inputResourceMap.map { (type, inputResourceData) ->
            val requiredAmount: Double = inputResourceData.amountPerOutputUnit * mutableFactoryData.maxOutputAmount
            val qualityClass: ResourceQualityClass = resourceData.productionQualityClass(
                type,
                requiredAmount,
                inputResourceData.maxInputResourceQualityData
            )
            resourceData.getProductionResourceAmount(type, qualityClass) / requiredAmount
        }
        return fractionList.maxOrNull() ?: 0.0
    }
}