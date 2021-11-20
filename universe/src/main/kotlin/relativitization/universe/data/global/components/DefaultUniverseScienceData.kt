package relativitization.universe.data.global.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.components.default.science.knowledge.*
import relativitization.universe.data.global.components.default.science.knowledge.MutableUniverseProjectGenerationData
import relativitization.universe.data.global.components.default.science.knowledge.UniverseProjectGenerationData
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
@SerialName("UniverseScienceData")
data class UniverseScienceData(
    val commonSenseKnowledgeData: KnowledgeData = KnowledgeData(),
    val basicResearchProjectDataMap: Map<Int, BasicResearchProjectData> = mapOf(),
    val appliedResearchProjectDataMap: Map<Int, AppliedResearchProjectData> = mapOf(),
    val universeProjectGenerationData: UniverseProjectGenerationData = UniverseProjectGenerationData(),
) : DefaultGlobalDataComponent()

@Serializable
@SerialName("UniverseScienceData")
data class MutableUniverseScienceData(
    var commonSenseKnowledgeData: MutableKnowledgeData = MutableKnowledgeData(),
    val basicResearchProjectDataMap: MutableMap<Int, BasicResearchProjectData> = mutableMapOf(),
    val appliedResearchProjectDataMap: MutableMap<Int, AppliedResearchProjectData> = mutableMapOf(),
    var universeProjectGenerationData: MutableUniverseProjectGenerationData = MutableUniverseProjectGenerationData(),
) : MutableDefaultGlobalDataComponent() {
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
        basicResearchProjectDataMap.keys.filter { it <= newStartFromBasicResearchId }.forEach {
            basicResearchProjectDataMap.remove(it)
        }
        appliedResearchProjectDataMap.keys.filter { it <= newStartFromAppliedResearchId }.forEach {
            appliedResearchProjectDataMap.remove(it)
        }

    }

    fun getNewBasicResearchId(): Int = (basicResearchProjectDataMap.keys.maxOrNull()
        ?: (commonSenseKnowledgeData.startFromBasicResearchId - 1)) + 1

    fun getNewAppliedResearchId(): Int = (appliedResearchProjectDataMap.keys.maxOrNull()
        ?: (commonSenseKnowledgeData.startFromAppliedResearchId - 1)) + 1

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}