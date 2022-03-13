package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData
import relativitization.universe.data.components.defaults.popsystem.CarrierData
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierInternalData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
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
        return carrierDataMap.values.sumOf { it.totalOtherRestMass() }
    }


    fun totalMaxDeltaFuelRestMass(): Double {
        return carrierDataMap.values.sumOf { it.carrierInternalData.maxMovementDeltaFuelRestMass }
    }

    fun totalAdultPopulation(): Double {
        return carrierDataMap.values.sumOf {
            it.allPopData.totalAdultPopulation()
        }
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
        return carrierDataMap.values.sumOf { it.totalOtherRestMass() }
    }

    fun totalMaxMovementDeltaFuelRestMass(): Double {
        return carrierDataMap.values.sumOf { it.carrierInternalData.maxMovementDeltaFuelRestMass }
    }

    fun totalAdultPopulation(): Double {
        return carrierDataMap.values.sumOf {
            it.allPopData.totalAdultPopulation()
        }
    }

    /**
     * Find the smallest non-negative carrier id which is not in the carrier list
     */
    private fun newCarrierId(): Int {
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

    /**
     * Compute average salary
     */
    fun averageSalary(): Double {
        val totalSalary: Double = carrierDataMap.values.fold(0.0) { acc, mutableCarrierData ->
            acc + PopType.values().sumOf { popType ->
                val commonPopData: MutableCommonPopData =
                    mutableCarrierData.allPopData.getCommonPopData(popType)
                commonPopData.salaryPerEmployee * commonPopData.adultPopulation *
                        commonPopData.employmentRate
            }
        }

        val totalAdultPopulation : Double = totalAdultPopulation()

        return if (totalAdultPopulation > 0.0) {
            totalSalary / totalAdultPopulation
        } else {
            0.0
        }
    }
}

fun PlayerInternalData.popSystemData(): PopSystemData =
    playerDataComponentMap.getOrDefault(PopSystemData::class, PopSystemData())

fun MutablePlayerInternalData.popSystemData(): MutablePopSystemData =
    playerDataComponentMap.getOrDefault(MutablePopSystemData::class, MutablePopSystemData())

fun MutablePlayerInternalData.popSystemData(newPopSystemData: MutablePopSystemData) =
    playerDataComponentMap.put(newPopSystemData)