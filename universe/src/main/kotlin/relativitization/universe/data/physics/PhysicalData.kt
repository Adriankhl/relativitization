package relativitization.universe.data.physics

import kotlinx.serialization.Serializable

@Serializable
data class PhysicalData(
    val double4D: Double4D = Double4D(-1.0, -1.0, -1.0, -1.0),
    val velocity: Velocity = Velocity(0.0, 0.0, 0.0),
    val restMass: Double = 0.0,
    val energy: Double = 0.0,
    val moveEnergyEfficiency: Double = 1.0,
    val moveMaxPower: Double = 0.0,
)

@Serializable
data class MutablePhysicalData(
    var double4D: MutableDouble4D = MutableDouble4D(-1.0, -1.0, -1.0, -1.0),
    var velocity: MutableVelocity = MutableVelocity(0.0, 0.0, 0.0),
    var restMass: Double = 0.0,
    var energy: Double = 0.0,
    var moveEnergyEfficiency: Double = 1.0,
    var moveMaxPower: Double = 0.0,
)