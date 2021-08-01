package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable

@Serializable
data class BasicResearchData(
    val mathematicsLevel: Double = 0.0,
    val physicsLevel: Double = 0.0,
    val computerScienceLevel: Double = 0.0,
    val engineeringLevel: Double = 0.0,
    val biomedicalScienceLevel: Double = 0.0,
    val lifeScienceLevel: Double = 0.0,
    val socialScienceLevel: Double = 0.0,
    val humanityLevel: Double = 0.0,
)

@Serializable
data class BasicResearchProjectData(
    val basicResearchId: Int,
    val basicResearchField: BasicResearchField,
    val xCor: Double,
    val yCor: Double,
    val significance: Double,
    val difficulty: Double,
    val referenceBasicResearchIdList: List<Int>,
    val referenceAppliedResearchIdList: List<Int>,
)

enum class BasicResearchField(val value: String) {
    MATHEMATICS("Mathematics"),
    PHYSICS("Physics"),
    COMPUTER_SCIENCE("Computer science"),
    ENGINEERING("Engineering"),
    BIOMEDICAL_SCIENCE("Biomedical science"),
    LIFE_SCIENCE("Life science"),
    SOCIAL_SCIENCE("Social science"),
    HUMANITY("Humanity"),
    ;

    override fun toString(): String {
        return value
    }
}