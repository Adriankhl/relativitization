package relativitization.universe.data.components.default.popsystem.pop.labourer

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.default.popsystem.pop.labourer.factory.FuelFactoryData
import relativitization.universe.data.components.default.popsystem.pop.labourer.factory.MutableFuelFactoryData
import relativitization.universe.data.components.default.popsystem.pop.labourer.factory.MutableResourceFactoryData
import relativitization.universe.data.components.default.popsystem.pop.labourer.factory.ResourceFactoryData
import relativitization.universe.maths.collection.ListFind

@Serializable
data class LabourerPopData(
    val commonPopData: relativitization.universe.data.components.default.popsystem.pop.CommonPopData = relativitization.universe.data.components.default.popsystem.pop.CommonPopData(),
    val resourceFactoryMap: Map<Int, ResourceFactoryData> = mapOf(),
    val fuelFactoryMap: Map<Int, FuelFactoryData> = mapOf(),
)

@Serializable
data class MutableLabourerPopData(
    var commonPopData: relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData = relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData(),
    val resourceFactoryMap: MutableMap<Int, MutableResourceFactoryData> = mutableMapOf(),
    val fuelFactoryMap: MutableMap<Int, MutableFuelFactoryData> = mutableMapOf(),
) {
    fun addResourceFactory(mutableResourceFactoryData: MutableResourceFactoryData) {
        val allIdList: List<Int> = resourceFactoryMap.keys.toList()
        val newId: Int = ListFind.minMissing(allIdList, 0)
        resourceFactoryMap[newId] = mutableResourceFactoryData
    }

    fun addFuelFactory(mutableFuelFactoryData: MutableFuelFactoryData) {
        val allIdList: List<Int> = fuelFactoryMap.keys.toList()
        val newId: Int = ListFind.minMissing(allIdList, 0)
        fuelFactoryMap[newId] = mutableFuelFactoryData
    }
}