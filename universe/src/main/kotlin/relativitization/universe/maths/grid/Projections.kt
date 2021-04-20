package relativitization.universe.maths.grid

import relativitization.universe.data.physics.Int2D

object Projections {

    /**
     *
     */
    fun coordinate2DFrom3D(
        x: Int,
        y: Int,
        z: Int,
        zDim: Int,
        gridWidth: Int,
        gridHeight: Int,
        xSpace: Int,
        ySpace: Int,
        xExtra: Int,
        yExtra: Int
    ): Int2D {

        val xSeparation = if (xSpace != ySpace) {
            (zDim - 1 + xExtra) * (xSpace + 1) * gridWidth
        } else {
            (zDim - 1 + xExtra) * 2 * (xSpace + 1) * gridWidth
        }

        val ySeparation = if (xSpace != ySpace) {
            (zDim - 1 + yExtra) * (ySpace + 1) * gridWidth
        } else {
            (zDim - 1 + xExtra) * 2 * (xSpace + 1) * gridWidth
        }
    }
}

