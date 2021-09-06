package relativitization.universe.mechanisms.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.component.economy.*
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
     * @param inputResourceMap the input resources required
     * @param resourceData the amount of resource owned by the player
     */
    fun productivityFraction(
        inputResourceMap: MutableMap<ResourceType, MutableInputResourceData>,
        resourceData: MutableResourceData,
    ): Double {
        val fractionList: List<Double> = inputResourceMap.map { (type, inputResourceData) ->
            0.0
        }
        return 0.0
    }
}