package relativitization.universe.data.component.popsystem.pop.labourer

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.popsystem.pop.CommonPopData
import relativitization.universe.data.component.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.ResourceFactoryData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableResourceFactoryData
import relativitization.universe.maths.collection.ListFind

@Serializable
data class LabourerPopData(
    val commonPopData: CommonPopData = CommonPopData(),
    val resourceFactoryMap: Map<Int, ResourceFactoryData> = mapOf(),
)

@Serializable
data class MutableLabourerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData(),
    var resourceFactoryMap: MutableMap<Int, MutableResourceFactoryData> = mutableMapOf(),
) {
    fun addFactory(mutableResourceFactoryData: MutableResourceFactoryData) {
        val allIdList: List<Int> = resourceFactoryMap.keys.toList()
        val newId: Int = ListFind.minMissing(allIdList, 0)
        resourceFactoryMap[newId] = mutableResourceFactoryData
    }
}