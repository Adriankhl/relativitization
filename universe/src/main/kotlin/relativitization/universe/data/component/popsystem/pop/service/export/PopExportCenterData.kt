package relativitization.universe.data.component.popsystem.pop.service.export

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.ResourceQualityClass
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.popsystem.pop.PopType

/**
 * Export center of pop
 */
@Serializable
data class PopExportCenterData(
    val exportDataMap: Map<Int, Map<PopType, List<PopSingleExportData>>> = mapOf()
) {
    fun hasSingleExportData(
        carrierId: Int,
        popType: PopType,
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Boolean {
        return if (exportDataMap.containsKey(carrierId) &&
            exportDataMap.getValue(carrierId).containsKey(popType)
        ) {
            val exportDataList: List<PopSingleExportData> = exportDataMap.getValue(
                carrierId
            ).getValue(
                popType
            )

            exportDataList.any {
                (it.resourceType == resourceType) && (it.resourceQualityClass == resourceQualityClass)
            }
        } else {
            false
        }
    }

    fun getSingleExportData(
        carrierId: Int,
        popType: PopType,
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): PopSingleExportData {
        return if(hasSingleExportData(carrierId, popType, resourceType, resourceQualityClass)) {
            exportDataMap.getValue(
                carrierId
            ).getValue(
                popType
            ).first {
                (it.resourceType == resourceType) && (it.resourceQualityClass == resourceQualityClass)
            }
        } else {
            PopSingleExportData()
        }
    }
}

@Serializable
data class MutablePopExportCenterData(
    val exportDataMap: MutableMap<Int, MutableMap<PopType, MutableList<MutablePopSingleExportData>>> = mutableMapOf()
) {
    fun getSingleExportData(
        playerId: Int,
        carrierId: Int,
        popType: PopType,
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutablePopSingleExportData {
        val exportDataList: MutableList<MutablePopSingleExportData> = exportDataMap.getOrPut(
            carrierId
        ) { mutableMapOf() }.getOrPut(
                popType
        ) {
            mutableListOf()
        }

        val hasData: Boolean = exportDataList.any {
            (it.resourceType == resourceType) && (it.resourceQualityClass == resourceQualityClass)
        }

        if(!hasData) {
            exportDataList.add(
                MutablePopSingleExportData(
                    playerId = playerId,
                    carrierId = carrierId,
                    popType = popType,
                    resourceType = resourceType,
                    resourceQualityClass = resourceQualityClass,
                    amountPerTime = 0.0,
                    storedFuelRestMass = 0.0
                )
            )
        }

        return exportDataList.first {
            (it.resourceType == resourceType) && (it.resourceQualityClass == resourceQualityClass)
        }
    }
}

@Serializable
data class PopSingleExportData(
    val playerId: Int = -1,
    val carrierId: Int = -1,
    val popType: PopType = PopType.LABOURER,
    val resourceType: ResourceType = ResourceType.PLANT,
    val resourceQualityClass: ResourceQualityClass = ResourceQualityClass.FIRST,
    val amountPerTime: Double = 0.0,
    val storedFuelRestMass: Double = 0.0,
)

@Serializable
data class MutablePopSingleExportData(
    var playerId: Int = -1,
    var carrierId: Int = -1,
    var popType: PopType = PopType.LABOURER,
    var resourceType: ResourceType = ResourceType.PLANT,
    var resourceQualityClass: ResourceQualityClass = ResourceQualityClass.FIRST,
    var amountPerTime: Double = 0.0,
    var storedFuelRestMass: Double = 0.0,
)