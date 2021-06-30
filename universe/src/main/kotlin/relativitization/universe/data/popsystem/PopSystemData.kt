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
}

@Serializable
data class MutablePopSystemicData(
    val carrierList: MutableList<MutableCarrier> = mutableListOf()
) {
    fun addRandomStellarSystem() {
        val restMass = Random.nextDouble(1.0e30, 2.5e30)
        carrierList.add(MutableCarrier(
            coreRestMass = restMass,
            carrierType = CarrierType.STELLAR
        ))
    }

    fun totalCoreRestMass(): Double {
        return carrierList.sumOf { it.coreRestMass }
    }

    fun totalFuelRestMass(): Double {
        return carrierList.sumOf { it.fuelRestMass }
    }
}