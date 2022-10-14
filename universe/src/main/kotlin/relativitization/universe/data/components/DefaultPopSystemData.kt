package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData
import relativitization.universe.data.components.defaults.popsystem.CarrierData
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.data.components.defaults.popsystem.GeneralPopSystemData
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierInternalData
import relativitization.universe.data.components.defaults.popsystem.MutableGeneralPopSystemData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.maths.collection.ListFind
import kotlin.random.Random

@Serializable
@SerialName("PopSystemData")
data class PopSystemData(
    val generalPopSystemData: GeneralPopSystemData = GeneralPopSystemData(),
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

    fun numCarrier() = carrierDataMap.values.size

    /**
     * Total adult population of all pop type
     */
    fun totalAdultPopulation(): Double {
        return carrierDataMap.values.sumOf {
            it.allPopData.totalAdultPopulation()
        }
    }

    /**
     * Total adult population of a specific pop type
     */
    fun totalAdultPopulation(popType: PopType): Double {
        return carrierDataMap.values.sumOf {
            it.allPopData.getCommonPopData(popType).adultPopulation
        }
    }

    /**
     * Compute total salary of all pop
     */
    fun totalSalary(): Double {
        return carrierDataMap.values.fold(0.0) { acc, carrierData ->
            acc + PopType.values().sumOf { popType ->
                val commonPopData = carrierData.allPopData.getCommonPopData(popType)
                commonPopData.salaryPerEmployee(generalPopSystemData) *
                        commonPopData.adultPopulation *
                        commonPopData.employmentRate
            }
        }
    }

    /**
     * Compute total salary of a specific pop type
     */
    fun totalSalary(popType: PopType): Double {
        return carrierDataMap.values.fold(0.0) { acc, carrierData ->
            val commonPopData = carrierData.allPopData.getCommonPopData(popType)
            val totalSalary: Double = commonPopData.salaryPerEmployee(generalPopSystemData) *
                    commonPopData.adultPopulation *
                    commonPopData.employmentRate

            acc + totalSalary
        }
    }

    /**
     * Compute average salary of all pop
     */
    fun averageSalary(): Double {
        val totalSalary: Double = totalSalary()

        val totalAdultPopulation: Double = totalAdultPopulation()

        return if (totalAdultPopulation > 0.0) {
            totalSalary / totalAdultPopulation
        } else {
            0.0
        }
    }

    /**
     * Compute average salary of a pop type
     */
    fun averageSalary(popType: PopType): Double {
        val totalSalary: Double = totalSalary(popType)

        val totalAdultPopulation: Double = totalAdultPopulation(popType)

        return if (totalAdultPopulation > 0.0) {
            totalSalary / totalAdultPopulation
        } else {
            0.0
        }
    }

    /**
     * Compute the total saving of population
     */
    fun totalSaving(): Double {
        return carrierDataMap.values.fold(0.0) { acc, carrierData ->
            acc + PopType.values().sumOf { popType ->
                carrierData.allPopData.getCommonPopData(popType).saving
            }
        }
    }

    /**
     * Compute the satisfaction times the population
     */
    fun totalSatisfaction(): Double {
        return carrierDataMap.values.fold(0.0) { acc, carrierData ->
            acc + PopType.values().sumOf { popType ->
                val commonPopData = carrierData.allPopData.getCommonPopData(popType)
                commonPopData.satisfaction * commonPopData.adultPopulation
            }
        }
    }
}

@Serializable
@SerialName("PopSystemData")
data class MutablePopSystemData(
    var generalPopSystemData: MutableGeneralPopSystemData = MutableGeneralPopSystemData(),
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

    /**
     * Find the smallest non-negative carrier id which is not in the carrier list
     */
    private fun newCarrierId(): Int {
        return ListFind.minMissing(carrierDataMap.keys.toList(), 0)
    }

    fun addRandomStellarSystem(random: Random) {
        val coreRestMass = random.nextDouble(1.0e30, 2.5e30)
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
     * Total adult population of all pop type
     */
    fun totalAdultPopulation(): Double {
        return carrierDataMap.values.sumOf {
            it.allPopData.totalAdultPopulation()
        }
    }

    /**
     * Total adult population of a specific pop type
     */
    fun totalAdultPopulation(popType: PopType): Double {
        return carrierDataMap.values.sumOf {
            it.allPopData.getCommonPopData(popType).adultPopulation
        }
    }

    /**
     * Compute total salary of all pop
     */
    fun totalSalary(): Double {
        return carrierDataMap.values.fold(0.0) { acc, carrierData ->
            acc + PopType.values().sumOf { popType ->
                val commonPopData = carrierData.allPopData.getCommonPopData(popType)
                commonPopData.salaryPerEmployee(generalPopSystemData) *
                        commonPopData.adultPopulation *
                        commonPopData.employmentRate
            }
        }
    }

    /**
     * Compute total salary of a specific pop type
     */
    fun totalSalary(popType: PopType): Double {
        return carrierDataMap.values.fold(0.0) { acc, CarrierData ->
            val commonPopData = CarrierData.allPopData.getCommonPopData(popType)
            val totalSalary: Double = commonPopData.salaryPerEmployee(generalPopSystemData) *
                    commonPopData.adultPopulation *
                    commonPopData.employmentRate

            acc + totalSalary
        }
    }

    /**
     * Compute average salary of all pop
     */
    fun averageSalary(): Double {
        val totalSalary: Double = totalSalary()

        val totalAdultPopulation: Double = totalAdultPopulation()

        return if (totalAdultPopulation > 0.0) {
            totalSalary / totalAdultPopulation
        } else {
            0.0
        }
    }

    /**
     * Compute average salary of a pop type
     */
    fun averageSalary(popType: PopType): Double {
        val totalSalary: Double = totalSalary(popType)

        val totalAdultPopulation: Double = totalAdultPopulation(popType)

        return if (totalAdultPopulation > 0.0) {
            totalSalary / totalAdultPopulation
        } else {
            0.0
        }
    }

    /**
     * Compute the total saving of population
     */
    fun totalSaving(): Double {
        return carrierDataMap.values.fold(0.0) { acc, carrierData ->
            acc + PopType.values().sumOf { popType ->
                carrierData.allPopData.getCommonPopData(popType).saving
            }
        }
    }

    /**
     * Compute the satisfaction times the population
     */
    fun totalSatisfaction(): Double {
        return carrierDataMap.values.fold(0.0) { acc, carrierData ->
            acc + PopType.values().sumOf { popType ->
                val commonPopData = carrierData.allPopData.getCommonPopData(popType)
                val totalSatisfaction: Double = commonPopData.satisfaction *
                        commonPopData.adultPopulation

                acc + totalSatisfaction
            }
        }
    }
}

fun PlayerInternalData.popSystemData(): PopSystemData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.popSystemData(): MutablePopSystemData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.popSystemData(newPopSystemData: MutablePopSystemData) =
    playerDataComponentMap.put(newPopSystemData)