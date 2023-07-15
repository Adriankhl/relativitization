package relativitization.universe.game.data.components.defaults.popsystem

import ksergen.annotations.GenerateImmutable
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableAllPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.game.data.components.defaults.popsystem.pop.getCommonPopData

enum class CarrierType {
    STELLAR,
    SPACESHIP
}

@GenerateImmutable
data class MutableCarrierData(
    var carrierType: CarrierType = CarrierType.SPACESHIP,
    var carrierInternalData: MutableCarrierInternalData = MutableCarrierInternalData(),
    var allPopData: MutableAllPopData = MutableAllPopData(),
)

/**
 * The rest mass of the carrier except the core mass
 */
fun CarrierData.totalOtherRestMass(): Double {
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

    val popStoredFuelRestMass: Double = PopType.entries.sumOf { popType ->
        allPopData.getCommonPopData(popType).saving
    }

    return factoryStoredRestMass + exportCenterStoredFuelRestMass + popStoredFuelRestMass
}


fun MutableCarrierData.totalOtherRestMass(): Double {
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

    val popStoredFuelRestMass: Double = PopType.entries.sumOf { popType ->
        allPopData.getCommonPopData(popType).saving
    }

    return factoryStoredRestMass + exportCenterStoredFuelRestMass + popStoredFuelRestMass
}

@GenerateImmutable
data class MutableCarrierInternalData(
    var coreRestMass: Double = 1.0,
    var maxMovementDeltaFuelRestMass: Double = 0.0,
    var size: Double = 100.0,
    var idealPopulation: Double = 100.0,
)