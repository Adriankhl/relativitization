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
    fun toMutableInt4D() =
        MutableInt4D(floor(t).toInt(), floor(x).toInt(), floor(y).toInt(), floor(z).toInt())

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
     * @param halfEdgeLength half of the edge length of the greater cube + 0.5
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
        return if (halfEdgeLength >= 1) {
            val maxDistanceFromCenter: Int = halfEdgeLength - 1
            val realMinX: Int = max(x - maxDistanceFromCenter, minX)
            val realMaxX: Int = min(x + maxDistanceFromCenter, maxX)
            val realMinY: Int = max(y - maxDistanceFromCenter, minY)
            val realMaxY: Int = min(y + maxDistanceFromCenter, maxY)
            val realMinZ: Int = max(z - maxDistanceFromCenter, minZ)
            val realMaxZ: Int = min(z + maxDistanceFromCenter, maxZ)

            (realMinX..realMaxX).map { x ->
                (realMinY..realMaxY).map { y ->
                    (realMinZ..realMaxZ).map { z ->
                        Int3D(x, y, z)
                    }
                }
            }.flatten().flatten()
        } else {
            listOf()
        }
    }

    /**
     * Get list of int3D at the surface of a greater cube center at the current coordinate
     *
     * @param halfEdgeLength half of the edge length of the greater cube + 0.5
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
        return if (halfEdgeLength >= 1) {
            val maxDistanceFromCenter: Int = halfEdgeLength - 1

            val isLowerXSurfaceExist: Boolean = (x - maxDistanceFromCenter) >= minX
            val isUpperXSurfaceExist: Boolean = (x + maxDistanceFromCenter) <= maxX
            val isLowerYSurfaceExist: Boolean = (y - maxDistanceFromCenter) >= minY
            val isUpperYSurfaceExist: Boolean = (y + maxDistanceFromCenter) <= maxY
            val isLowerZSurfaceExist: Boolean = (z - maxDistanceFromCenter) >= minZ
            val isUpperZSurfaceExist: Boolean = (z + maxDistanceFromCenter) <= maxZ

            val cubeMinX: Int = max(x - maxDistanceFromCenter, minX)
            val cubeMaxX: Int = min(x + maxDistanceFromCenter, maxX)
            val cubeMinY: Int = max(y - maxDistanceFromCenter, minY)
            val cubeMaxY: Int = min(y + maxDistanceFromCenter, maxY)
            val cubeMinZ: Int = max(z - maxDistanceFromCenter, minZ)
            val cubeMaxZ: Int = min(z + maxDistanceFromCenter, maxZ)

            val lowerXSurface: List<Int3D> = if (isLowerXSurfaceExist) {
                (cubeMinY..cubeMaxY).map { yCor ->
                    (cubeMinZ..cubeMaxZ).map { zCor ->
                        Int3D(cubeMinX, yCor, zCor)
                    }
                }.flatten()
            } else {
                listOf()
            }

            val upperXSurface: List<Int3D> = if (isUpperXSurfaceExist) {
                (cubeMinY..cubeMaxY).map { yCor ->
                    (cubeMinZ..cubeMaxZ).map { zCor ->
                        Int3D(cubeMaxX, yCor, zCor)
                    }
                }.flatten()
            } else {
                listOf()
            }

            val lowerYSurface: List<Int3D> = if (isLowerYSurfaceExist) {
                // Avoid repeating, exclude the edge
                val minXNoRepeat: Int = if (isLowerXSurfaceExist) {
                    cubeMinX + 1
                } else {
                    cubeMinX
                }
                val maxXNoRepeat: Int = if (isUpperXSurfaceExist) {
                    cubeMaxX - 1
                } else {
                    cubeMaxX
                }
                (minXNoRepeat..maxXNoRepeat).map { xCor ->
                    (cubeMinZ..cubeMaxZ).map { zCor ->
                        Int3D(xCor, cubeMinY, zCor)
                    }
                }.flatten()
            } else {
                listOf()
            }

            val upperYSurface: List<Int3D> = if (isUpperYSurfaceExist) {
                // Avoid repeating, exclude the edge
                val minXNoRepeat: Int = if (isLowerXSurfaceExist) {
                    cubeMinX + 1
                } else {
                    cubeMinX
                }
                val maxXNoRepeat: Int = if (isUpperXSurfaceExist) {
                    cubeMaxX - 1
                } else {
                    cubeMaxX
                }
                (minXNoRepeat..maxXNoRepeat).map { xCor ->
                    (cubeMinZ..cubeMaxZ).map { zCor ->
                        Int3D(xCor, cubeMaxY, zCor)
                    }
                }.flatten()
            } else {
                listOf()
            }

            val lowerZSurface: List<Int3D> = if (isLowerZSurfaceExist) {
                // Avoid repeating, exclude the edge
                val minXNoRepeat: Int = if (isLowerXSurfaceExist) {
                    cubeMinX + 1
                } else {
                    cubeMinX
                }
                val maxXNoRepeat: Int = if (isUpperXSurfaceExist) {
                    cubeMaxX - 1
                } else {
                    cubeMaxX
                }
                val minYNoRepeat: Int = if (isLowerYSurfaceExist) {
                    cubeMinY + 1
                } else {
                    cubeMinY
                }
                val maxYNoRepeat: Int = if (isUpperYSurfaceExist) {
                    cubeMaxY - 1
                } else {
                    cubeMaxY
                }
                (minXNoRepeat..maxXNoRepeat).map { xCor ->
                    (minYNoRepeat..maxYNoRepeat).map { yCor ->
                        Int3D(xCor, yCor, cubeMinZ)
                    }
                }.flatten()
            } else {
                listOf()
            }

            val upperZSurface: List<Int3D> = if (isUpperZSurfaceExist) {
                // Avoid repeating, exclude the edge
                val minXNoRepeat: Int = if (isLowerXSurfaceExist) {
                    cubeMinX + 1
                } else {
                    cubeMinX
                }
                val maxXNoRepeat: Int = if (isUpperXSurfaceExist) {
                    cubeMaxX - 1
                } else {
                    cubeMaxX
                }
                val minYNoRepeat: Int = if (isLowerYSurfaceExist) {
                    cubeMinY + 1
                } else {
                    cubeMinY
                }
                val maxYNoRepeat: Int = if (isUpperYSurfaceExist) {
                    cubeMaxY - 1
                } else {
                    cubeMaxY
                }
                (minXNoRepeat..maxXNoRepeat).map { xCor ->
                    (minYNoRepeat..maxYNoRepeat).map { yCor ->
                        Int3D(xCor, yCor, cubeMaxZ)
                    }
                }.flatten()
            } else {
                listOf()
            }

            lowerXSurface + upperXSurface + lowerYSurface + upperYSurface + lowerZSurface +
                    upperZSurface
        } else {
            listOf()
        }
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