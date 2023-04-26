package relativitization.universe.game.data.components.defaults.popsystem.pop.scholar.institute

import ksergen.annotations.GenerateImmutable

/**
 * Data of a research institute
 *
 * @property instituteInternalData the internal defining this institute
 * @property strength how good is this institute
 * @property reputation reputation of this institute
 * @property lastNumEmployee number of employee in the last round
 */
@GenerateImmutable
data class MutableInstituteData(
    var instituteInternalData: MutableInstituteInternalData = MutableInstituteInternalData(),
    var strength: Double = 0.0,
    var reputation: Double = 0.0,
    var lastNumEmployee: Double = 0.0,
)

/**
 * Internal data of a research institute
 *
 * @property xCor x coordinate of the center of the institute in the knowledge plane
 * @property yCor y coordinate of the center of the institute in the knowledge plane
 * @property range how far the institute cover the knowledge plane
 * @property researchEquipmentPerTime amount of research equipment provided per time
 * @property maxNumEmployee maximum number ofBuildInstituteCommand employee
 * @property size the size of this institute
 */
@GenerateImmutable
data class MutableInstituteInternalData(
    var xCor: Double = 0.0,
    var yCor: Double = 0.0,
    var range: Double = 1.0,
    var researchEquipmentPerTime: Double = 0.0,
    var maxNumEmployee: Double = 0.0,
    var size: Double = 0.0,
)