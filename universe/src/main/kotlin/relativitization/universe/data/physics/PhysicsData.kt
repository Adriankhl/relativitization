package relativitization.universe.data.physics

import kotlinx.serialization.Serializable

@Serializable
data class PhysicsData(
    val restMass: Double = 1.0,
    val energy: Double = 1.0,
    val moveEnergyEfficiency: Double = 1.0,
    val moveMaxPower: Double = 0.0,
)

@Serializable
data class MutablePhysicsData(
    var restMass: Double = 1.0,
    var energy: Double = 1.0,
    var moveEnergyEfficiency: Double = 1.0,
    var moveMaxPower: Double = 0.0,
)