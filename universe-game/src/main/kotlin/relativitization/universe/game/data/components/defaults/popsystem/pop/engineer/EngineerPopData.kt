package relativitization.universe.game.data.components.defaults.popsystem.pop.engineer

import ksergen.annotations.GenerateImmutable
import relativitization.universe.core.maths.collection.ListFind
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.engineer.laboratory.MutableLaboratoryData

@GenerateImmutable
data class MutableEngineerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData(),
    var laboratoryMap: MutableMap<Int, MutableLaboratoryData> = mutableMapOf(),
) {
    fun addLaboratory(mutableLaboratoryData: MutableLaboratoryData) {
        val newId: Int = ListFind.minMissing(laboratoryMap.keys.toList(), 0)
        laboratoryMap[newId] = mutableLaboratoryData
    }
}
