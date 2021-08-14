package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Represent the effect of a set of research project
 *
 * @property startFromBasicResearchId basic research projects with id lower than
 *  this value are all included
 * @property basicResearchIdList basic research projects with id in this list are included,
 *  should be higher than or equal to startFromBasicResearchId
 * @property basicResearchData the basic research data
 * @property startFromAppliedResearchId applied research projects with id lower than
 *  this value are all included
 * @property appliedResearchIdList applied research projects with id in this list are included,
 *  should be higher than or equal to startFromAppliedResearchId
 * @property appliedResearchData the applied research data
 */
@Serializable
data class KnowledgeData(
    val startFromBasicResearchId: Int = 0,
    val basicResearchIdList: List<Int> = listOf(),
    val basicResearchData: BasicResearchData = BasicResearchData(),
    val startFromAppliedResearchId: Int = 0,
    val appliedResearchIdList: List<Int> = listOf(),
    val appliedResearchData: AppliedResearchData = AppliedResearchData(),
)

@Serializable
data class MutableKnowledgeData(
    var startFromBasicResearchId: Int = 0,
    val basicResearchIdList: MutableList<Int> = mutableListOf(),
    var basicResearchData: MutableBasicResearchData = MutableBasicResearchData(),
    var startFromAppliedResearchId: Int = 0,
    val appliedResearchIdList: MutableList<Int> = mutableListOf(),
    var appliedResearchData: MutableAppliedResearchData = MutableAppliedResearchData(),
) {
    fun addBasicResearchProjectData(
        basicResearchProjectData: BasicResearchProjectData,
        function: (BasicResearchProjectData, MutableBasicResearchData) -> Unit
    ) {
        if (basicResearchIdList.contains(basicResearchProjectData.basicResearchId) ||
            basicResearchProjectData.basicResearchId < startFromAppliedResearchId
        ) {
            logger.error("Duplicate basic research id ${basicResearchProjectData.basicResearchId}, skip this")
        } else {
            function(basicResearchProjectData, basicResearchData)
            basicResearchIdList.add(basicResearchProjectData.basicResearchId)
            computeStartFromBasicResearchId()
        }
    }

    /**
     * Compute and modify the startFromBasicResearchId
     */
    private fun computeStartFromBasicResearchId() {
        val sortedList: List<Int> = basicResearchIdList.sorted()
        startFromBasicResearchId = sortedList.fold(startFromBasicResearchId) { newStart, id ->
            if ((newStart + 1) == id) {
                newStart + 1
            } else {
                newStart
            }
        }
        basicResearchIdList.removeAll { it < startFromBasicResearchId }
    }

    fun addAppliedResearchProjectData(
        appliedResearchProjectData: AppliedResearchProjectData,
        function: (AppliedResearchProjectData, MutableAppliedResearchData) -> Unit
    ) {
        if (appliedResearchIdList.contains(appliedResearchProjectData.appliedResearchId) ||
            appliedResearchProjectData.appliedResearchId < startFromAppliedResearchId
        ) {
            logger.error("Duplicate applied research id ${appliedResearchProjectData.appliedResearchId}, skip this")
        } else {
            function(appliedResearchProjectData, appliedResearchData)
            appliedResearchIdList.add(appliedResearchProjectData.appliedResearchId)
            computeStartFromAppliedResearchId()
        }
    }


    /**
     * Compute and modify the startFromAppliedResearchId
     */
    private fun computeStartFromAppliedResearchId() {
        val sortedList: List<Int> = appliedResearchIdList.sorted()
        startFromAppliedResearchId = sortedList.fold(startFromAppliedResearchId) { newStart, id ->
            if ((newStart + 1) == id) {
                newStart + 1
            } else {
                newStart
            }
        }
        appliedResearchIdList.removeAll { it < startFromAppliedResearchId }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}