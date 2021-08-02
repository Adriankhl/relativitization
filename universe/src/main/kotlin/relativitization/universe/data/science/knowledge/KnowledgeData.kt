package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable

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
    val startFromBasicResearchId: Int = -1,
    val basicResearchIdList: List<Int> = listOf(),
    val basicResearchData: BasicResearchData = BasicResearchData(),
    val startFromAppliedResearchId: Int = -1,
    val appliedResearchIdList: List<Int> = listOf(),
    val appliedResearchData: AppliedResearchData = AppliedResearchData(),
)

@Serializable
data class MutableKnowledgeData(
    var startFromBasicResearchId: Int = -1,
    val basicResearchIdList: MutableList<Int> = mutableListOf(),
    var basicResearchData: MutableBasicResearchData = MutableBasicResearchData(),
    var startFromAppliedResearchId: Int = -1,
    val appliedResearchIdList: MutableList<Int> = mutableListOf(),
    var appliedResearchData: MutableAppliedResearchData = MutableAppliedResearchData(),
)