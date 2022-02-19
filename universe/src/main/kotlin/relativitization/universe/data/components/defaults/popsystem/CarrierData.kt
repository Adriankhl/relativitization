package relativitization.universe.data.components.defaults.popsystem

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.defaults.popsystem.pop.AllPopData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableAllPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType

enum class CarrierType {
    STELLAR,
    SPACESHIP
}

@Serializable
data class CarrierData(
    val carrierType: CarrierType = CarrierType.SPACESHIP,
    val carrierInternalData: CarrierInternalData = CarrierInternalData(),
    val allPopData: AllPopData = AllPopData(),
) {
    /**
     * The rest mass of the carrier except the core mass
     */
    fun totalOtherRestMass(): Double {
        val factoryStoredRestMass: Double =
            allPopData.labourerPopData.resourceFactoryMap.values.sumOf { resourceFactoryData ->
                resourceFactoryData.storedFuelRestMass
            } + allPopData.labourerPopData.fuelFactoryMap.values.sumOf { fuelFactoryData ->
                fuelFactoryData.storedFuelRestMass
            }

        val exportCenterStoredFuelRestMass: Double =
            allPopData.servicePopData.exportData.playerExportCenterMap.values.sumOf { playerExportCenterData ->
                playerExportCenterData.exportDataList.sumOf { playerSingleExportData ->
                    playerSingleExportData.storedFuelRestMass
                }
            } + allPopData.servicePopData.exportData.popExportCenterMap.values.sumOf { popExportCenterData ->
                popExportCenterData.exportDataMap.values.sumOf { exportMap ->
                    exportMap.values.flatten().sumOf { popSingleExportData ->
                        popSingleExportData.storedFuelRestMass
                    }
                }
            }

        val popStoredFuelRestMass: Double = PopType.values().sumOf { popType ->
            allPopData.getCommonPopData(popType).saving
        }

        return factoryStoredRestMass + exportCenterStoredFuelRestMass + popStoredFuelRestMass
    }
}

@Serializable
data class MutableCarrierData(
    var carrierType: CarrierType = CarrierType.SPACESHIP,
    var carrierInternalData: MutableCarrierInternalData = MutableCarrierInternalData(),
    var allPopData: MutableAllPopData = MutableAllPopData(),
) {
    fun totalOtherRestMass(): Double {
        val factoryStoredRestMass: Double =
            allPopData.labourerPopData.resourceFactoryMap.values.sumOf { resourceFactoryData ->
                resourceFactoryData.storedFuelRestMass
            } + allPopData.labourerPopData.fuelFactoryMap.values.sumOf { fuelFactoryData ->
                fuelFactoryData.storedFuelRestMass
            }

        val exportCenterStoredFuelRestMass: Double =
            allPopData.servicePopData.exportData.playerExportCenterMap.values.sumOf { playerExportCenterData ->
                playerExportCenterData.exportDataList.sumOf { playerSingleExportData ->
                    playerSingleExportData.storedFuelRestMass
                }
            } + allPopData.servicePopData.exportData.popExportCenterMap.values.sumOf { popExportCenterData ->
                popExportCenterData.exportDataMap.values.sumOf { exportMap ->
                    exportMap.values.flatten().sumOf { popSingleExportData ->
                        popSingleExportData.storedFuelRestMass
                    }
                }
            }

        val popStoredFuelRestMass: Double = PopType.values().sumOf { popType ->
            allPopData.getCommonPopData(popType).saving
        }

        return factoryStoredRestMass + exportCenterStoredFuelRestMass + popStoredFuelRestMass
    }
}

@Serializable
data class CarrierInternalData(
    val coreRestMass: Double = 1.0,
    val maxMovementDeltaFuelRestMass: Double = 0.0,
    val size: Double = 100.0,
    val idealPopulation: Double = 100.0,
)

@Serializable
data class MutableCarrierInternalData(
    var coreRestMass: Double = 1.0,
    var maxMovementDeltaFuelRestMass: Double = 0.0,
    var size: Double = 100.0,
    var idealPopulation: Double = 100.0,
)