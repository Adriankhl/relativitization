package relativitization.universe.data.science

import kotlinx.serialization.Serializable
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.science.knowledge.*
import relativitization.universe.generate.science.DefaultGenerateUniverseScienceData
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.min

@Serializable
data class UniverseScienceData(
    val commonSenseKnowledgeData: KnowledgeData = KnowledgeData(),
    val basicResearchProjectDataMap: Map<Int, BasicResearchProjectData> = mapOf(),
    val appliedResearchProjectDataMap: Map<Int, AppliedResearchProjectData> = mapOf(),
    val universeProjectGenerationData: UniverseProjectGenerationData = UniverseProjectGenerationData(),
)

@Serializable
data class MutableUniverseScienceData(
    var commonSenseKnowledgeData: MutableKnowledgeData = MutableKnowledgeData(),
    val basicResearchProjectDataMap: MutableMap<Int, BasicResearchProjectData> = mutableMapOf(),
    val appliedResearchProjectDataMap: MutableMap<Int, AppliedResearchProjectData> = mutableMapOf(),
    var universeProjectGenerationData: MutableUniverseProjectGenerationData = MutableUniverseProjectGenerationData(),
) {
    /**
     * Check the validity and add basic research project
     */
    fun addBasicResearchProjectData(basicResearchProjectData: BasicResearchProjectData) {
        when {
            basicResearchProjectDataMap.containsKey(basicResearchProjectData.basicResearchId) -> {
                logger.error("new basic research project has duplicate id, ignore the new data")
            }
            (basicResearchProjectDataMap.keys.maxOrNull()
                ?: -1) >= basicResearchProjectData.basicResearchId -> {
                logger.error("new basic research project has id smaller than the maximum id")

                // Still add the data as long as there is no duplication
                basicResearchProjectDataMap[basicResearchProjectData.basicResearchId] =
                    basicResearchProjectData
            }
            else -> {
                basicResearchProjectDataMap[basicResearchProjectData.basicResearchId] =
                    basicResearchProjectData
            }
        }
    }

    /**
     * Check the validity and add applied research project
     */
    fun addAppliedResearchProjectData(appliedResearchProjectData: AppliedResearchProjectData) {
        when {
            appliedResearchProjectDataMap.containsKey(appliedResearchProjectData.appliedResearchId) -> {
                logger.error("new applied research project has duplicate id, ignore the new data")
            }
            (appliedResearchProjectDataMap.keys.maxOrNull()
                ?: -1) >= appliedResearchProjectData.appliedResearchId -> {
                logger.error("new applied research project has id smaller than the maximum id")

                // Still add the data as long as there is no duplication
                appliedResearchProjectDataMap[appliedResearchProjectData.appliedResearchId] =
                    appliedResearchProjectData
            }
            else -> {
                appliedResearchProjectDataMap[appliedResearchProjectData.appliedResearchId] =
                    appliedResearchProjectData
            }
        }
    }

    /**
     * Update common sense to new common sense
     *
     * @param newStartFromBasicResearchId new beginning basic research id of the new common sense
     * @param newStartFromAppliedResearchId new beginning applied research id of the new common sense
     * @param basicProjectFunction the function encoding the effect of basic research projects
     * @param appliedProjectFunction  the function encoding the effect of applied research projects
     */
    fun updateCommonSenseData(
        newStartFromBasicResearchId: Int,
        newStartFromAppliedResearchId: Int,
        basicProjectFunction: (BasicResearchProjectData, MutableBasicResearchData) -> Unit,
        appliedProjectFunction: (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit,
    ) {
        basicResearchProjectDataMap.filter { it.key <= newStartFromBasicResearchId }.forEach {
            basicProjectFunction(it.value, commonSenseKnowledgeData.basicResearchData)
        }

        appliedResearchProjectDataMap.filter { it.key <= newStartFromAppliedResearchId }.forEach {
            appliedProjectFunction(it.value, commonSenseKnowledgeData.appliedResearchData)
        }

        // Clear old projects
        basicResearchProjectDataMap.keys.filter { it <= newStartFromBasicResearchId  }.forEach {
            basicResearchProjectDataMap.remove(it)
        }
        appliedResearchProjectDataMap.keys.filter { it <= newStartFromAppliedResearchId  }.forEach {
            appliedResearchProjectDataMap.remove(it)
        }

    }

    fun getNewBasicResearchId(): Int = (basicResearchProjectDataMap.keys.maxOrNull() ?: -1) + 1

    fun getNewAppliedResearchId(): Int = (appliedResearchProjectDataMap.keys.maxOrNull() ?: -1) + 1

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

object ProcessUniverseScienceData {

    private val logger = RelativitizationLogManager.getLogger()

    // list of all possible name of
    val universeScienceDataProcessNameList: List<String> = listOf(
        "DefaultUniverseScienceDataProcess",
        "EmptyUniverseScienceDataProcess"
    )

    /**
     * Obtain a function encoding the effect of basic research project
     *
     * @param universeSettings the settings, for universeScienceDataProcessName
     * @return a function transforming MutableBasicResearchData
     */
    fun basicResearchProjectFunction(
        universeSettings: UniverseSettings,
    ): (BasicResearchProjectData, MutableBasicResearchData) -> Unit {

        return when (
            universeSettings.universeScienceDataProcessName
        ) {
            "DefaultUniverseScienceDataProcess" -> {
                DefaultProcessUniverseScienceData.basicResearchProjectFunction()
            }
            "EmptyUniverseScienceDataProcess" -> {
                { _, _ ->}
            }
            else -> {
                logger.error("Invalid universeScienceDataProcessName:" +
                        " ${universeSettings.universeScienceDataProcessName}, use default process")

                DefaultProcessUniverseScienceData.basicResearchProjectFunction()
            }
        }
    }

    /**
     * Obtain a function encoding the effect of applied research project
     *
     * @param universeSettings the settings, for universeScienceDataProcessName
     * @return a function transforming MutableBasicResearchData
     */
    fun appliedResearchProjectFunction(
        universeSettings: UniverseSettings,
    ): (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit {

        return when (
            universeSettings.universeScienceDataProcessName
        ) {
            "DefaultUniverseScienceDataProcess" -> {
                DefaultProcessUniverseScienceData.appliedResearchProjectFunction()
            }
            "EmptyUniverseScienceDataProcess" -> {
                { _, _ -> }
            }
            else -> {
                logger.error("Invalid universeScienceDataProcessName:" +
                        " ${universeSettings.universeScienceDataProcessName}, use default process")

                DefaultProcessUniverseScienceData.appliedResearchProjectFunction()
            }
        }
    }


    /**
     * Generate new universe science data per turn
     * Should generate new projects and new common sense
     *
     * @param universeScienceData the universe science data to be processed
     * @param universeSettings the settings of the universe
     * @return the new universe science data
     */
    fun newUniverseScienceData(
        universeScienceData: UniverseScienceData,
        universeSettings: UniverseSettings,
    ): UniverseScienceData {
        return when (
            universeSettings.universeScienceDataProcessName
        ) {
            "DefaultUniverseScienceDataProcess" -> {
                DefaultProcessUniverseScienceData.newUniverseScienceData(
                    universeScienceData,
                )
            }
            "EmptyUniverseScienceDataProcess" -> {
                universeScienceData
            }
            else -> {
                logger.error("Invalid universeScienceDataProcessName:" +
                        " ${universeSettings.universeScienceDataProcessName}, use default process")
                DefaultProcessUniverseScienceData.newUniverseScienceData(
                    universeScienceData,
                )
            }
        }
    }
}

object DefaultProcessUniverseScienceData {
    private val logger = RelativitizationLogManager.getLogger()

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
    ): UniverseScienceData {

        val numBasicResearchProject: Int = universeScienceData.basicResearchProjectDataMap.size
        val numAppliedResearchProject: Int = universeScienceData.appliedResearchProjectDataMap.size

        // Generate new projects only if there are too few remaining projects
        val shouldGenerate: Boolean = (numBasicResearchProject < 10) || (numAppliedResearchProject < 10)

        return if (shouldGenerate) {
            DefaultGenerateUniverseScienceData.generate(
                universeScienceData,
                min(30 - numBasicResearchProject, 0),
                min(30 - numAppliedResearchProject, 0),
            )
        } else {
            universeScienceData
        }
    }
}