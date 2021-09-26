package relativitization.universe.data.component.popsystem.pop.labourer

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.popsystem.pop.CommonPopData
import relativitization.universe.data.component.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.FactoryData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryData
import relativitization.universe.maths.collection.ListFind

@Serializable
data class LabourerPopData(
    val commonPopData: CommonPopData = CommonPopData(),
    val factoryMap: Map<Int, FactoryData> = mapOf(),
)

@Serializable
data class MutableLabourerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData(),
    var factoryMap: MutableMap<Int, MutableFactoryData> = mutableMapOf(),
) {
    fun addFactory(mutableFactoryData: MutableFactoryData) {
        val allIdList: List<Int> = factoryMap.keys.toList()
        val newId: Int = ListFind.minMissing(allIdList, 0)
        factoryMap[newId] = mutableFactoryData
    }
}