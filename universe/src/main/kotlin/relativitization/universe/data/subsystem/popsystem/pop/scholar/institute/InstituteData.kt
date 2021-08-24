package relativitization.universe.data.subsystem.popsystem.pop.scholar.institute

import kotlinx.serialization.Serializable

@Serializable
data class InstituteData(
    val xCor: Double = 0.0,
    val yCor: Double = 0.0,
    val maxNumEmployee: Double = 0.0,
    val lastNumEmployee: Double = 0.0,
    val size: Double = 0.0,
    val reputation: Double = 0.0,
)