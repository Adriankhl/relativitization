package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.components.popsystem.*
import relativitization.universe.maths.collection.ListFind
import kotlin.random.Random

@Serializable
@SerialName("PopSystemData")
data class PopSystemData(
    val carrierDataMap: Map<Int, CarrierData> = mapOf(),
) : PlayerDataComponent() {
    fun totalCoreRestMass(): Double {
        val carrierCoreMass: Double =  carrierDataMap.values.sumOf { it.coreRestMass }

        val factoryFuelRestMass: Double = carrierDataMap.values.sumOf {
            it.allPopData.labourerPopData.resourceFactoryMap.values.sumOf {
                it.storedFuelRestMass
            }
        }

        return carrierCoreMass + factoryFuelRestMass
    }

    fun totalMaxDeltaFuelRestMass(): Double {
        return carrierDataMap.values.sumOf { it.maxMovementDeltaFuelRestMass }
    }

    fun numCarrier() = carrierDataMap.values.size
}

@Serializable @SerialName("PopSystemData")
data class MutablePopSystemData(
    val carrierDataMap: MutableMap<Int, MutableCarrierData> = mutableMapOf(),
) : MutablePlayerDataComponent() {
    fun totalCoreRestMass(): Double {
        val carrierCoreMass: Double =  carrierDataMap.values.sumOf { it.coreRestMass }

        val factoryFuelRestMass: Double = carrierDataMap.values.sumOf {
            it.allPopData.labourerPopData.resourceFactoryMap.values.sumOf {
                it.storedFuelRestMass
            }
        }

        return carrierCoreMass + factoryFuelRestMass
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
        val newCarrier = MutableCarrierData(
            coreRestMass = restMass,
            carrierType = CarrierType.STELLAR
        )
        carrierDataMap[newCarrierId()] = newCarrier
    }

    fun addStellarSystem(
        coreRestMass: Double
    ) {
        val newCarrier = MutableCarrierData(
            coreRestMass = coreRestMass,
            carrierType = CarrierType.STELLAR
        )
        carrierDataMap[newCarrierId()] = newCarrier

    }

    fun addSpaceShip(
        coreRestMass: Double,
        maxDeltaFuelRestMass: Double
    ) {
        val newCarrier = MutableCarrierData(
            coreRestMass = coreRestMass,
            carrierType = CarrierType.SPACESHIP,
            maxMovementDeltaFuelRestMass = maxDeltaFuelRestMass
        )
        carrierDataMap[newCarrierId()] = newCarrier
    }

    fun numCarrier(): Int = carrierDataMap.values.size
}