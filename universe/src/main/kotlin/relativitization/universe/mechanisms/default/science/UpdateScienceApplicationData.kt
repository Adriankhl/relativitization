package relativitization.universe.mechanisms.default.science

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutablePlayerScienceData
import relativitization.universe.data.components.default.economy.MutableResourceQualityData
import relativitization.universe.data.components.default.economy.ResourceType
import relativitization.universe.data.components.default.popsystem.MutableCarrierInternalData
import relativitization.universe.data.components.default.popsystem.pop.labourer.factory.MutableFuelFactoryInternalData
import relativitization.universe.data.components.default.popsystem.pop.labourer.factory.MutableResourceFactoryInternalData
import relativitization.universe.data.components.default.science.knowledge.MutableKnowledgeData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.tanh

object UpdateScienceApplicationData : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val scienceData: MutablePlayerScienceData =
            mutablePlayerData.playerInternalData.playerScienceData()

        // Update ideal ship
        scienceData.playerScienceApplicationData.idealSpaceship =
            computeIdealShip(scienceData.playerKnowledgeData)

        // Update ideal fuel factory
        scienceData.playerScienceApplicationData.idealFuelFactory =
            computeIdealFuelFactory(scienceData.playerKnowledgeData)

        // Update all ideal resource factories, ensure loop through all resource type
        // Entertainment does not have factory, rely on entertainer pop
        ResourceType.values().forEach {
            scienceData.playerScienceApplicationData.idealResourceFactoryMap[it] = when(it) {
                ResourceType.PLANT -> computeIdealPlantFactory(scienceData.playerKnowledgeData)
                ResourceType.ANIMAL -> TODO()
                ResourceType.METAL -> TODO()
                ResourceType.PLASTIC -> TODO()
                ResourceType.FOOD -> TODO()
                ResourceType.CLOTH -> TODO()
                ResourceType.HOUSEHOLD_GOOD -> TODO()
                ResourceType.RESEARCH_EQUIPMENT -> TODO()
                ResourceType.MEDICINE -> TODO()
                ResourceType.AMMUNITION -> TODO()
                ResourceType.ENTERTAINMENT -> MutableResourceFactoryInternalData()
            }
        }

        return listOf()
    }

    fun computeIdealShip(mutableKnowledgeData: MutableKnowledgeData): MutableCarrierInternalData {

        val coreRestMass: Double =
            mutableKnowledgeData.appliedResearchData.architectureTechnologyLevel * 1E6

        val maxMovementDeltaFuelRestMass: Double = coreRestMass *
                tanh(mutableKnowledgeData.appliedResearchData.energyTechnologyLevel / 100.0)

        val idealPopulation: Double = coreRestMass

        return MutableCarrierInternalData(
            coreRestMass = coreRestMass,
            maxMovementDeltaFuelRestMass = maxMovementDeltaFuelRestMass,
            size = 100.0,
            idealPopulation = idealPopulation
        )
    }

    fun computeIdealFuelFactory(mutableKnowledgeData: MutableKnowledgeData): MutableFuelFactoryInternalData {

        val maxOutputAmount: Double = mutableKnowledgeData.appliedResearchData.energyTechnologyLevel * 1E6

        val maxNumEmployee: Double = maxOutputAmount

        return MutableFuelFactoryInternalData(
            maxOutputAmount = maxOutputAmount,
            maxNumEmployee = maxNumEmployee,
            size = 100.0
        )
    }

    fun computeIdealPlantFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData: MutableResourceQualityData = MutableResourceQualityData(
            tanh(mutableKnowledgeData.appliedResearchData.environmentalTechnologyLevel),
            0.0,
            0.0
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.PLANT,
            maxOutputResourceQualityData = MutableResourceQualityData(),
            maxOutputAmount = 0.0,
            inputResourceMap = mutableMapOf(),
            fuelRestMassConsumptionRate = 0.0,
            maxNumEmployee = 0.0,
            size = 0.0
        )
    }
}