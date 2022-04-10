package relativitization.universe.maths.physics

import kotlinx.serialization.Serializable
import kotlin.math.*

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
    fun toInt4D() = Int4D(floor(t).toInt(), floor(x).toInt(), floor(y).toInt(), floor(z).toInt())
    fun toInt3D() = Int3D(floor(x).toInt(), floor(y).toInt(), floor(z).toInt())
    fun toDouble3D() = Double3D(x, y, z)

    fun atInt4D(int4D: Int4D): Boolean {
        return (floor(t).toInt() == int4D.t) && (floor(x).toInt() == int4D.x) && (floor(y).toInt() == int4D.y) &&
                (floor(z).toInt() == int4D.z)
    }
}

@Serializable
data class MutableDouble4D(var t: Double, var x: Double, var y: Double, var z: Double) {
    fun toMutableInt4D() = MutableInt4D(floor(t).toInt(), floor(x).toInt(), floor(y).toInt(), floor(z).toInt())
    fun toDouble3D() = Double3D(x, y, z)
    fun toMutableDouble3D() = MutableDouble3D(x, y, z)
    fun toInt3D() = Int3D(floor(x).toInt(), floor(y).toInt(), floor(z).toInt())
    fun toMutableInt3D() = MutableInt3D(floor(x).toInt(), floor(y).toInt(), floor(z).toInt())
    fun atInt4D(int4D: MutableInt4D): Boolean {
        return (floor(t).toInt() == int4D.t) && (floor(x).toInt() == int4D.x) && (floor(y).toInt() == int4D.y) &&
                (floor(z).toInt() == int4D.z)
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
     * @return an equivalent double sitting at the center of the grid
     */
    fun toDouble3DCenter(): Double3D {
        return Double3D(x.toDouble() + 0.5, y.toDouble() + 0.5, z.toDouble() + 0.5)
    }

    /**
     * Get list of int3D of a greater cube center at the current coordinate
     *
     * @param halfEdgeLength half of the length of the edge of the greater cube
     * @param minX minimum x coordinate
     * @param maxX maximum x coordinate
     * @param minY minimum y coordinate
     * @param maxY maximum y coordinate
     * @param minZ minimum z coordinate
     * @param maxZ maximum z coordinate
     */
    fun getInt3DCubeList(
        halfEdgeLength: Int,
        minX: Int,
        maxX: Int,
        minY: Int,
        maxY: Int,
        minZ: Int,
        maxZ: Int
    ): List<Int3D> {
        val realMinX: Int = max(x - halfEdgeLength, minX)
        val realMaxX: Int = min(x + halfEdgeLength, maxX)
        val realMinY: Int = max(y - halfEdgeLength, minY)
        val realMaxY: Int = min(y + halfEdgeLength, maxY)
        val realMinZ: Int = max(z - halfEdgeLength, minZ)
        val realMaxZ: Int = min(z + halfEdgeLength, maxZ)

        return (realMinX..realMaxX).map { x ->
            (realMinY..realMaxY).map { y ->
                (realMinZ..realMaxZ).map { z ->
                    Int3D(x, y, z)
                }
            }
        }.flatten().flatten()
    }

    /**
     * Get list of int3D at the surface of a greater cube center at the current coordinate
     *
     * @param halfEdgeLength half of the length of the edge of the greater cube
     * @param minX minimum x coordinate
     * @param maxX maximum x coordinate
     * @param minY minimum y coordinate
     * @param maxY maximum y coordinate
     * @param minZ minimum z coordinate
     * @param maxZ maximum z coordinate
     */
    fun getInt3DSurfaceList(
        halfEdgeLength: Int,
        minX: Int,
        maxX: Int,
        minY: Int,
        maxY: Int,
        minZ: Int,
        maxZ: Int
    ): List<Int3D> {
        val isLowerXSurfaceExist: Boolean = (x - halfEdgeLength) >= minX
        val isUpperXSurfaceExist: Boolean = (x + halfEdgeLength) <= maxX
        val isLowerYSurfaceExist: Boolean = (y - halfEdgeLength) >= minY
        val isUpperYSurfaceExist: Boolean = (y + halfEdgeLength) <= maxY
        val isLowerZSurfaceExist: Boolean = (z - halfEdgeLength) >= minZ
        val isUpperZSurfaceExist: Boolean = (z + halfEdgeLength) <= maxZ

        val realMinX: Int = max(x - halfEdgeLength, minX)
        val realMaxX: Int = min(x + halfEdgeLength, maxX)
        val realMinY: Int = max(y - halfEdgeLength, minY)
        val realMaxY: Int = min(y + halfEdgeLength, maxY)
        val realMinZ: Int = max(z - halfEdgeLength, minZ)
        val realMaxZ: Int = min(z + halfEdgeLength, maxZ)

        val lowerXSurface: List<Int3D> = if (isLowerXSurfaceExist) {
            (realMinY..realMaxY).map { yCor ->
                (realMinZ..realMaxZ).map { zCor ->
                    Int3D(realMinX, yCor, zCor)
                }
            }.flatten()
        } else {
            listOf()
        }

        val upperXSurface: List<Int3D> = if (isUpperXSurfaceExist) {
            (realMinY..realMaxY).map { yCor ->
                (realMinZ..realMaxZ).map { zCor ->
                    Int3D(realMaxX, yCor, zCor)
                }
            }.flatten()
        } else {
            listOf()
        }

        val lowerYSurface: List<Int3D> = if (isLowerYSurfaceExist && (halfEdgeLength > 0)) {
            // Avoid repeating, exclude the edge
            val minXNoRepeat: Int = if (isLowerXSurfaceExist) {
                realMinX + 1
            } else {
                realMinX
            }
            val maxXNoRepeat: Int = if (isUpperXSurfaceExist) {
                realMaxX - 1
            } else {
                realMaxX
            }
            (minXNoRepeat..maxXNoRepeat).map { xCor ->
                (realMinZ..realMaxZ).map { zCor ->
                    Int3D(xCor, realMinY, zCor)
                }
            }.flatten()
        } else {
            listOf()
        }

        val upperYSurface: List<Int3D> = if (isUpperYSurfaceExist && (halfEdgeLength > 0)) {
            // Avoid repeating, exclude the edge
            val minXNoRepeat: Int = if (isLowerXSurfaceExist) {
                realMinX + 1
            } else {
                realMinX
            }
            val maxXNoRepeat: Int = if (isUpperXSurfaceExist) {
                realMaxX - 1
            } else {
                realMaxX
            }
            (minXNoRepeat..maxXNoRepeat).map { xCor ->
                (realMinZ..realMaxZ).map { zCor ->
                    Int3D(xCor, realMaxY, zCor)
                }
            }.flatten()
        } else {
            listOf()
        }

        val lowerZSurface: List<Int3D> = if (isLowerZSurfaceExist && (halfEdgeLength > 0)) {
            // Avoid repeating, exclude the edge
            val minXNoRepeat: Int = if (isLowerXSurfaceExist) {
                realMinX + 1
            } else {
                realMinX
            }
            val maxXNoRepeat: Int = if (isUpperXSurfaceExist) {
                realMaxX - 1
            } else {
                realMaxX
            }
            val minYNoRepeat: Int = if (isLowerYSurfaceExist) {
                realMinY + 1
            } else {
                realMinY
            }
            val maxYNoRepeat: Int = if (isUpperYSurfaceExist) {
                realMaxY - 1
            } else {
                realMaxY
            }
            (minXNoRepeat..maxXNoRepeat).map { xCor ->
                (minYNoRepeat..maxYNoRepeat).map { yCor ->
                    Int3D(xCor, yCor, realMinZ)
                }
            }.flatten()
        } else {
            listOf()
        }

        val upperZSurface: List<Int3D> = if (isUpperZSurfaceExist && (halfEdgeLength > 0)) {
            // Avoid repeating, exclude the edge
            val minXNoRepeat: Int = if (isLowerXSurfaceExist) {
                realMinX + 1
            } else {
                realMinX
            }
            val maxXNoRepeat: Int = if (isUpperXSurfaceExist) {
                realMaxX - 1
            } else {
                realMaxX
            }
            val minYNoRepeat: Int = if (isLowerYSurfaceExist) {
                realMinY + 1
            } else {
                realMinY
            }
            val maxYNoRepeat: Int = if (isUpperYSurfaceExist) {
                realMaxY - 1
            } else {
                realMaxY
            }
            (minXNoRepeat..maxXNoRepeat).map { xCor ->
                (minYNoRepeat..maxYNoRepeat).map { yCor ->
                    Int3D(xCor, yCor, realMaxZ)
                }
            }.flatten()
        } else {
            listOf()
        }

        return lowerXSurface + upperXSurface + lowerYSurface + upperYSurface + lowerZSurface +
                upperZSurface
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
    fun toDouble3D() = Double3D(x, y, z)
    fun atInt3D(int3D: MutableInt3D): Boolean {
        return (floor(x).toInt() == int3D.x) && (floor(y).toInt() == int3D.y) && (floor(z).toInt() == int3D.z)
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