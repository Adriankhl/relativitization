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
import kotlin.math.log2
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
            scienceData.playerScienceApplicationData.idealResourceFactoryMap[it] = when (it) {
                ResourceType.PLANT -> computeIdealPlantFactory(scienceData.playerKnowledgeData)
                ResourceType.ANIMAL -> computeIdealAnimalFactory(scienceData.playerKnowledgeData)
                ResourceType.METAL -> computeIdealMetalFactory(scienceData.playerKnowledgeData)
                ResourceType.PLASTIC -> computeIdealPlasticFactory(scienceData.playerKnowledgeData)
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
                tanh(mutableKnowledgeData.appliedResearchData.energyTechnologyLevel / 500.0) * 5.0

        val idealPopulation: Double = coreRestMass

        return MutableCarrierInternalData(
            coreRestMass = coreRestMass,
            maxMovementDeltaFuelRestMass = maxMovementDeltaFuelRestMass,
            size = 100.0,
            idealPopulation = idealPopulation
        )
    }

    fun computeIdealFuelFactory(mutableKnowledgeData: MutableKnowledgeData): MutableFuelFactoryInternalData {

        val maxOutputAmount: Double = 1E6

        val maxNumEmployee: Double = maxOutputAmount / log2(
            mutableKnowledgeData.appliedResearchData.energyTechnologyLevel / 100.0 + 2.0
        )

        return MutableFuelFactoryInternalData(
            maxOutputAmount = maxOutputAmount,
            maxNumEmployee = maxNumEmployee,
            size = 100.0
        )
    }

    fun computeIdealPlantFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData: MutableResourceQualityData = MutableResourceQualityData(
            log2(mutableKnowledgeData.appliedResearchData.environmentalTechnologyLevel),
            0.0,
            0.0
        )

        val maxOutputAmount: Double = 1E6

        val fuelRestMassConsumptionRate = 0.1 * maxOutputAmount / log2(
            mutableKnowledgeData.appliedResearchData.environmentalTechnologyLevel / 100.0 + 2.0
        )

        val maxNumEmployee: Double = maxOutputAmount / log2(
            mutableKnowledgeData.appliedResearchData.environmentalTechnologyLevel / 100.0 + 2.0
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.PLANT,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmount = maxOutputAmount,
            inputResourceMap = mutableMapOf(),
            fuelRestMassConsumptionRate = fuelRestMassConsumptionRate,
            maxNumEmployee = maxNumEmployee,
            size = 100.0,
        )
    }

    fun computeIdealAnimalFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData: MutableResourceQualityData = MutableResourceQualityData(
            log2(mutableKnowledgeData.appliedResearchData.biomedicalTechnologyLevel),
            0.0,
            0.0
        )

        val maxOutputAmount: Double = 1E6

        val fuelRestMassConsumptionRate = 0.1 * maxOutputAmount / log2(
            mutableKnowledgeData.appliedResearchData.biomedicalTechnologyLevel / 100.0 + 2.0
        )

        val maxNumEmployee: Double = maxOutputAmount / log2(
            mutableKnowledgeData.appliedResearchData.biomedicalTechnologyLevel / 100.0 + 2.0
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.ANIMAL,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmount = maxOutputAmount,
            inputResourceMap = mutableMapOf(),
            fuelRestMassConsumptionRate = fuelRestMassConsumptionRate,
            maxNumEmployee = maxNumEmployee,
            size = 100.0,
        )
    }

    fun computeIdealMetalFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData: MutableResourceQualityData = MutableResourceQualityData(
            log2(mutableKnowledgeData.appliedResearchData.machineryTechnologyLevel),
            0.0,
            0.0
        )

        val maxOutputAmount: Double = 1E6

        val fuelRestMassConsumptionRate = 0.1 * maxOutputAmount / log2(
            mutableKnowledgeData.appliedResearchData.machineryTechnologyLevel / 100.0 + 2.0
        )

        val maxNumEmployee: Double = maxOutputAmount / log2(
            mutableKnowledgeData.appliedResearchData.machineryTechnologyLevel / 100.0 + 2.0
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.METAL,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmount = maxOutputAmount,
            inputResourceMap = mutableMapOf(),
            fuelRestMassConsumptionRate = fuelRestMassConsumptionRate,
            maxNumEmployee = maxNumEmployee,
            size = 100.0,
        )
    }

    fun computeIdealPlasticFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData: MutableResourceQualityData = MutableResourceQualityData(
            log2(mutableKnowledgeData.appliedResearchData.chemicalTechnologyLevel),
            0.0,
            0.0
        )

        val maxOutputAmount: Double = 1E6

        val fuelRestMassConsumptionRate = 0.1 * maxOutputAmount / log2(
            mutableKnowledgeData.appliedResearchData.chemicalTechnologyLevel / 100.0 + 2.0
        )

        val maxNumEmployee: Double = maxOutputAmount / log2(
            mutableKnowledgeData.appliedResearchData.chemicalTechnologyLevel / 100.0 + 2.0
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.PLASTIC,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmount = maxOutputAmount,
            inputResourceMap = mutableMapOf(),
            fuelRestMassConsumptionRate = fuelRestMassConsumptionRate,
            maxNumEmployee = maxNumEmployee,
            size = 100.0,
        )
    }
}