package relativitization.universe.game.mechanisms.defaults.regular.science

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.game.data.components.MutablePlayerScienceData
import relativitization.universe.game.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.popsystem.MutableCarrierInternalData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryInternalData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.MutableInputResourceData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.MutableResourceFactoryInternalData
import relativitization.universe.game.data.components.defaults.science.knowledge.MutableKnowledgeData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import kotlin.math.log2
import kotlin.random.Random

object UpdateScienceApplicationData : Mechanism() {
    // Parameters
    // Ideal spaceship
    private const val spaceshipCoreRestMassFactor: Double = 1E6

    // Fuel factory
    private const val fuelFactoryOutputFactor: Double = 20.0

    // Resource factory
    private const val fuelConsumptionFactor: Double = 2.0
    private const val primaryResourceOutputFactor: Double = 600.0
    private const val secondaryResourceOutputFactor: Double = 200.0

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
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
                ResourceType.FOOD -> computeIdealFoodFactory(scienceData.playerKnowledgeData)
                ResourceType.CLOTH -> computeIdealClothFactory(scienceData.playerKnowledgeData)
                ResourceType.HOUSEHOLD_GOOD -> computeIdealHouseholdGoodFactory(scienceData.playerKnowledgeData)
                ResourceType.RESEARCH_EQUIPMENT -> computeIdealResearchEquipmentFactory(scienceData.playerKnowledgeData)
                ResourceType.MEDICINE -> computeIdealMedicineFactory(scienceData.playerKnowledgeData)
                ResourceType.AMMUNITION -> computeIdealAmmunitionFactory(scienceData.playerKnowledgeData)
                ResourceType.ENTERTAINMENT -> MutableResourceFactoryInternalData()
            }
        }

        // Update ideal entertainment quality
        scienceData.playerScienceApplicationData.idealEntertainmentQuality =
            computeIdealEntertainmentQuality(scienceData.playerKnowledgeData)

        // Update logistic tech
        scienceData.playerScienceApplicationData.fuelLogisticsLossFractionPerDistance =
            computeFuelLogisticsLoss(scienceData.playerKnowledgeData)
        scienceData.playerScienceApplicationData.resourceLogisticsLossFractionPerDistance =
            computeResourceLogisticsLoss(scienceData.playerKnowledgeData)

        // Update military tech
        scienceData.playerScienceApplicationData.militaryBaseAttackFactor =
            computeMilitaryBaseAttackFactor(scienceData.playerKnowledgeData)
        scienceData.playerScienceApplicationData.militaryBaseShieldFactor =
            computeMilitaryBaseShieldFactor(scienceData.playerKnowledgeData)

        return listOf()
    }

    fun computeIdealShip(mutableKnowledgeData: MutableKnowledgeData): MutableCarrierInternalData {
        val coreRestMass: Double =
            (mutableKnowledgeData.appliedResearchData.architectureTechnologyLevel + 1.0) *
                    spaceshipCoreRestMassFactor

        // Set a high value to encourage movement
        // May reconsider this
        val maxMovementDeltaFuelRestMass: Double = coreRestMass * 1E4

        val idealPopulation: Double = coreRestMass

        return MutableCarrierInternalData(
            coreRestMass = coreRestMass,
            maxMovementDeltaFuelRestMass = maxMovementDeltaFuelRestMass,
            size = 100.0,
            idealPopulation = idealPopulation
        )
    }

    fun computeIdealFuelFactory(mutableKnowledgeData: MutableKnowledgeData): MutableFuelFactoryInternalData {
        val maxOutputAmountPerEmployee: Double = fuelFactoryOutputFactor * log2(
            mutableKnowledgeData.appliedResearchData.energyTechnologyLevel / 100.0 + 2.0
        )

        return MutableFuelFactoryInternalData(
            maxOutputAmountPerEmployee = maxOutputAmountPerEmployee,
            sizePerEmployee = 50.0,
        )
    }

    fun computeIdealPlantFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData = MutableResourceQualityData(
            log2(mutableKnowledgeData.appliedResearchData.environmentalTechnologyLevel + 2.0),
        )

        val fuelRestMassConsumptionRatePerEmployee = fuelConsumptionFactor

        val maxOutputAmountPerEmployee: Double = primaryResourceOutputFactor * log2(
            mutableKnowledgeData.appliedResearchData.environmentalTechnologyLevel / 100.0 + 2.0
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.PLANT,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmountPerEmployee = maxOutputAmountPerEmployee,
            inputResourceMap = mutableMapOf(),
            fuelRestMassConsumptionRatePerEmployee = fuelRestMassConsumptionRatePerEmployee,
            sizePerEmployee = 100.0,
        )
    }

    fun computeIdealAnimalFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData = MutableResourceQualityData(
            log2(mutableKnowledgeData.appliedResearchData.biomedicalTechnologyLevel + 2.0),
        )

        val fuelRestMassConsumptionRatePerEmployee = fuelConsumptionFactor

        val maxOutputAmountPerEmployee: Double = primaryResourceOutputFactor * log2(
            mutableKnowledgeData.appliedResearchData.biomedicalTechnologyLevel / 100.0 + 2.0
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.ANIMAL,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmountPerEmployee = maxOutputAmountPerEmployee,
            inputResourceMap = mutableMapOf(),
            fuelRestMassConsumptionRatePerEmployee = fuelRestMassConsumptionRatePerEmployee,
            sizePerEmployee = 100.0
        )
    }

    fun computeIdealMetalFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData = MutableResourceQualityData(
            log2(mutableKnowledgeData.appliedResearchData.machineryTechnologyLevel + 2.0),
        )

        val fuelRestMassConsumptionRatePerEmployee = fuelConsumptionFactor

        val maxOutputAmountPerEmployee: Double = primaryResourceOutputFactor * log2(
            mutableKnowledgeData.appliedResearchData.biomedicalTechnologyLevel / 100.0 + 2.0
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.METAL,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmountPerEmployee = maxOutputAmountPerEmployee,
            inputResourceMap = mutableMapOf(),
            fuelRestMassConsumptionRatePerEmployee = fuelRestMassConsumptionRatePerEmployee,
            sizePerEmployee = 100.0
        )
    }

    fun computeIdealPlasticFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData = MutableResourceQualityData(
            log2(mutableKnowledgeData.appliedResearchData.chemicalTechnologyLevel + 2.0),
        )

        val fuelRestMassConsumptionRatePerEmployee = fuelConsumptionFactor

        val maxOutputAmountPerEmployee: Double = primaryResourceOutputFactor * log2(
            mutableKnowledgeData.appliedResearchData.biomedicalTechnologyLevel / 100.0 + 2.0
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.PLASTIC,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmountPerEmployee = maxOutputAmountPerEmployee,
            inputResourceMap = mutableMapOf(),
            fuelRestMassConsumptionRatePerEmployee = fuelRestMassConsumptionRatePerEmployee,
            sizePerEmployee = 100.0
        )
    }

    fun computeIdealFoodFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData = MutableResourceQualityData(
            quality = log2(mutableKnowledgeData.appliedResearchData.foodTechnologyLevel + 2.0),
        )

        val fuelRestMassConsumptionRatePerEmployee = fuelConsumptionFactor

        val maxOutputAmountPerEmployee: Double = secondaryResourceOutputFactor * log2(
            mutableKnowledgeData.appliedResearchData.foodTechnologyLevel / 100.0 + 2.0
        )

        val inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(
            ResourceType.ANIMAL to MutableInputResourceData(
                qualityData = MutableResourceQualityData(
                    quality = log2(mutableKnowledgeData.appliedResearchData.foodTechnologyLevel + 2.0),
                ),
                amountPerOutput = 1.0
            ),
            ResourceType.PLANT to MutableInputResourceData(
                qualityData = MutableResourceQualityData(
                    quality = log2(mutableKnowledgeData.appliedResearchData.foodTechnologyLevel + 2.0),
                ),
                amountPerOutput = 1.0
            ),
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.FOOD,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmountPerEmployee = maxOutputAmountPerEmployee,
            inputResourceMap = inputResourceMap,
            fuelRestMassConsumptionRatePerEmployee = fuelRestMassConsumptionRatePerEmployee,
            sizePerEmployee = 100.0,
        )
    }

    fun computeIdealClothFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData = MutableResourceQualityData(
            quality = log2(mutableKnowledgeData.appliedResearchData.materialTechnologyLevel + 2.0),
        )

        val fuelRestMassConsumptionRatePerEmployee = fuelConsumptionFactor

        val maxOutputAmountPerEmployee: Double = secondaryResourceOutputFactor * log2(
            mutableKnowledgeData.appliedResearchData.foodTechnologyLevel / 100.0 + 2.0
        )

        val inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(
            ResourceType.ANIMAL to MutableInputResourceData(
                qualityData = MutableResourceQualityData(
                    quality = log2(mutableKnowledgeData.appliedResearchData.materialTechnologyLevel + 2.0),
                ),
                amountPerOutput = 1.0
            ),
            ResourceType.PLASTIC to MutableInputResourceData(
                qualityData = MutableResourceQualityData(
                    quality = log2(mutableKnowledgeData.appliedResearchData.materialTechnologyLevel + 2.0),
                ),
                amountPerOutput = 1.0
            ),
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.CLOTH,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmountPerEmployee = maxOutputAmountPerEmployee,
            inputResourceMap = inputResourceMap,
            fuelRestMassConsumptionRatePerEmployee = fuelRestMassConsumptionRatePerEmployee,
            sizePerEmployee = 100.0,
        )
    }

    fun computeIdealHouseholdGoodFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData = MutableResourceQualityData(
            quality = log2(mutableKnowledgeData.appliedResearchData.artTechnologyLevel + 2.0),
        )

        val fuelRestMassConsumptionRatePerEmployee = fuelConsumptionFactor

        val maxOutputAmountPerEmployee: Double = secondaryResourceOutputFactor * log2(
            mutableKnowledgeData.appliedResearchData.foodTechnologyLevel / 100.0 + 2.0
        )

        val inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(
            ResourceType.PLANT to MutableInputResourceData(
                qualityData = MutableResourceQualityData(
                    quality = log2(mutableKnowledgeData.appliedResearchData.artTechnologyLevel + 2.0),
                ),
                amountPerOutput = 1.0
            ),
            ResourceType.PLASTIC to MutableInputResourceData(
                qualityData = MutableResourceQualityData(
                    quality = log2(mutableKnowledgeData.appliedResearchData.artTechnologyLevel + 2.0),
                ),
                amountPerOutput = 1.0
            ),
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.HOUSEHOLD_GOOD,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmountPerEmployee = maxOutputAmountPerEmployee,
            inputResourceMap = inputResourceMap,
            fuelRestMassConsumptionRatePerEmployee = fuelRestMassConsumptionRatePerEmployee,
            sizePerEmployee = 100.0,
        )
    }

    fun computeIdealResearchEquipmentFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData = MutableResourceQualityData(
            quality = log2(mutableKnowledgeData.appliedResearchData.materialTechnologyLevel + 2.0),
        )

        val fuelRestMassConsumptionRatePerEmployee = fuelConsumptionFactor

        val maxOutputAmountPerEmployee: Double = secondaryResourceOutputFactor * 0.5 * log2(
            mutableKnowledgeData.appliedResearchData.foodTechnologyLevel / 100.0 + 2.0
        )

        val inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(
            ResourceType.ANIMAL to MutableInputResourceData(
                qualityData = MutableResourceQualityData(
                    quality = log2(mutableKnowledgeData.appliedResearchData.materialTechnologyLevel + 2.0),
                ),
                amountPerOutput = 1.0
            ),
            ResourceType.METAL to MutableInputResourceData(
                qualityData = MutableResourceQualityData(
                    quality = log2(mutableKnowledgeData.appliedResearchData.materialTechnologyLevel + 2.0),
                ),
                amountPerOutput = 1.0
            ),
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.RESEARCH_EQUIPMENT,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmountPerEmployee = maxOutputAmountPerEmployee,
            inputResourceMap = inputResourceMap,
            fuelRestMassConsumptionRatePerEmployee = fuelRestMassConsumptionRatePerEmployee,
            sizePerEmployee = 100.0,
        )
    }

    fun computeIdealMedicineFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData = MutableResourceQualityData(
            quality = log2(mutableKnowledgeData.appliedResearchData.biomedicalTechnologyLevel + 2.0),
        )

        val fuelRestMassConsumptionRatePerEmployee = fuelConsumptionFactor

        val maxOutputAmountPerEmployee: Double = secondaryResourceOutputFactor * 0.25 * log2(
            mutableKnowledgeData.appliedResearchData.foodTechnologyLevel / 100.0 + 2.0
        )

        val inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(
            ResourceType.PLANT to MutableInputResourceData(
                qualityData = MutableResourceQualityData(
                    quality = log2(mutableKnowledgeData.appliedResearchData.biomedicalTechnologyLevel + 2.0),
                ),
                amountPerOutput = 1.0
            ),
            ResourceType.METAL to MutableInputResourceData(
                qualityData = MutableResourceQualityData(
                    quality = log2(mutableKnowledgeData.appliedResearchData.biomedicalTechnologyLevel + 2.0),
                ),
                amountPerOutput = 1.0
            ),
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.MEDICINE,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmountPerEmployee = maxOutputAmountPerEmployee,
            inputResourceMap = inputResourceMap,
            fuelRestMassConsumptionRatePerEmployee = fuelRestMassConsumptionRatePerEmployee,
            sizePerEmployee = 100.0,
        )
    }

    fun computeIdealAmmunitionFactory(mutableKnowledgeData: MutableKnowledgeData): MutableResourceFactoryInternalData {
        val maxOutputResourceQualityData = MutableResourceQualityData(
            quality = log2(mutableKnowledgeData.appliedResearchData.militaryTechnologyLevel + 2.0),
        )

        val fuelRestMassConsumptionRatePerEmployee = fuelConsumptionFactor

        val maxOutputAmountPerEmployee: Double = secondaryResourceOutputFactor * 0.25 * log2(
            mutableKnowledgeData.appliedResearchData.foodTechnologyLevel / 100.0 + 2.0
        )

        val inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(
            ResourceType.METAL to MutableInputResourceData(
                qualityData = MutableResourceQualityData(
                    quality = log2(mutableKnowledgeData.appliedResearchData.militaryTechnologyLevel + 2.0),
                ),
                amountPerOutput = 1.0
            ),
            ResourceType.PLASTIC to MutableInputResourceData(
                qualityData = MutableResourceQualityData(
                    quality = log2(mutableKnowledgeData.appliedResearchData.militaryTechnologyLevel + 2.0),
                ),
                amountPerOutput = 1.0
            ),
        )

        return MutableResourceFactoryInternalData(
            outputResource = ResourceType.AMMUNITION,
            maxOutputResourceQualityData = maxOutputResourceQualityData,
            maxOutputAmountPerEmployee = maxOutputAmountPerEmployee,
            inputResourceMap = inputResourceMap,
            fuelRestMassConsumptionRatePerEmployee = fuelRestMassConsumptionRatePerEmployee,
            sizePerEmployee = 100.0,
        )
    }

    fun computeIdealEntertainmentQuality(mutableKnowledgeData: MutableKnowledgeData): MutableResourceQualityData {
        return MutableResourceQualityData(
            quality = log2(mutableKnowledgeData.appliedResearchData.informationTechnologyLevel + 2.0),
        )
    }

    fun computeFuelLogisticsLoss(mutableKnowledgeData: MutableKnowledgeData): Double {
        return 0.1 + 0.8 / (mutableKnowledgeData.appliedResearchData.energyTechnologyLevel / 100.0 + 1.0)
    }

    fun computeResourceLogisticsLoss(mutableKnowledgeData: MutableKnowledgeData): Double {
        return 0.1 + 0.8 / (mutableKnowledgeData.appliedResearchData.informationTechnologyLevel / 100.0 + 1.0)
    }

    fun computeMilitaryBaseAttackFactor(mutableKnowledgeData: MutableKnowledgeData): Double {
        return log2(mutableKnowledgeData.appliedResearchData.militaryTechnologyLevel / 10.0 + 2.0)
    }

    fun computeMilitaryBaseShieldFactor(mutableKnowledgeData: MutableKnowledgeData): Double {
        return log2(mutableKnowledgeData.appliedResearchData.militaryTechnologyLevel / 10.0 + 2.0)
    }
}