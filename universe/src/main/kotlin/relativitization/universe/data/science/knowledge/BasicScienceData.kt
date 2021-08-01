package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable

@Serializable
data class BasicScienceData(
    val basicScienceId: Int,
    val basicScienceField: BasicScienceField,
    val xCor: Double,
    val yCor: Double,
    val difficulty: Double,
    val referenceKnowledgeIdList: List<Int>,
    val referenceTechnologyIdList: List<Int>,
)

enum class BasicScienceField(val value: String) {
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