package relativitization.universe.mechanisms.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.component.economy.MutableResourceQualityData
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.popsystem.MutableCarrierData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableBuildFactoryData
import relativitization.universe.data.component.science.UniverseScienceData
import relativitization.universe.mechanisms.Mechanism

object BuildFactory : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeScienceData: UniverseScienceData
    ): List<Command> {
        mutablePlayerData.playerInternalData.popSystemData(
        ).carrierDataMap.values.forEach { carrier ->
            carrier.allPopData.labourerPopData.buildingFactoryMap.values.forEach {
                updateFactoryBuildProcess(it)
            }
        }
        return listOf()
    }

    fun updateFactoryBuildProcess(
        mutableBuildFactoryData: MutableBuildFactoryData,
    ) {
        val totalRequiredFuelRestMass: Double = computeTotalRequiredFuelRestMass(
            mutableBuildFactoryData,
        )
    }

    fun computeTotalRequiredFuelRestMass(
        mutableBuildFactoryData: MutableBuildFactoryData,
    ): Double {
        return if (mutableBuildFactoryData.outputResource == ResourceType.FUEL) {
            mutableBuildFactoryData.targetOutputAmount * 10.0
        } else {
            val totalQuality: Double = mutableBuildFactoryData.targetQualityData.quality1 +
                    mutableBuildFactoryData.targetQualityData.quality2 +
                    mutableBuildFactoryData.targetQualityData.quality3
            mutableBuildFactoryData.targetOutputAmount * totalQuality
        }
    }
}