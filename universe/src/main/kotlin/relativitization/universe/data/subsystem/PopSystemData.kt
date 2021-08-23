package relativitization.universe.data.subsystem

import kotlinx.serialization.Serializable
import relativitization.universe.data.subsystem.popsystem.Carrier
import relativitization.universe.data.subsystem.popsystem.CarrierType
import relativitization.universe.data.subsystem.popsystem.MutableCarrier
import relativitization.universe.maths.collection.ListFind
import kotlin.random.Random

@Serializable
data class PopSystemicData(
    val carrierMap: Map<Int, Carrier> = mapOf()
) : PlayerSubsystemData {
    fun totalCoreRestMass(): Double {
        return carrierMap.values.sumOf { it.coreRestMass }
    }

    fun totalFuelRestMass(): Double {
        return carrierMap.values.sumOf { it.fuelRestMass }
    }

    fun totalMaxDeltaFuelRestMass(): Double {
        return carrierMap.values.sumOf { it.maxDeltaFuelRestMass }
    }
}

@Serializable
data class MutablePopSystemicData(
    val carrierMap: MutableMap<Int, MutableCarrier> = mutableMapOf()
) : MutablePlayerSubsystemData {
    fun totalCoreRestMass(): Double {
        return carrierMap.values.sumOf { it.coreRestMass }
    }

    fun totalFuelRestMass(): Double {
        return carrierMap.values.sumOf { it.fuelRestMass }
    }

    fun totalMaxDeltaFuelRestMass(): Double {
        return carrierMap.values.sumOf { it.maxDeltaFuelRestMass }
    }

    /**
     * Find the smallest non-negative carrier id which is not in the carrier list
     */
    fun newCarrierId(): Int {
        return ListFind.minMissing(carrierMap.keys.toList(), 0)
    }

    fun addRandomStellarSystem() {
        val restMass = Random.nextDouble(1.0e30, 2.5e30)
        val newCarrier = MutableCarrier(
            coreRestMass = restMass,
            carrierType = CarrierType.STELLAR
        )
        carrierMap[newCarrierId()] = newCarrier

    }

    fun addSpaceShip(
        coreRestMass: Double,
        fuelRestMass: Double,
        maxDeltaFuelRestMass: Double
    ) {
        val newCarrier = MutableCarrier(
            coreRestMass = coreRestMass,
            fuelRestMass = fuelRestMass,
            maxDeltaFuelRestMass = maxDeltaFuelRestMass,
            carrierType = CarrierType.SPACESHIP
        )
        carrierMap[newCarrierId()] = newCarrier
    }
}