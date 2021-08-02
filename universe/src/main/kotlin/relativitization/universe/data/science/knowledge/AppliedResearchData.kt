package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable

/**
 * Classification of applied research field
 */
enum class AppliedResearchField(val value: String) {
    FOOD_TECHNOLOGY("Food technology"),
    BIOMEDICAL_TECHNOLOGY("Biomedical technology"),
    CHEMICAL_TECHNOLOGY("Chemical technology"),
    ENVIRONMENTAL_TECHNOLOGY("Environmental technology"),
    ARCHITECTURE_TECHNOLOGY("Architecture technology"),
    MACHINERY_TECHNOLOGY("Machinery technology"),
    MATERIAL_TECHNOLOGY("Material technology"),
    ENERGY_TECHNOLOGY("Energy technology"),
    INFORMATION_TECHNOLOGY("Information technology"),
    ART_TECHNOLOGY("Art technology"),
    MILITARY_TECHNOLOGY("Military technology"),
    ;

    override fun toString(): String {
        return value
    }
}

/**
 * A single applied research project
 */
@Serializable
data class AppliedResearchProjectData(
    val appliedResearchId: Int,
    val appliedResearchField: AppliedResearchField,
    val xCor: Double,
    val yCor: Double,
    val difficulty: Double,
    val significance: Double,
    val referenceBasicResearchIdList: List<Int>,
    val referenceAppliedResearchIdList: List<Int>
)

/**
 * Level of applied research
 */
@Serializable
data class AppliedResearchData(
    val foodTechnologyLevel: Double = 0.0,
    val biomedicalTechnologyLevel: Double = 0.0,
    val chemicalTechnologyLevel: Double = 0.0,
    val environmentalTechnologyLevel: Double = 0.0,
    val architectureTechnologyLevel: Double = 0.0,
    val machineryTechnologyLevel: Double = 0.0,
    val materialTechnologyLevel: Double = 0.0,
    val energyTechnologyLevel: Double = 0.0,
    val informationTechnologyLevel: Double = 0.0,
    val artTechnologyLevel: Double = 0.0,
    val militaryTechnologyLevel: Double = 0.0,
)

@Serializable
data class MutableAppliedResearchData(
    var foodTechnologyLevel: Double = 0.0,
    var biomedicalTechnologyLevel: Double = 0.0,
    var chemicalTechnologyLevel: Double = 0.0,
    var environmentalTechnologyLevel: Double = 0.0,
    var architectureTechnologyLevel: Double = 0.0,
    var machineryTechnologyLevel: Double = 0.0,
    var materialTechnologyLevel: Double = 0.0,
    var energyTechnologyLevel: Double = 0.0,
    var informationTechnologyLevel: Double = 0.0,
    var artTechnologyLevel: Double = 0.0,
    var militaryTechnologyLevel: Double = 0.0,
)