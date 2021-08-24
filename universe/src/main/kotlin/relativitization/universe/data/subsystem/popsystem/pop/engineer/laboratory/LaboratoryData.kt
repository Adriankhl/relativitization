package relativitization.universe.data.subsystem.popsystem.pop.engineer.laboratory

import kotlinx.serialization.Serializable
import relativitization.universe.data.subsystem.economy.MutableResourceQualityData
import relativitization.universe.data.subsystem.economy.ResourceQualityData

/**
 * Data of a research laboratory
 *
 * @property xCor x coordinate of the center of the institute in the knowledge plane
 * @property yCor y coordinate of the center of the institute in the knowledge plane
 * @property range how far the institute cover the knowledge plane
 * @property strength how good is this institute
 * @property reputation reputation of this institute
 * @property requiredResearchEquipmentQuality the required quality of research equipment
 * @property requiredResearchEquipmentAmount required amount of research equipment
 * @property fuelRestMassConsumptionRate fuel consumption rate
 * @property maxNumEmployee maximum number of employee
 * @property lastNumEmployee number of employee in the last round
 * @property size the size of this institute
 */
@Serializable
data class LaboratoryData(
    val xCor: Double = 0.0,
    val yCor: Double = 0.0,
    val range: Double = 0.0,
    val strength: Double = 0.0,
    val reputation: Double = 0.0,
    val requiredResearchEquipmentQuality: ResourceQualityData = ResourceQualityData(),
    val requiredResearchEquipmentAmount: Double = 0.0,
    val fuelRestMassConsumptionRate: Double = 0.0,
    val maxNumEmployee: Double = 0.0,
    val lastNumEmployee: Double = 0.0,
    val size: Double = 0.0,
)

@Serializable
data class MutableLaboratoryData(
    var xCor: Double = 0.0,
    var yCor: Double = 0.0,
    var range: Double = 0.0,
    var strength: Double = 0.0,
    var reputation: Double = 0.0,
    var requiredResearchEquipmentQuality: MutableResourceQualityData = MutableResourceQualityData(),
    var requiredResearchEquipmentAmount: Double = 0.0,
    var fuelRestMassConsumptionRate: Double = 0.0,
    var maxNumEmployee: Double = 0.0,
    var lastNumEmployee: Double = 0.0,
    var size: Double = 0.0,
)
