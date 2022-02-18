package relativitization.universe.global.defaults.science

import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.components.defaults.science.knowledge.*
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.data.global.MutableUniverseGlobalData
import relativitization.universe.data.global.components.MutableUniverseScienceData
import relativitization.universe.data.global.components.UniverseScienceData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.science.DefaultGenerateUniverseScienceData
import relativitization.universe.global.GlobalMechanism
import kotlin.math.max

object UpdateUniverseScienceData : GlobalMechanism() {
    override fun updateGlobalData(
        mutableUniverseGlobalData: MutableUniverseGlobalData,
        universeData: UniverseData
    ) {
        // Parameters
        val minBasicProject: Int = 10
        val maxBasicProject: Int = 30
        val minAppliedProject: Int = 10
        val maxAppliedProject: Int = 30

        // Update universe common sense
        val mutableUniverseScienceData: MutableUniverseScienceData =
            mutableUniverseGlobalData.universeScienceData()

        val allVisiblePlayerData: List<PlayerData> = universeData.getAllVisiblePlayerData()

        val newStartFromBasicResearchId: Int = allVisiblePlayerData.minOfOrNull {
            it.playerInternalData.playerScienceData().playerKnowledgeData.startFromBasicResearchId
        } ?: 0

        val newStartFromAppliedResearchId: Int = allVisiblePlayerData.minOfOrNull {
            it.playerInternalData.playerScienceData().playerKnowledgeData.startFromAppliedResearchId
        } ?: 0

        mutableUniverseScienceData.updateCommonSenseData(
            newStartFromBasicResearchId = newStartFromBasicResearchId,
            newStartFromAppliedResearchId = newStartFromAppliedResearchId,
            basicProjectFunction = basicResearchProjectFunction(),
            appliedProjectFunction = appliedResearchProjectFunction(),
        )

        // Difficulty and significance increases as more knowledge in common sense
        val maxDifficulty: Double =
            (mutableUniverseScienceData.commonSenseKnowledgeData.startFromBasicResearchId +
                    mutableUniverseScienceData.commonSenseKnowledgeData.startFromAppliedResearchId).toDouble() *
                    0.1 + 1.0
        val maxSignificance: Double =
            (mutableUniverseScienceData.commonSenseKnowledgeData.startFromBasicResearchId +
                    mutableUniverseScienceData.commonSenseKnowledgeData.startFromAppliedResearchId).toDouble() *
                    0.1 + 1.0

        // Generate new projects
        val newUniverseScienceData: MutableUniverseScienceData = newUniverseScienceData(
            universeScienceData = DataSerializer.copy(mutableUniverseScienceData),
            minBasicProject = minBasicProject,
            maxBasicProject = maxBasicProject,
            minAppliedProject = minAppliedProject,
            maxAppliedProject = maxAppliedProject,
            maxDifficulty = maxDifficulty,
            maxSignificance = maxSignificance,
        )

        mutableUniverseGlobalData.universeScienceData(newUniverseScienceData)
    }

    fun basicResearchProjectFunction(): (BasicResearchProjectData, MutableBasicResearchData) -> Unit {
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

    fun appliedResearchProjectFunction(): (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit {
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

    fun newUniverseScienceData(
        universeScienceData: UniverseScienceData,
        minBasicProject: Int,
        maxBasicProject: Int,
        minAppliedProject: Int,
        maxAppliedProject: Int,
        maxDifficulty: Double,
        maxSignificance: Double,
    ): MutableUniverseScienceData {

        val numBasicResearchProject: Int = universeScienceData.basicResearchProjectDataMap.size
        val numAppliedResearchProject: Int = universeScienceData.appliedResearchProjectDataMap.size

        // Generate new projects only if there are too few remaining projects
        val shouldGenerate: Boolean =
            (numBasicResearchProject < minBasicProject) || (numAppliedResearchProject < minAppliedProject)

        val newScienceData: UniverseScienceData = if (shouldGenerate) {
            DefaultGenerateUniverseScienceData.generate(
                universeScienceData = universeScienceData,
                numBasicResearchProjectGenerate = max(maxBasicProject - numBasicResearchProject, 0),
                numAppliedResearchProjectGenerate = max(maxAppliedProject - numAppliedResearchProject, 0),
                maxBasicReference = maxBasicProject / 3,
                maxAppliedReference = maxAppliedProject / 3,
                maxDifficulty = maxDifficulty,
                maxSignificance = maxSignificance,
            )
        } else {
            universeScienceData
        }

        return DataSerializer.copy(newScienceData)
    }
}