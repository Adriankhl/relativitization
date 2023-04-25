package relativitization.universe.game.data.components.defaults.popsystem.pop.engineer

import kotlinx.serialization.Serializable
import relativitization.universe.game.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.engineer.laboratory.LaboratoryData
import relativitization.universe.game.data.components.defaults.popsystem.pop.engineer.laboratory.MutableLaboratoryData
import relativitization.universe.game.maths.collection.ListFind

@Serializable
data class EngineerPopData(
    val commonPopData: CommonPopData = CommonPopData(),
    val laboratoryMap: Map<Int, LaboratoryData> = mapOf(),
)

@Serializable
data class MutableEngineerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData(),
    var laboratoryMap: MutableMap<Int, MutableLaboratoryData> = mutableMapOf(),
) {
    fun addLaboratory(mutableLaboratoryData: MutableLaboratoryData) {
        val newId: Int = ListFind.minMissing(laboratoryMap.keys.toList(), 0)
        laboratoryMap[newId] = mutableLaboratoryData
    }
}
