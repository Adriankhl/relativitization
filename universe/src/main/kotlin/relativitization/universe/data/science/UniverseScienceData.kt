package relativitization.universe.data.science

import kotlinx.serialization.Serializable
import relativitization.universe.data.UniverseData
import relativitization.universe.data.science.knowledge.*
import relativitization.universe.generate.science.DefaultGenerateUniverseScienceData
import relativitization.universe.utils.RelativitizationLogManager

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
     * Generate new universe science data per turn
     * Should generate new projects and new common sense
     *
     * @param universeData the current universe data
     * @return the new universe science data
     */
    fun newUniverseScienceData(universeData: UniverseData): UniverseScienceData {

        return when (
            universeData.universeSettings.universeScienceDataProcessName
        ) {
            "DefaultUniverseScienceDataProcess" -> {
                DefaultProcessUniverseScienceData.newUniverseScienceData(
                    universeData,
                )
            }
            "EmptyUniverseScienceDataProcess" -> {
                universeData.universeScienceData
            }
            else -> {
                logger.error("Invalid universeScienceDataProcessName, use default process")
                DefaultProcessUniverseScienceData.newUniverseScienceData(
                    universeData,
                )
            }
        }
    }
}

object DefaultProcessUniverseScienceData {
    private val logger = RelativitizationLogManager.getLogger()

    fun basicResearchProjectFunction(
        basicResearchProjectData: BasicResearchProjectData
    ): (MutableBasicResearchData) -> Unit {
        return {
            when (basicResearchProjectData.basicResearchField) {
                BasicResearchField.MATHEMATICS -> {
                    it.mathematicsLevel += basicResearchProjectData.significance
                }
                BasicResearchField.PHYSICS -> {
                    it.physicsLevel += basicResearchProjectData.significance
                }
                BasicResearchField.COMPUTER_SCIENCE -> {
                    it.computerScienceLevel += basicResearchProjectData.significance
                }
                BasicResearchField.LIFE_SCIENCE -> {
                    it.lifeScienceLevel += basicResearchProjectData.significance
                }
                BasicResearchField.SOCIAL_SCIENCE -> {
                    it.socialScienceLevel += basicResearchProjectData.significance
                }
                BasicResearchField.HUMANITY -> {
                    it.humanityLevel += basicResearchProjectData.significance
                }
            }
        }
    }

    fun appliedResearchProjectFunction(
        appliedResearchProjectData: AppliedResearchProjectData
    ): (MutableAppliedResearchData) -> Unit {
        return {
            when (appliedResearchProjectData.appliedResearchField) {
                AppliedResearchField.ENERGY_TECHNOLOGY -> {
                    it.energyTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.FOOD_TECHNOLOGY -> {
                    it.foodTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.BIOMEDICAL_TECHNOLOGY -> {
                    it.biomedicalTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.CHEMICAL_TECHNOLOGY -> {
                    it.chemicalTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.ENVIRONMENTAL_TECHNOLOGY -> {
                    it.energyTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.ARCHITECTURE_TECHNOLOGY -> {
                    it.architectureTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.MACHINERY_TECHNOLOGY -> {
                    it.machineryTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.MATERIAL_TECHNOLOGY -> {
                    it.machineryTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.INFORMATION_TECHNOLOGY -> {
                    it.informationTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.ART_TECHNOLOGY -> {
                    it.artTechnologyLevel += appliedResearchProjectData.significance
                }
                AppliedResearchField.MILITARY_TECHNOLOGY -> {
                    it.militaryTechnologyLevel += appliedResearchProjectData.significance
                }
            }
        }
    }

    fun newUniverseScienceData(
        universeData: UniverseData,
    ): UniverseScienceData {

        val numBasicResearchProject: Int = universeData.universeScienceData.basicResearchProjectDataMap.size
        val numAppliedResearchProject: Int = universeData.universeScienceData.appliedResearchProjectDataMap.size

        // Generate new projects only if there are too few remaining projects
        val shouldGenerate: Boolean = (numBasicResearchProject < 10) || (numAppliedResearchProject < 10)

        return if (shouldGenerate) {
            DefaultGenerateUniverseScienceData.generate(
                universeData,
                30 - numBasicResearchProject,
                30 - numAppliedResearchProject,
            )
        } else {
            universeData.universeScienceData
        }
    }
}