package relativitization.universe.data.component.popsystem.pop.service.export

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.ResourceQualityClass
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.popsystem.pop.PopType
import kotlin.math.exp

@Serializable
data class PlayerExportCenterData(
    val exportDataList: List<PlayerSingleExportData> = listOf()
)

@Serializable
data class MutablePlayerExportCenterData(
    val exportDataList: MutableList<MutablePlayerSingleExportData> = mutableListOf()
) {
    fun getSingleExportData(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutablePlayerSingleExportData {
        val hasData: Boolean = exportDataList.any {
            (it.resourceType == resourceType) && (it.resourceQualityClass == resourceQualityClass)
        }


        return if(hasData) {
            exportDataList.first {
                (it.resourceType == resourceType) && (it.resourceQualityClass == resourceQualityClass)
            }
        } else {
            val default =  MutablePlayerSingleExportData(
                resourceType = resourceType,
                resourceQualityClass = resourceQualityClass,
                amountPerTime = 0.0,
                storedFuelRestMass = 0.0
            )

            exportDataList.add(default)

            default
        }
    }
}

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