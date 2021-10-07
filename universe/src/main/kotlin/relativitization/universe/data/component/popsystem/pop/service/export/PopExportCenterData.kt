package relativitization.universe.data.component.popsystem.pop.service.export

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.ResourceQualityClass
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.popsystem.pop.PopType

@Serializable
data class PopExportCenterData(
    val exportDataMap: Map<Int, Map<PopType, List<PopSingleExportData>>> = mapOf()
)

@Serializable
data class MutablePopExportCenterData(
    val exportDataMap: MutableMap<Int, MutableMap<PopType, MutableList<PopSingleExportData>>> = mutableMapOf()
)

@Serializable
data class PopSingleExportData(
    val playerId: Int,
    val carrierId: Int,
    val popType: PopType,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val amountPerTime: Double,
    val storedFuelRestMass: Double,
)

@Serializable
data class MutablePopSingleExportData(
    var playerId: Int,
    var carrierId: Int,
    var popType: PopType,
    var resourceType: ResourceType,
    var resourceQualityClass: ResourceQualityClass,
    var amountPerTime: Double,
    var storedFuelRestMass: Double,
)