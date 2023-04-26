package relativitization.universe.game.data.global.components.defaults.science.knowledge

import ksergen.annotations.GenerateImmutable
import relativitization.universe.game.data.components.defaults.science.knowledge.AppliedResearchField
import relativitization.universe.game.data.components.defaults.science.knowledge.BasicResearchField

/**
 * Determine how new projects are generated
 *
 * @property basicResearchProjectGenerationDataList determine basic research project generation
 * @property appliedResearchProjectGenerationDataList determine applied research project generation
 */
@GenerateImmutable
data class MutableUniverseProjectGenerationData(
    val basicResearchProjectGenerationDataList: MutableList<MutableBasicResearchProjectGenerationData> = mutableListOf(),
    val appliedResearchProjectGenerationDataList: MutableList<MutableAppliedResearchProjectGenerationData> = mutableListOf(),
)

/**
 * For generating a project of a field
 *
 * @property centerX the x coordinate of the center of the field in the knowledge plane
 * @property centerY the y coordinate of the center of the field in the knowledge plane
 * @property range the dispersion of this field in the knowledge plane
 * @property weight the likelihood that a new technology is in this field
 */
@GenerateImmutable
data class MutableProjectGenerationData(
    val centerX: Double = 0.0,
    val centerY: Double = 0.0,
    val range: Double = 1.0,
    val weight: Double = 1.0,
)

/**
 * For generating basic research project
 */
@GenerateImmutable
data class MutableBasicResearchProjectGenerationData(
    var basicResearchField: BasicResearchField,
    var projectGenerationData: MutableProjectGenerationData,
)

/**
 * For generating applied research project
 */
@GenerateImmutable
data class MutableAppliedResearchProjectGenerationData(
    var appliedResearchField: AppliedResearchField,
    var projectGenerationData: MutableProjectGenerationData,
)