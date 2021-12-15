package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.components.defaults.popsystem.CarrierData
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.maths.collection.ListFind
import relativitization.universe.maths.random.Rand

@Serializable
@SerialName("PopSystemData")
data class PopSystemData(
    val carrierDataMap: Map<Int, CarrierData> = mapOf(),
) : DefaultPlayerDataComponent() {
    fun totalCoreRestMass(): Double {
        return carrierDataMap.values.sumOf { it.carrierInternalData.coreRestMass }
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
            PopType.values().sumOf { popType ->
                mutableCarrierData.allPopData.getCommonPopData(popType).saving
            }
        }

        return factoryStoredFuelRestMass + exportCenterStoredFuelRestMass + popStoredFuelRestMass
    }


    fun totalMaxDeltaFuelRestMass(): Double {
        return carrierDataMap.values.sumOf { it.carrierInternalData.maxMovementDeltaFuelRestMass }
    }

    fun numCarrier() = carrierDataMap.values.size
}

@Serializable
@SerialName("PopSystemData")
data class MutablePopSystemData(
    val carrierDataMap: MutableMap<Int, MutableCarrierData> = mutableMapOf(),
) : MutableDefaultPlayerDataComponent() {
    fun totalCoreRestMass(): Double {
        return carrierDataMap.values.sumOf { it.carrierInternalData.coreRestMass }
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
            PopType.values().sumOf { popType ->
                mutableCarrierData.allPopData.getCommonPopData(popType).saving
            }
        }

        return factoryStoredFuelRestMass + exportCenterStoredFuelRestMass + popStoredFuelRestMass
    }

    fun totalMaxMovementDeltaFuelRestMass(): Double {
        return carrierDataMap.values.sumOf { it.carrierInternalData.maxMovementDeltaFuelRestMass }
    }

    /**
     * Find the smallest non-negative carrier id which is not in the carrier list
     */
    fun newCarrierId(): Int {
        return ListFind.minMissing(carrierDataMap.keys.toList(), 0)
    }

    fun addRandomStellarSystem() {
        val coreRestMass = Rand.rand().nextDouble(1.0e30, 2.5e30)
        val newCarrierInternalData = MutableCarrierInternalData(
            coreRestMass = coreRestMass,
            maxMovementDeltaFuelRestMass = 0.0,
            size = 100.0,
            idealPopulation = coreRestMass / 1E20
        )
        val newCarrier =
            MutableCarrierData(
                carrierType = CarrierType.STELLAR,
                carrierInternalData = newCarrierInternalData
            )
        carrierDataMap[newCarrierId()] = newCarrier
    }

    fun addStellarSystem(
        coreRestMass: Double,
    ) {
        val newCarrierInternalData = MutableCarrierInternalData(
            coreRestMass = coreRestMass,
            maxMovementDeltaFuelRestMass = 0.0,
            size = 100.0,
            idealPopulation = coreRestMass / 1E20
        )
        val newCarrier =
            MutableCarrierData(
                carrierType = CarrierType.STELLAR,
                carrierInternalData = newCarrierInternalData
            )
        carrierDataMap[newCarrierId()] = newCarrier

    }

    fun addSpaceShip(
        coreRestMass: Double,
        maxDeltaFuelRestMass: Double,
        idealPopulation: Double
    ) {
        val newCarrierInternalData = MutableCarrierInternalData(
            coreRestMass = coreRestMass,
            maxMovementDeltaFuelRestMass = maxDeltaFuelRestMass,
            size = 100.0,
            idealPopulation = idealPopulation,
        )
        val newCarrier =
            MutableCarrierData(
                carrierType = CarrierType.SPACESHIP,
                carrierInternalData = newCarrierInternalData,
            )
        carrierDataMap[newCarrierId()] = newCarrier
    }

    fun addCarrier(
        newCarrier: MutableCarrierData
    ) {
        carrierDataMap[newCarrierId()] = newCarrier
    }

    fun numCarrier(): Int = carrierDataMap.values.size
}