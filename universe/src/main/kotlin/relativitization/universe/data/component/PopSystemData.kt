package relativitization.universe.data.component

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.component.popsystem.*
import relativitization.universe.maths.collection.ListFind
import kotlin.random.Random

@Serializable
@SerialName("PopSystemData")
data class PopSystemData(
    val carrierDataMap: Map<Int, CarrierData> = mapOf(),
    val combatData: CombatData = CombatData(),
) : PlayerDataComponent() {
    fun totalCoreRestMass(): Double {
        return carrierDataMap.values.sumOf { it.coreRestMass }
    }

    fun totalFuelRestMass(): Double {
        return carrierDataMap.values.sumOf { it.fuelRestMass }
    }

    fun totalMaxDeltaFuelRestMass(): Double {
        return carrierDataMap.values.sumOf { it.maxDeltaFuelRestMass }
    }
}

@Serializable
@SerialName("PopSystemData")
data class MutablePopSystemData(
    val carrierDataMap: MutableMap<Int, MutableCarrierData> = mutableMapOf(),
    var combatData: MutableCombatData = MutableCombatData(),
) : MutablePlayerDataComponent() {
    fun totalCoreRestMass(): Double {
        return carrierDataMap.values.sumOf { it.coreRestMass }
    }

    fun totalFuelRestMass(): Double {
        return carrierDataMap.values.sumOf { it.fuelRestMass }
    }

    fun totalMaxDeltaFuelRestMass(): Double {
        return carrierDataMap.values.sumOf { it.maxDeltaFuelRestMass }
    }

    /**
     * Find the smallest non-negative carrier id which is not in the carrier list
     */
    fun newCarrierId(): Int {
        return ListFind.minMissing(carrierDataMap.keys.toList(), 0)
    }

    fun addRandomStellarSystem() {
        val restMass = Random.nextDouble(1.0e30, 2.5e30)
        val newCarrier = MutableCarrierData(
            coreRestMass = restMass,
            carrierType = CarrierType.STELLAR
        )
        carrierDataMap[newCarrierId()] = newCarrier

    }

    fun addSpaceShip(
        coreRestMass: Double,
        fuelRestMass: Double,
        maxDeltaFuelRestMass: Double
    ) {
        val newCarrier = MutableCarrierData(
            coreRestMass = coreRestMass,
            fuelRestMass = fuelRestMass,
            maxDeltaFuelRestMass = maxDeltaFuelRestMass,
            carrierType = CarrierType.SPACESHIP
        )
        carrierDataMap[newCarrierId()] = newCarrier
    }
}