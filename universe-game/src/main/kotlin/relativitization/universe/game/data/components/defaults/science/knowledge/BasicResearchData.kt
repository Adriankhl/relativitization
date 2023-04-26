package relativitization.universe.game.data.components.defaults.science.knowledge

import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable

/**
 * Classification of applied research field
 */
enum class BasicResearchField(val value: String) {
    MATHEMATICS("Mathematics"),
    PHYSICS("Physics"),
    COMPUTER_SCIENCE("Computer science"),
    LIFE_SCIENCE("Life science"),
    SOCIAL_SCIENCE("Social science"),
    HUMANITY("Humanity"),
    ;

    override fun toString(): String {
        return value
    }
}

/**
 * A single basic research project
 */
@Serializable
data class BasicResearchProjectData(
    val basicResearchId: Int,
    val basicResearchField: BasicResearchField,
    val xCor: Double,
    val yCor: Double,
    val difficulty: Double,
    val significance: Double,
    val referenceBasicResearchIdList: List<Int>,
    val referenceAppliedResearchIdList: List<Int>,
)

/**
 * Level of applied research
 */
@GenerateImmutable
data class MutableBasicResearchData(
    var mathematicsLevel: Double = 0.0,
    var physicsLevel: Double = 0.0,
    var computerScienceLevel: Double = 0.0,
    var lifeScienceLevel: Double = 0.0,
    var socialScienceLevel: Double = 0.0,
    var humanityLevel: Double = 0.0,
)