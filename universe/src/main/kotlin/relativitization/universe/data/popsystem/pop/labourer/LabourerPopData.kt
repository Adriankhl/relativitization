package relativitization.universe.data.popsystem.pop.labourer

import kotlinx.serialization.Serializable
import relativitization.universe.data.popsystem.pop.CommonPopData
import relativitization.universe.data.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.popsystem.pop.labourer.factory.BuildFactoryData
import relativitization.universe.data.popsystem.pop.labourer.factory.FactoryData
import relativitization.universe.data.popsystem.pop.labourer.factory.MutableBuildFactoryData
import relativitization.universe.data.popsystem.pop.labourer.factory.MutableFactoryData
import relativitization.universe.maths.collection.ListFind

@Serializable
data class LabourerPopData(
    val commonPopData: CommonPopData = CommonPopData(),
    val factoryMap: Map<Int, FactoryData> = mapOf(),
    val buildingFactoryMap: Map<Int, BuildFactoryData> = mapOf(),
)

@Serializable
data class MutableLabourerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData(),
    var factoryMap: MutableMap<Int, MutableFactoryData> = mutableMapOf(),
    var buildingFactoryMap: MutableMap<Int, MutableBuildFactoryData> = mutableMapOf(),
) {
    fun addFactory(mutableFactoryData: MutableFactoryData) {
        val allIdList: List<Int> = factoryMap.keys.toList() + buildingFactoryMap.keys.toList()
        val newId: Int = ListFind.minMissing(allIdList, 0)
        factoryMap[newId] = mutableFactoryData
    }

    fun addBuildFactory(mutableBuildFactoryData: MutableBuildFactoryData) {
        val allIdList: List<Int> = factoryMap.keys.toList() + buildingFactoryMap.keys.toList()
        val newId: Int = ListFind.minMissing(allIdList, 0)
        buildingFactoryMap[newId] = mutableBuildFactoryData
    }
}