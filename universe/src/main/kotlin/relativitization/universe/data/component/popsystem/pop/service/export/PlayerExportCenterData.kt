package relativitization.universe.data.component.popsystem.pop.service.export

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.ResourceQualityClass
import relativitization.universe.data.component.economy.ResourceType

@Serializable
data class PlayerExportCenterData(
    val exportDataList: List<PlayerSingleExportData> = listOf()
)

@Serializable
data class MutablePlayerExportCenterData(
    val exportDataList: MutableList<MutablePlayerSingleExportData> = mutableListOf()
)

@Serializable
data class PlayerSingleExportData(
    val playerId: Int,
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val amountPerTime: Double,
    val storedFuelRestMass: Double,
)

@Serializable
data class MutablePlayerSingleExportData(
    var playerId: Int,
    var resourceType: ResourceType,
    var resourceQualityClass: ResourceQualityClass,
    var amountPerTime: Double,
    var storedFuelRestMass: Double,
)