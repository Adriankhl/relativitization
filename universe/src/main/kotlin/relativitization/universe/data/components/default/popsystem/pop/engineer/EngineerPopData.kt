package relativitization.universe.data.components.default.popsystem.pop.engineer

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.default.popsystem.pop.engineer.laboratory.LaboratoryData
import relativitization.universe.data.components.default.popsystem.pop.engineer.laboratory.MutableLaboratoryData
import relativitization.universe.maths.collection.ListFind

@Serializable
data class EngineerPopData(
    val commonPopData: relativitization.universe.data.components.default.popsystem.pop.CommonPopData = relativitization.universe.data.components.default.popsystem.pop.CommonPopData(),
    val laboratoryMap: Map<Int, LaboratoryData> = mapOf(),
)

@Serializable
data class MutableEngineerPopData(
    var commonPopData: relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData = relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData(),
    var laboratoryMap: MutableMap<Int, MutableLaboratoryData> = mutableMapOf(),
) {
    fun addLaboratory(mutableLaboratoryData: MutableLaboratoryData) {
        val newId: Int = ListFind.minMissing(laboratoryMap.keys.toList(), 0)
        laboratoryMap[newId] = mutableLaboratoryData
    }
}
