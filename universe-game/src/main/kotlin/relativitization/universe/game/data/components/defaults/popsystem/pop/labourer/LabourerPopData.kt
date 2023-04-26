package relativitization.universe.game.data.components.defaults.popsystem.pop.labourer

import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable
import relativitization.universe.game.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.FuelFactoryData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.MutableFuelFactoryData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.MutableResourceFactoryData
import relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory.ResourceFactoryData
import relativitization.universe.core.maths.collection.ListFind

@GenerateImmutable
data class MutableLabourerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData(),
    val fuelFactoryMap: MutableMap<Int, MutableFuelFactoryData> = mutableMapOf(),
    val resourceFactoryMap: MutableMap<Int, MutableResourceFactoryData> = mutableMapOf(),
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