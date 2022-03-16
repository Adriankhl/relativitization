package relativitization.universe.data.components.defaults.popsystem

import kotlinx.serialization.Serializable

/**
 * Data for all pop in all carrier
 *
 * @property baseSalaryPerEmployee minimum salary per employee
 */
@Serializable
data class GeneralPopSystemData(
    val baseSalaryPerEmployee: Double = 0.01,
)

@Serializable
data class MutableGeneralPopSystemData(
    var baseSalaryPerEmployee: Double = 0.01,
)