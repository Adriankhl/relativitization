package relativitization.universe.game.data.components.defaults.popsystem.pop.service.export

import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable
import relativitization.universe.game.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.game.data.components.defaults.economy.ResourceType

@GenerateImmutable
data class MutablePlayerExportCenterData(
    val exportDataList: MutableList<MutablePlayerSingleExportData> = mutableListOf()
) {
    fun getSingleExportData(
        targetPlayerId: Int,
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutablePlayerSingleExportData {
        val hasData: Boolean = exportDataList.any {
            (it.targetPlayerId == targetPlayerId) &&
                    (it.resourceType == resourceType) &&
                    (it.resourceQualityClass == resourceQualityClass)
        }


        return if (hasData) {
            exportDataList.first {
                (it.targetPlayerId == targetPlayerId) &&
                        (it.resourceType == resourceType) &&
                        (it.resourceQualityClass == resourceQualityClass)
            }
        } else {
            val default = MutablePlayerSingleExportData(
                targetPlayerId = targetPlayerId,
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
        exportDataList.removeAll {
            it.storedFuelRestMass <= 0.0
        }
    }
}

/**
 * Get resource type by target player id, empty list if export data does not exist
 */
fun PlayerExportCenterData.getResourceTypeList(
    targetPlayerId: Int,
): List<ResourceType> {
    return exportDataList.filter {
        it.targetPlayerId == targetPlayerId
    }.map { it.resourceType }.toSet().toList()
}

/**
 * Get resource type by target player id, empty list if export data does not exist
 */
fun MutablePlayerExportCenterData.getResourceTypeList(
    targetPlayerId: Int,
): List<ResourceType> {
    return exportDataList.filter {
        it.targetPlayerId == targetPlayerId
    }.map { it.resourceType }.toSet().toList()
}

/**
 * Get resource type by target player id and resource type,
 * empty list if export data does not exist
 */
fun PlayerExportCenterData.getResourceQualityClassList(
    targetPlayerId: Int,
    resourceType: ResourceType
): List<ResourceQualityClass> {
    return exportDataList.filter {
        (it.targetPlayerId == targetPlayerId) && (it.resourceType == resourceType)
    }.map { it.resourceQualityClass }.toSet().toList()
}

/**
 * Get resource type by target player id and resource type,
 * empty list if export data does not exist
 */
fun MutablePlayerExportCenterData.getResourceQualityClassList(
    targetPlayerId: Int,
    resourceType: ResourceType
): List<ResourceQualityClass> {
    return exportDataList.filter {
        (it.targetPlayerId == targetPlayerId) && (it.resourceType == resourceType)
    }.map { it.resourceQualityClass }.toSet().toList()
}

/**
 * Get resource type by target player id and resource type,
 * empty list if export data does not exist
 */
fun PlayerExportCenterData.getExportDataList(
    targetPlayerId: Int,
    resourceType: ResourceType,
    resourceQualityClass: ResourceQualityClass
): List<PlayerSingleExportData> {
    return exportDataList.filter {
        (it.targetPlayerId == targetPlayerId) &&
                (it.resourceType == resourceType) &&
                (it.resourceQualityClass == resourceQualityClass)
    }
}

/**
 * Get resource type by target player id and resource type,
 * empty list if export data does not exist
 */
fun MutablePlayerExportCenterData.getExportDataList(
    targetPlayerId: Int,
    resourceType: ResourceType,
    resourceQualityClass: ResourceQualityClass
): List<MutablePlayerSingleExportData> {
    return exportDataList.filter {
        (it.targetPlayerId == targetPlayerId) &&
                (it.resourceType == resourceType) &&
                (it.resourceQualityClass == resourceQualityClass)
    }
}

/**
 * Store the data of exporting a single resource to a player
 *
 * @property targetPlayerId the target player of the export, does not necessarily equal to the owner
 * @property resourceType the type of export resource
 * @property resourceQualityClass the quality class of the export resource
 * @property amountPerTime the amount of the export per turn
 * @property storedFuelRestMass the stored fuel rest mass in this center for buying the resource
 */
@GenerateImmutable
data class MutablePlayerSingleExportData(
    var targetPlayerId: Int,
    var resourceType: ResourceType,
    var resourceQualityClass: ResourceQualityClass,
    var amountPerTime: Double,
    var storedFuelRestMass: Double,
)