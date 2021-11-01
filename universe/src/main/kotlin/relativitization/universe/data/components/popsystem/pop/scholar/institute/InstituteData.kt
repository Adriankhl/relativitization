package relativitization.universe.data.components.popsystem.pop.scholar.institute

import kotlinx.serialization.Serializable

/**
 * Data of a research institute
 *
 * @property xCor x coordinate of the center of the institute in the knowledge plane
 * @property yCor y coordinate of the center of the institute in the knowledge plane
 * @property range how far the institute cover the knowledge plane
 * @property strength how good is this institute
 * @property reputation reputation of this institute
 * @property researchEquipmentPerTime amount of research equipment provided per time
 * @property maxNumEmployee maximum number of employee
 * @property lastNumEmployee number of employee in the last round
 * @property size the size of this institute
 */
@Serializable
data class InstituteData(
    val xCor: Double = 0.0,
    val yCor: Double = 0.0,
    val range: Double = 1.0,
    val strength: Double = 0.0,
    val reputation: Double = 0.0,
    val researchEquipmentPerTime: Double = 0.0,
    val maxNumEmployee: Double = 0.0,
    val lastNumEmployee: Double = 0.0,
    val size: Double = 0.0,
)

@Serializable
data class MutableInstituteData(
    var xCor: Double = 0.0,
    var yCor: Double = 0.0,
    var range: Double = 1.0,
    var strength: Double = 0.0,
    var reputation: Double = 0.0,
    var researchEquipmentPerTime: Double = 0.0,
    var maxNumEmployee: Double = 0.0,
    var lastNumEmployee: Double = 0.0,
    var size: Double = 0.0,
)