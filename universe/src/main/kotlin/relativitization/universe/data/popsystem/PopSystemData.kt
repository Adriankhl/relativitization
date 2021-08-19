package relativitization.universe.data.popsystem

import kotlinx.serialization.Serializable
import relativitization.universe.maths.collection.ListFind
import kotlin.random.Random

@Serializable
data class PopSystemicData(
    val carrierList: List<Carrier> = listOf()
) {
    fun totalCoreRestMass(): Double {
        return carrierList.sumOf { it.coreRestMass }
    }

    fun totalFuelRestMass(): Double {
        return carrierList.sumOf { it.fuelRestMass }
    }

    fun totalMaxDeltaFuelRestMass(): Double {
        return carrierList.sumOf { it.maxDeltaFuelRestMass }
    }
}

@Serializable
data class MutablePopSystemicData(
    val carrierList: MutableList<MutableCarrier> = mutableListOf()
) {
    fun totalCoreRestMass(): Double {
        return carrierList.sumOf { it.coreRestMass }
    }

    fun totalFuelRestMass(): Double {
        return carrierList.sumOf { it.fuelRestMass }
    }

    fun totalMaxDeltaFuelRestMass(): Double {
        return carrierList.sumOf { it.maxDeltaFuelRestMass }
    }

    /**
     * Find the smallest non-negative carrier id which is not in the carrier list
     */
    fun newCarrierId(): Int {
        return ListFind.minMissing(carrierList.map { it.carrierId }, 0)
        val sortedList: List<MutableCarrier> = carrierList.sortedBy { it.carrierId }
        return sortedList.fold(0) { index, carrier ->
            if (index == carrier.carrierId) {
                index + 1
            } else {
                index
            }
        }
    }

    fun addRandomStellarSystem() {
        val restMass = Random.nextDouble(1.0e30, 2.5e30)
        carrierList.add(
            MutableCarrier(
                carrierId = newCarrierId(),
                coreRestMass = restMass,
                carrierType = CarrierType.STELLAR
            )
        )
    }

    fun addSpaceShip(
        coreRestMass: Double,
        fuelRestMass: Double,
        maxDeltaFuelRestMass: Double
    ) {
        carrierList.add(
            MutableCarrier(
                carrierId = newCarrierId(),
                coreRestMass = coreRestMass,
                fuelRestMass = fuelRestMass,
                maxDeltaFuelRestMass = maxDeltaFuelRestMass,
                carrierType = CarrierType.SPACESHIP
            )
        )
    }
}