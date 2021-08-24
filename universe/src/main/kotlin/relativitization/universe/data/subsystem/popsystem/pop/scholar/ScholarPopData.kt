package relativitization.universe.data.subsystem.popsystem.pop.scholar

import kotlinx.serialization.Serializable
import relativitization.universe.data.subsystem.popsystem.pop.CommonPopData
import relativitization.universe.data.subsystem.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.subsystem.popsystem.pop.scholar.institute.InstituteData
import relativitization.universe.data.subsystem.popsystem.pop.scholar.institute.MutableInstituteData
import relativitization.universe.maths.collection.ListFind

@Serializable
data class ScholarPopData(
    val commonPopData: CommonPopData = CommonPopData(),
    val instituteMap: Map<Int, InstituteData> = mapOf(),
)

@Serializable
data class MutableScholarPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData(),
    var instituteMap: MutableMap<Int, MutableInstituteData> = mutableMapOf(),
) {
    fun addInstitute(mutableInstituteData: MutableInstituteData) {
        val newId: Int = ListFind.minMissing(instituteMap.keys.toList(), 0)
        instituteMap[newId] = mutableInstituteData
    }
}