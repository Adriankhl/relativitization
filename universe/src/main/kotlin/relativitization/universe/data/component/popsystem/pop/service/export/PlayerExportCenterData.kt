package relativitization.universe.data.component.popsystem.pop.service.export

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.ResourceQualityClass
import relativitization.universe.data.component.economy.ResourceType

@Serializable
data class PlayerExportCenterData(
    val exportDataList: List<PlayerExportCenterData> = listOf()
)

@Serializable
data class MutablePlayerExportCenterData(
    val exportDataList: MutableList<MutablePlayerExportCenterData> = mutableListOf()
)

@Serializable
data class PlayerSingleExportData(
    val resourceType: ResourceType,
    val resourceQualityClass: ResourceQualityClass,
    val amountPerTime: Double,
    val storedFuelRestMass: Double,
)

@Serializable
data class MutablePlayerSingleExportData(
    var resourceType: ResourceType,
    var resourceQualityClass: ResourceQualityClass,
    var amountPerTime: Double,
    var storedFuelRestMass: Double,
)