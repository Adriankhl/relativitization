package relativitization.universe.data.physics

import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
data class Int4D(val t: Int, val x: Int, val y: Int, val z: Int) {
    fun toInt3D() = Int3D(x, y, z)
    fun toDouble4D() = Double4D(t.toDouble(), x.toDouble(), y.toDouble(), z.toDouble())
}

@Serializable
data class MutableInt4D(var t: Int, var x: Int, var y: Int, var z: Int) {
    fun toMutableInt3D() = MutableInt3D(x, y, z)
    fun toDouble4D() = Double4D(t.toDouble(), x.toDouble(), y.toDouble(), z.toDouble())
}

@Serializable
data class Double4D(val t: Double, val x: Double, val y: Double, val z: Double) {
    fun toInt4D() = Int4D(t.toInt(), x.toInt(), y.toInt(), z.toInt())
}

@Serializable
data class MutableDouble4D(var t: Double, var x: Double, var y: Double, var z: Double) {
    fun toMutableInt4D() = MutableInt4D(t.toInt(), x.toInt(), y.toInt(), z.toInt())
}

@Serializable
data class Int3D(val x: Int, val y: Int, val z: Int) {
    fun isNearby(int3D: Int3D): Boolean {
        val xDistance = abs(x - int3D.x)
        val yDistance = abs(y - int3D.y)
        val zDistance = abs(z - int3D.z)
        return (xDistance <= 1) && (yDistance <= 1) && (zDistance <= 1)
    }
}

@Serializable
data class MutableInt3D(var x: Int, var y: Int, var z: Int)

@Serializable
data class Double3D(val x: Double, val y: Double, val z: Double)

@Serializable
data class MutableDouble3D(var x: Double, var y: Double, var z: Double)

@Serializable
data class Int2D(val x: Int, val y: Int)

@Serializable
data class MutableInt2D(var x: Int, var y: Int)

@Serializable
data class Double2D(val x: Double, val y: Double)

@Serializable
data class MutableDouble2D(var x: Double, var y: Double)