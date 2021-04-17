package relativitization.universe.data.physics

import kotlinx.serialization.Serializable

@Serializable
data class Int4D(var t: Int, var x: Int, var y: Int, var z: Int) {
    fun toInt3D() = Int3D(x, y, z)
    fun toDouble4D() = Double4D(t.toDouble(), x.toDouble(), y.toDouble(), z.toDouble())
}

@Serializable
data class Double4D(var t: Double, var x: Double, var y: Double, var z: Double) {
    fun toInt4D() = Int4D(t.toInt(), x.toInt(), y.toInt(), z.toInt())
}

@Serializable
data class Int3D(var x: Int, var y: Int, var z: Int)

@Serializable
data class Double3D(var x: Double, var y: Double, var z: Double)

@Serializable
data class Int2D(var x: Int, var y: Int)

@Serializable
data class Double2D(var x: Double, var y: Double)