package relativitization.universe.game.data.components.defaults.popsystem

import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable

/**
 * Data for all pop in all carrier
 *
 * @property baseSalaryPerEmployee minimum salary per employee
 */
@GenerateImmutable
data class MutableGeneralPopSystemData(
    var baseSalaryPerEmployee: Double = 1E-5,
)