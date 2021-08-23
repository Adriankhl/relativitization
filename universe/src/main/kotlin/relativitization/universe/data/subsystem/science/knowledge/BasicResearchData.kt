package relativitization.universe.data.subsystem.science.knowledge

import kotlinx.serialization.Serializable

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
@Serializable
data class BasicResearchData(
    val mathematicsLevel: Double = 0.0,
    val physicsLevel: Double = 0.0,
    val computerScienceLevel: Double = 0.0,
    val lifeScienceLevel: Double = 0.0,
    val socialScienceLevel: Double = 0.0,
    val humanityLevel: Double = 0.0,
)

@Serializable
data class MutableBasicResearchData(
    var mathematicsLevel: Double = 0.0,
    var physicsLevel: Double = 0.0,
    var computerScienceLevel: Double = 0.0,
    var lifeScienceLevel: Double = 0.0,
    var socialScienceLevel: Double = 0.0,
    var humanityLevel: Double = 0.0,
)