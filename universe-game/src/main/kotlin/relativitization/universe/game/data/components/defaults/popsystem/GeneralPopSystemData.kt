package relativitization.universe.game.data.components.defaults.popsystem

import kotlinx.serialization.Serializable

/**
 * Data for all pop in all carrier
 *
 * @property baseSalaryPerEmployee minimum salary per employee
 */
@Serializable
data class GeneralPopSystemData(
    val baseSalaryPerEmployee: Double = 1E-5,
)

@Serializable
data class MutableGeneralPopSystemData(
    var baseSalaryPerEmployee: Double = 1E-5,
)