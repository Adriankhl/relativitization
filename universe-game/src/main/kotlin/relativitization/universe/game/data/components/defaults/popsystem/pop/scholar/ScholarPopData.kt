package relativitization.universe.game.data.components.defaults.popsystem.pop.scholar

import ksergen.annotations.GenerateImmutable
import relativitization.universe.core.maths.collection.ListFind
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.scholar.institute.MutableInstituteData

@GenerateImmutable
data class MutableScholarPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData(),
    var instituteMap: MutableMap<Int, MutableInstituteData> = mutableMapOf(),
) {
    fun addInstitute(mutableInstituteData: MutableInstituteData) {
        val newId: Int = ListFind.minMissing(instituteMap.keys.toList(), 0)
        instituteMap[newId] = mutableInstituteData
    }
}