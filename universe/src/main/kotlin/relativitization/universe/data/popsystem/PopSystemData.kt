package relativitization.universe.data.popsystem

import kotlinx.serialization.Serializable
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

    fun totalMaxDeltaFuelRestMass(): Double{
        return carrierList.sumOf { it.maxDeltaFuelRestMass }
    }

    fun addRandomStellarSystem() {
        val restMass = Random.nextDouble(1.0e30, 2.5e30)
        carrierList.add(MutableCarrier(
            coreRestMass = restMass,
            carrierType = CarrierType.STELLAR
        ))
    }

    fun addSpaceShip(
        coreRestMass: Double,
        fuelRestMass: Double,
        maxDeltaFuelRestMass: Double
    ) {
        carrierList.add(
            MutableCarrier(
            coreRestMass = coreRestMass,
            fuelRestMass = fuelRestMass,
            maxDeltaFuelRestMass = maxDeltaFuelRestMass,
            carrierType = CarrierType.SPACESHIP
        ))
    }
}