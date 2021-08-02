package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable


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