package relativitization.universe.data.components.defaults.physics

import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.sqrt

@Serializable
data class Int4D(val t: Int, val x: Int, val y: Int, val z: Int) {
    constructor(time: Int, int3D: Int3D) : this(time, int3D.x, int3D.y, int3D.z)
    constructor(mutableInt4D: MutableInt4D) : this(
        mutableInt4D.t,
        mutableInt4D.x,
        mutableInt4D.y,
        mutableInt4D.z
    )

    fun toMutableInt4D() = Int4D(t, x, y, z)
    fun toInt3D() = Int3D(x, y, z)
    fun toDouble4D() = Double4D(t.toDouble(), x.toDouble(), y.toDouble(), z.toDouble())
    fun toDouble4DCenter() =
        Double4D(t.toDouble(), x.toDouble() + 0.5, y.toDouble() + 0.5, z.toDouble() + 0.5)

    fun toDouble3D() = Double3D(x.toDouble(), y.toDouble(), z.toDouble())
}

@Serializable
data class MutableInt4D(var t: Int, var x: Int, var y: Int, var z: Int) {
    constructor(mutableInt4D: MutableInt4D) : this(
        mutableInt4D.t,
        mutableInt4D.x,
        mutableInt4D.y,
        mutableInt4D.z
    )

    fun toInt3D() = Int3D(x, y, z)
    fun toInt4D() = Int4D(t, x, y, z)
    fun toMutableInt3D() = MutableInt3D(x, y, z)
    fun toMutableDouble4D() =
        MutableDouble4D(t.toDouble(), x.toDouble(), y.toDouble(), z.toDouble())

    fun toMutableDouble4DCenter() =
        MutableDouble4D(t.toDouble(), x.toDouble() + 0.5, y.toDouble() + 0.5, z.toDouble() + 0.5)

    fun toDouble4D() = Double4D(t.toDouble(), x.toDouble(), y.toDouble(), z.toDouble())
    fun toDouble3D() = Double3D(x.toDouble(), y.toDouble(), z.toDouble())
}

@Serializable
data class Double4D(val t: Double, val x: Double, val y: Double, val z: Double) {
    fun toInt4D() = Int4D(t.toInt(), x.toInt(), y.toInt(), z.toInt())
    fun toInt3D() = Int3D(x.toInt(), y.toInt(), z.toInt())
    fun toDouble3D() = Double3D(x, y, z)

    fun atInt4D(int4D: Int4D): Boolean {
        return (t.toInt() == int4D.t) && (x.toInt() == int4D.x) && (y.toInt() == int4D.y) && (z.toInt() == int4D.z)
    }
}

@Serializable
data class MutableDouble4D(var t: Double, var x: Double, var y: Double, var z: Double) {
    fun toMutableInt4D() = MutableInt4D(t.toInt(), x.toInt(), y.toInt(), z.toInt())
    fun toDouble3D() = Double3D(x, y, z)
    fun toMutableDouble3D() = MutableDouble3D(x, y, z)
    fun toInt3D() = Int3D(x.toInt(), y.toInt(), z.toInt())
    fun toMutableInt3D() = MutableInt3D(x.toInt(), y.toInt(), z.toInt())
    fun atInt4D(int4D: MutableInt4D): Boolean {
        return (t.toInt() == int4D.t) && (x.toInt() == int4D.x) && (y.toInt() == int4D.y) && (z.toInt() == int4D.z)
    }
}

@Serializable
data class Int3D(val x: Int, val y: Int, val z: Int) {
    operator fun plus(double3D: Double3D): Double3D =
        Double3D(x + double3D.x, y + double3D.y, z + double3D.z)

    fun isNearby(int3D: Int3D): Boolean {
        val xDistance = abs(x - int3D.x)
        val yDistance = abs(y - int3D.y)
        val zDistance = abs(z - int3D.z)
        return (xDistance <= 1) && (yDistance <= 1) && (zDistance <= 1)
    }

    /**
     * @return a equivalent double sitting at the center of the grid
     */
    fun toDouble3DCenter(): Double3D {
        return Double3D(x.toDouble() + 0.5, y.toDouble() + 0.5, z.toDouble() + 0.5)
    }
}

@Serializable
data class MutableInt3D(var x: Int, var y: Int, var z: Int) {
    fun toInt3D(): Int3D = Int3D(x, y, z)
}

@Serializable
data class Double3D(val x: Double, val y: Double, val z: Double) {
    operator fun times(double: Double): Double3D = Double3D(x * double, y * double, z * double)

    operator fun plus(double3D: Double3D): Double3D =
        Double3D(x + double3D.x, y + double3D.y, z + double3D.z)

    operator fun minus(double3D: Double3D): Double3D =
        Double3D(x - double3D.x, y - double3D.y, z - double3D.z)

    fun squareMag(): Double = x * x + y * y + z * z

    fun normalize(): Double3D {
        val magnitude = sqrt(squareMag())
        return if (magnitude <= 0.0) {
            Double3D(0.0, 0.0, 0.0)
        } else {
            Double3D(x / magnitude, y / magnitude, z / magnitude)
        }
    }
}

@Serializable
data class MutableDouble3D(var x: Double, var y: Double, var z: Double) {
    fun atInt3D(int3D: MutableInt3D): Boolean {
        return (x.toInt() == int3D.x) && (y.toInt() == int3D.y) && (z.toInt() == int3D.z)
    }
}

@Serializable
data class Int2D(val x: Int, val y: Int)

@Serializable
data class MutableInt2D(var x: Int, var y: Int)

@Serializable
data class Double2D(val x: Double, val y: Double)

@Serializable
data class MutableDouble2D(var x: Double, var y: Double)