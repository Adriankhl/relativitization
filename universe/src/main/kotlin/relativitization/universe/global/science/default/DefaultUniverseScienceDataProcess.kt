package relativitization.universe.global.science.default

import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.global.components.science.UniverseScienceData
import relativitization.universe.data.components.default.science.knowledge.*
import relativitization.universe.generate.science.DefaultGenerateUniverseScienceData
import relativitization.universe.global.science.UniverseScienceDataProcess
import kotlin.math.max

object DefaultUniverseScienceDataProcess : UniverseScienceDataProcess() {
    override fun basicResearchProjectFunction(): (BasicResearchProjectData, MutableBasicResearchData) -> Unit {
        return { basicResearchProjectData, mutableBasicResearchData ->
            when (basicResearchProjectData.basicResearchField) {
                BasicResearchField.MATHEMATICS -> {
                    mutableBasicResearchData.mathematicsLevel += basicResearchProjectData.significance
                }
                BasicResearchField.PHYSICS -> {
                    mutableBasicResearchData.physicsLevel += basicResearchProjectData.significance
                }
                BasicResearchField.COMPUTER_SCIENCE -> {
                    mutableBasicResearchData.computerScienceLevel += basicResearchProjectData.significance
                }
                BasicResearchField.LIFE_SCIENCE -> {
                    mutableBasicResearchData.lifeScienceLevel += basicResearchProjectData.significance
                }
                BasicResearchField.SOCIAL_SCIENCE -> {
                    mutableBasicResearchData.socialScienceLevel += basicResearchProjectData.significance
                }
                BasicResearchField.HUMANITY -> {
                    mutableBasicResearchData.humanityLevel += basicResearchProjectData.significance
                }
            }
        }
    }

    override fun appliedResearchProjectFunction(): (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit {
        return { appliedResearchProjectData, mutableAppliedResearchData ->
            when (appliedResearchProjectData.appliedResearchField) {
                AppliedResearchField.ENERGY_TECHNOLOGY -> {
                    mutableAppliedResearchData.energyTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.FOOD_TECHNOLOGY -> {
                    mutableAppliedResearchData.foodTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.BIOMEDICAL_TECHNOLOGY -> {
                    mutableAppliedResearchData.biomedicalTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.CHEMICAL_TECHNOLOGY -> {
                    mutableAppliedResearchData.chemicalTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.ENVIRONMENTAL_TECHNOLOGY -> {
                    mutableAppliedResearchData.energyTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.ARCHITECTURE_TECHNOLOGY -> {
                    mutableAppliedResearchData.architectureTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.MACHINERY_TECHNOLOGY -> {
                    mutableAppliedResearchData.machineryTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.MATERIAL_TECHNOLOGY -> {
                    mutableAppliedResearchData.machineryTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.INFORMATION_TECHNOLOGY -> {
                    mutableAppliedResearchData.informationTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.ART_TECHNOLOGY -> {
                    mutableAppliedResearchData.artTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.MILITARY_TECHNOLOGY -> {
                    mutableAppliedResearchData.militaryTechnologyLevel += appliedResearchProjectData.significance
                }
            }
        }
    }

    override fun newUniverseScienceData(
        universeScienceData: UniverseScienceData,
        universeSettings: UniverseSettings
    ): UniverseScienceData {

        val numBasicResearchProject: Int = universeScienceData.basicResearchProjectDataMap.size
        val numAppliedResearchProject: Int = universeScienceData.appliedResearchProjectDataMap.size

        // Generate new projects only if there are too few remaining projects
        val shouldGenerate: Boolean =
            (numBasicResearchProject < 10) || (numAppliedResearchProject < 10)

        return if (shouldGenerate) {
            DefaultGenerateUniverseScienceData.generate(
                universeScienceData,
                max(30 - numBasicResearchProject, 0),
                max(30 - numAppliedResearchProject, 0),
            )
        } else {
            universeScienceData
        }
    }
}