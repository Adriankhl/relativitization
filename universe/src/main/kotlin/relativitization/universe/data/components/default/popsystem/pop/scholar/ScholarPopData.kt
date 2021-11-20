package relativitization.universe.data.components.default.popsystem.pop.scholar

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.default.popsystem.pop.CommonPopData
import relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.popsystem.pop.scholar.institute.InstituteData
import relativitization.universe.data.components.popsystem.pop.scholar.institute.MutableInstituteData
import relativitization.universe.maths.collection.ListFind

@Serializable
data class ScholarPopData(
    val commonPopData: relativitization.universe.data.components.default.popsystem.pop.CommonPopData = relativitization.universe.data.components.default.popsystem.pop.CommonPopData(),
    val instituteMap: Map<Int, InstituteData> = mapOf(),
)

@Serializable
data class MutableScholarPopData(
    var commonPopData: relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData = relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData(),
    var instituteMap: MutableMap<Int, MutableInstituteData> = mutableMapOf(),
) {
    fun addInstitute(mutableInstituteData: MutableInstituteData) {
        val newId: Int = ListFind.minMissing(instituteMap.keys.toList(), 0)
        instituteMap[newId] = mutableInstituteData
    }
}