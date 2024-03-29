package relativitization.universe.game.data.components.defaults.popsystem.pop.service.export

import ksergen.annotations.GenerateImmutable
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.popsystem.pop.PopType

/**
 * Export center of pop
 *
 * @property exportDataMap map from carrier id in other player to pop type to a list of export
 * to consider,
 */
@GenerateImmutable
data class MutablePopExportCenterData(
    val exportDataMap: MutableMap<Int, MutableMap<PopType, MutableList<MutablePopSingleExportData>>> = mutableMapOf()
) {
    fun getOrPutSingleExportData(
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

        if (!hasData) {
            exportDataList.add(
                MutablePopSingleExportData(
                    resourceType = resourceType,
                    resourceQualityClass = resourceQualityClass,
                    amountPerTime = 0.0,
                    storedFuelRestMass = 0.0
                )
            )
        }

        return if (hasData) {
            exportDataList.first {
                (it.resourceType == resourceType) && (it.resourceQualityClass == resourceQualityClass)
            }
        } else {
            val default = MutablePopSingleExportData(
                resourceType = resourceType,
                resourceQualityClass = resourceQualityClass,
                amountPerTime = 0.0,
                storedFuelRestMass = 0.0
            )

            exportDataList.add(default)

            default
        }
    }

    fun clearExportData() {
        exportDataMap.values.forEach { popMap ->
            popMap.values.forEach { exportDataList ->
                exportDataList.removeAll { exportData ->
                    exportData.storedFuelRestMass <= 0.0
                }
            }

            popMap.values.removeAll { it.isEmpty() }
        }
        exportDataMap.values.removeAll { it.isEmpty() }
    }
}

fun PopExportCenterData.hasSingleExportData(
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

fun MutablePopExportCenterData.hasSingleExportData(
    carrierId: Int,
    popType: PopType,
    resourceType: ResourceType,
    resourceQualityClass: ResourceQualityClass
): Boolean {
    return if (exportDataMap.containsKey(carrierId) &&
        exportDataMap.getValue(carrierId).containsKey(popType)
    ) {
        val exportDataList: List<MutablePopSingleExportData> = exportDataMap.getValue(
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

fun PopExportCenterData.getSingleExportData(
    carrierId: Int,
    popType: PopType,
    resourceType: ResourceType,
    resourceQualityClass: ResourceQualityClass
): PopSingleExportData {
    return if (hasSingleExportData(carrierId, popType, resourceType, resourceQualityClass)) {
        exportDataMap.getValue(
            carrierId
        ).getValue(
            popType
        ).first {
            (it.resourceType == resourceType) && (it.resourceQualityClass == resourceQualityClass)
        }
    } else {
        DataSerializer.copy(MutablePopSingleExportData())
    }
}

fun MutablePopExportCenterData.getSingleExportData(
    carrierId: Int,
    popType: PopType,
    resourceType: ResourceType,
    resourceQualityClass: ResourceQualityClass
): MutablePopSingleExportData {
    return if (hasSingleExportData(carrierId, popType, resourceType, resourceQualityClass)) {
        exportDataMap.getValue(
            carrierId
        ).getValue(
            popType
        ).first {
            (it.resourceType == resourceType) && (it.resourceQualityClass == resourceQualityClass)
        }
    } else {
        MutablePopSingleExportData()
    }
}

@GenerateImmutable
data class MutablePopSingleExportData(
    var resourceType: ResourceType = ResourceType.PLANT,
    var resourceQualityClass: ResourceQualityClass = ResourceQualityClass.FIRST,
    var amountPerTime: Double = 0.0,
    var storedFuelRestMass: Double = 0.0,
)