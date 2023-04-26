package relativitization.universe.game.global.defaults.science

import relativitization.universe.core.data.PlayerData
import relativitization.universe.core.data.UniverseData
import relativitization.universe.core.data.global.MutableUniverseGlobalData
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.global.GlobalMechanism
import relativitization.universe.game.data.components.defaults.science.knowledge.AppliedResearchField
import relativitization.universe.game.data.components.defaults.science.knowledge.AppliedResearchProjectData
import relativitization.universe.game.data.components.defaults.science.knowledge.BasicResearchField
import relativitization.universe.game.data.components.defaults.science.knowledge.BasicResearchProjectData
import relativitization.universe.game.data.components.defaults.science.knowledge.MutableAppliedResearchData
import relativitization.universe.game.data.components.defaults.science.knowledge.MutableBasicResearchData
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.game.data.global.components.MutableUniverseScienceData
import relativitization.universe.game.data.global.components.UniverseScienceData
import relativitization.universe.game.data.global.components.universeScienceData
import relativitization.universe.game.generate.random.science.DefaultGenerateUniverseScienceData
import kotlin.math.max
import kotlin.random.Random

object UpdateUniverseScienceData : GlobalMechanism() {
    override fun updateGlobalData(
        mutableUniverseGlobalData: MutableUniverseGlobalData,
        universeData: UniverseData,
        random: Random,
    ) {
        // Parameters
        val minBasicProject = 10
        val maxBasicProject = 30
        val minAppliedProject = 10
        val maxAppliedProject = 30

        // Update universe common sense
        val mutableUniverseScienceData: MutableUniverseScienceData =
            mutableUniverseGlobalData.universeScienceData()

        val allVisiblePlayerData: List<PlayerData> = universeData.getAllVisiblePlayerDataList()

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
            random = random,
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
        random: Random,
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
                random = random,
            )
        } else {
            universeScienceData
        }

        return DataSerializer.copy(newScienceData)
    }
}