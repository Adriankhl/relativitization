package relativitization.universe.data.component

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.component.popsystem.Carrier
import relativitization.universe.data.component.popsystem.CarrierType
import relativitization.universe.data.component.popsystem.MutableCarrier
import relativitization.universe.maths.collection.ListFind
import kotlin.random.Random

@Serializable
@SerialName("PopSystemData")
data class PopSystemData(
    val carrierMap: Map<Int, Carrier> = mapOf()
) : PlayerDataComponent() {
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
@SerialName("PopSystemData")
data class MutablePopSystemData(
    val carrierMap: MutableMap<Int, MutableCarrier> = mutableMapOf()
) : MutablePlayerDataComponent() {
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