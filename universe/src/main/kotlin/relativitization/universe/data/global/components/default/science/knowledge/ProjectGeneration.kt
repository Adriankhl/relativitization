package relativitization.universe.data.global.components.default.science.knowledge

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.default.science.knowledge.AppliedResearchField
import relativitization.universe.data.components.default.science.knowledge.BasicResearchField


/**
 * For generating a project of a field
 *
 * @property centerX the x coordinate of the center of the field in the knowledge plane
 * @property centerY the y coordinate of the center of the field in the knowledge plane
 * @property range the dispersion of this field in the knowledge plane
 * @property weight the likelihood that a new technology is in this field
 */
@Serializable
data class ProjectGenerationData(
    val centerX: Double = 0.0,
    val centerY: Double = 0.0,
    val range: Double = 1.0,
    val weight: Double = 1.0,
)

@Serializable
data class MutableProjectGenerationData(
    val centerX: Double = 0.0,
    val centerY: Double = 0.0,
    val range: Double = 1.0,
    val weight: Double = 1.0,
)

/**
 * For generating basic research project
 */
@Serializable
data class BasicResearchProjectGenerationData(
    val basicResearchField: BasicResearchField,
    val projectGenerationData: ProjectGenerationData,
)

/**
 * For generating basic research project
 */
@Serializable
data class MutableBasicResearchProjectGenerationData(
    var basicResearchField: BasicResearchField,
    var projectGenerationData: MutableProjectGenerationData,
)

/**
 * For generating applied research project
 */
@Serializable
data class AppliedResearchProjectGenerationData(
    val appliedResearchField: AppliedResearchField,
    val projectGenerationData: ProjectGenerationData,
)

@Serializable
data class MutableAppliedResearchProjectGenerationData(
    var appliedResearchField: AppliedResearchField,
    var projectGenerationData: MutableProjectGenerationData,
)


@Serializable
data class UniverseProjectGenerationData(
    val basicResearchProjectGenerationDataList: List<BasicResearchProjectGenerationData> = listOf(),
    val appliedResearchProjectGenerationDataList: List<AppliedResearchProjectGenerationData> = listOf(),
)

@Serializable
data class MutableUniverseProjectGenerationData(
    val basicResearchProjectGenerationDataList: MutableList<MutableBasicResearchProjectGenerationData> = mutableListOf(),
    val appliedResearchProjectGenerationDataList: MutableList<MutableAppliedResearchProjectGenerationData> = mutableListOf(),
)