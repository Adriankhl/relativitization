package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.components.popsystem.*
import relativitization.universe.data.components.default.popsystem.pop.PopType
import relativitization.universe.data.components.popsystem.pop.labourer.factory.MutableFuelFactoryData
import relativitization.universe.data.components.popsystem.pop.service.export.MutablePlayerExportCenterData
import relativitization.universe.data.components.popsystem.pop.service.export.MutablePlayerSingleExportData
import relativitization.universe.data.components.popsystem.pop.service.export.MutablePopExportCenterData
import relativitization.universe.data.components.popsystem.pop.service.export.MutablePopSingleExportData
import relativitization.universe.maths.collection.ListFind
import kotlin.random.Random

@Serializable
@SerialName("PopSystemData")
data class PopSystemData(
    val carrierDataMap: Map<Int, relativitization.universe.data.components.default.popsystem.CarrierData> = mapOf(),
) : PlayerDataComponent() {
    fun totalCoreRestMass(): Double {
        return carrierDataMap.values.sumOf { it.coreRestMass }
    }

    fun totalOtherRestMass(): Double {
        val factoryStoredFuelRestMass: Double = carrierDataMap.values.sumOf { carrierData ->
            carrierData.allPopData.labourerPopData.resourceFactoryMap.values.sumOf { resourceFactoryData ->
                resourceFactoryData.storedFuelRestMass
            } + carrierData.allPopData.labourerPopData.fuelFactoryMap.values.sumOf { fuelFactoryData ->
                fuelFactoryData.storedFuelRestMass
            }
        }

        val exportCenterStoredFuelRestMass: Double = carrierDataMap.values.sumOf { carrierData ->
            carrierData.allPopData.servicePopData.exportData.playerExportCenterMap.values.sumOf { playerExportCenterData ->
                playerExportCenterData.exportDataList.sumOf { playerSingleExportData ->
                    playerSingleExportData.storedFuelRestMass
                }
            } + carrierData.allPopData.servicePopData.exportData.popExportCenterMap.values.sumOf { popExportCenterData ->
                popExportCenterData.exportDataMap.values.sumOf { exportMap ->
                    exportMap.values.flatten().sumOf { popSingleExportData ->
                        popSingleExportData.storedFuelRestMass
                    }
                }
            }
        }

        val popStoredFuelRestMass: Double = carrierDataMap.values.sumOf { mutableCarrierData ->
            relativitization.universe.data.components.default.popsystem.pop.PopType.values().sumOf { popType ->
                mutableCarrierData.allPopData.getCommonPopData(popType).saving
            }
        }

        return factoryStoredFuelRestMass + exportCenterStoredFuelRestMass + popStoredFuelRestMass
    }


    fun totalMaxDeltaFuelRestMass(): Double {
        return carrierDataMap.values.sumOf { it.maxMovementDeltaFuelRestMass }
    }

    fun numCarrier() = carrierDataMap.values.size
}

@Serializable @SerialName("PopSystemData")
data class MutablePopSystemData(
    val carrierDataMap: MutableMap<Int, relativitization.universe.data.components.default.popsystem.MutableCarrierData> = mutableMapOf(),
) : MutablePlayerDataComponent() {
    fun totalCoreRestMass(): Double {
        return carrierDataMap.values.sumOf { it.coreRestMass }
    }

    fun totalOtherRestMass(): Double {
        val factoryStoredFuelRestMass: Double = carrierDataMap.values.sumOf { carrierData ->
            carrierData.allPopData.labourerPopData.resourceFactoryMap.values.sumOf { resourceFactoryData ->
                resourceFactoryData.storedFuelRestMass
            } + carrierData.allPopData.labourerPopData.fuelFactoryMap.values.sumOf { fuelFactoryData ->
                fuelFactoryData.storedFuelRestMass
            }
        }

        val exportCenterStoredFuelRestMass: Double = carrierDataMap.values.sumOf { carrierData ->
            carrierData.allPopData.servicePopData.exportData.playerExportCenterMap.values.sumOf { playerExportCenterData ->
                playerExportCenterData.exportDataList.sumOf { playerSingleExportData ->
                    playerSingleExportData.storedFuelRestMass
                }
            } + carrierData.allPopData.servicePopData.exportData.popExportCenterMap.values.sumOf { popExportCenterData ->
                popExportCenterData.exportDataMap.values.sumOf { exportMap ->
                    exportMap.values.flatten().sumOf { popSingleExportData ->
                        popSingleExportData.storedFuelRestMass
                    }
                }
            }
        }

        val popStoredFuelRestMass: Double = carrierDataMap.values.sumOf { mutableCarrierData ->
            relativitization.universe.data.components.default.popsystem.pop.PopType.values().sumOf { popType ->
                mutableCarrierData.allPopData.getCommonPopData(popType).saving
            }
        }

        return factoryStoredFuelRestMass + exportCenterStoredFuelRestMass + popStoredFuelRestMass
    }

    fun totalMaxMovementDeltaFuelRestMass(): Double {
        return carrierDataMap.values.sumOf { it.maxMovementDeltaFuelRestMass }
    }

    /**
     * Find the smallest non-negative carrier id which is not in the carrier list
     */
    fun newCarrierId(): Int {
        return ListFind.minMissing(carrierDataMap.keys.toList(), 0)
    }

    fun addRandomStellarSystem() {
        val restMass = Random.nextDouble(1.0e30, 2.5e30)
        val newCarrier =
            relativitization.universe.data.components.default.popsystem.MutableCarrierData(
                coreRestMass = restMass,
                carrierType = relativitization.universe.data.components.default.popsystem.CarrierType.STELLAR
            )
        carrierDataMap[newCarrierId()] = newCarrier
    }

    fun addStellarSystem(
        coreRestMass: Double
    ) {
        val newCarrier =
            relativitization.universe.data.components.default.popsystem.MutableCarrierData(
                coreRestMass = coreRestMass,
                carrierType = relativitization.universe.data.components.default.popsystem.CarrierType.STELLAR
            )
        carrierDataMap[newCarrierId()] = newCarrier

    }

    fun addSpaceShip(
        coreRestMass: Double,
        maxDeltaFuelRestMass: Double
    ) {
        val newCarrier =
            relativitization.universe.data.components.default.popsystem.MutableCarrierData(
                coreRestMass = coreRestMass,
                carrierType = relativitization.universe.data.components.default.popsystem.CarrierType.SPACESHIP,
                maxMovementDeltaFuelRestMass = maxDeltaFuelRestMass
            )
        carrierDataMap[newCarrierId()] = newCarrier
    }

    fun numCarrier(): Int = carrierDataMap.values.size
}