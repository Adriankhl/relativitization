package relativitization.universe.ai.abm

import relativitization.universe.ai.AI
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.ChangeVelocityCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.Double3D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Intervals.distance
import relativitization.universe.utils.RelativitizationLogManager

class FlockingAI : AI() {
    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        logger.debug("Computing with FlockingAI")

        val nearByRadius = 2.0
        val desiredSeparation = 0.5
        val velocityMag = 0.5
        val ratio = 0.8

        val cohesionDouble3D = cohesion(universeData3DAtPlayer, nearByRadius)

        val alignmentDouble3D = alignment(universeData3DAtPlayer, nearByRadius)

        val separationDouble3D = separation(universeData3DAtPlayer, desiredSeparation)

        val avoidBoundaryDouble3D = avoidBoundary(universeData3DAtPlayer)

        val weightedDouble3D = cohesionDouble3D * 1.0 + alignmentDouble3D * 1.0 + separationDouble3D * 2.0 + avoidBoundaryDouble3D * 10.0

        val originalVelocity = universeData3DAtPlayer.getCurrentPlayerData().velocity

        // Constant velocity 0.5
        val targetVelocity: Velocity = Velocity(
            weightedDouble3D.x,
            weightedDouble3D.y,
            weightedDouble3D.z
        ).scaleVelocity(velocityMag)

        val weightedVelocity = originalVelocity * ratio + targetVelocity * ratio

        val changeVelocityCommand = ChangeVelocityCommand(
            targetVelocity = weightedVelocity,
            fromId = universeData3DAtPlayer.id,
            fromInt4D = universeData3DAtPlayer.getCurrentPlayerData().int4D,
            toId = universeData3DAtPlayer.id,
        )

        return listOf(changeVelocityCommand)
    }

    private fun cohesion(universeData3DAtPlayer: UniverseData3DAtPlayer, radius: Double): Double3D {
        val selfDouble4D = universeData3DAtPlayer.getCurrentPlayerData().double4D
        val nearByPlayerData: List<PlayerData> = universeData3DAtPlayer.playerDataMap.values.filter {
            val otherDouble4D = it.double4D
            distance(selfDouble4D, otherDouble4D) < radius && (it.id != universeData3DAtPlayer.getCurrentPlayerData().id)
        }

        return if (nearByPlayerData.isEmpty()) {
            Double3D(0.0, 0.0, 0.0)
        } else {
            val avgX: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.double4D.x
            } / nearByPlayerData.size.toDouble()

            val avgY: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.double4D.y
            } / nearByPlayerData.size.toDouble()

            val avgZ: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.double4D.z
            } / nearByPlayerData.size.toDouble()

            Double3D(avgX - selfDouble4D.x, avgY - selfDouble4D.y, avgZ - selfDouble4D.z)
        }
    }

    private fun alignment(universeData3DAtPlayer: UniverseData3DAtPlayer, radius: Double): Double3D {
        val selfDouble4D = universeData3DAtPlayer.getCurrentPlayerData().double4D
        val nearByPlayerData: List<PlayerData> = universeData3DAtPlayer.playerDataMap.values.filter {
            val otherDouble4D = it.double4D
            distance(selfDouble4D, otherDouble4D) < radius && (it.id != universeData3DAtPlayer.getCurrentPlayerData().id)
        }


        return if (nearByPlayerData.isEmpty()) {
            Double3D(0.0, 0.0, 0.0)
        } else {
            val avgX: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.velocity.vx
            } / nearByPlayerData.size.toDouble()

            val avgY: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.velocity.vy
            } / nearByPlayerData.size.toDouble()

            val avgZ: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.velocity.vz
            } / nearByPlayerData.size.toDouble()

            Double3D(avgX, avgY, avgZ)
        }
    }

    private fun separation(universeData3DAtPlayer: UniverseData3DAtPlayer, desiredSeparation: Double): Double3D {
        val selfDouble4D = universeData3DAtPlayer.getCurrentPlayerData().double4D
        val nearByPlayerData: List<PlayerData> = universeData3DAtPlayer.playerDataMap.values.filter {
            val otherDouble4D = it.double4D
            val distance = distance(selfDouble4D, otherDouble4D)
            (distance < desiredSeparation) && (distance > 0.0) && (it.id != universeData3DAtPlayer.getCurrentPlayerData().id)
        }

        return if (nearByPlayerData.isEmpty()) {
            Double3D(0.0, 0.0, 0.0)
        } else {
            val avgX: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                val otherDouble4D = playerData.double4D
                val distance = distance(selfDouble4D, otherDouble4D)
                val double3D = Double3D(
                    selfDouble4D.x - otherDouble4D.x,
                    selfDouble4D.y - otherDouble4D.y,
                    selfDouble4D.z - otherDouble4D.z
                )

                if (distance > 0.0) {
                    acc + double3D.normalize().x / distance
                } else {
                    acc + 100.0
                }
            } / nearByPlayerData.size.toDouble()

            val avgY: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                val otherDouble4D = playerData.double4D
                val distance = distance(selfDouble4D, otherDouble4D)
                val double3D = Double3D(
                    selfDouble4D.x - otherDouble4D.x,
                    selfDouble4D.y - otherDouble4D.y,
                    selfDouble4D.z - otherDouble4D.z
                )

                if (distance > 0.0) {
                    acc + double3D.normalize().y / distance
                } else {
                    acc + 100.0
                }
            } / nearByPlayerData.size.toDouble()

            val avgZ: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                val otherDouble4D = playerData.double4D
                val distance = distance(selfDouble4D, otherDouble4D)
                val double3D = Double3D(
                    selfDouble4D.x - otherDouble4D.x,
                    selfDouble4D.y - otherDouble4D.y,
                    selfDouble4D.z - otherDouble4D.z
                )

                if (distance > 0.0) {
                    acc + double3D.normalize().z / distance
                } else {
                    acc + 100.0
                }
            } / nearByPlayerData.size.toDouble()

            Double3D(avgX, avgY, avgZ)
        }
    }

    private fun avoidBoundary(universeData3DAtPlayer: UniverseData3DAtPlayer): Double3D {
        val selfDouble4D = universeData3DAtPlayer.getCurrentPlayerData().double4D

        val xComp = when {
            selfDouble4D.x < 0.1 -> {
                1.0
            }
            universeData3DAtPlayer.universeSettings.xDim.toDouble() - selfDouble4D.x < 0.1 -> {
                -1.0
            }
            else -> {
                0.0
            }
        }

        val yComp = when {
            selfDouble4D.y < 0.1 -> {
                1.0
            }
            universeData3DAtPlayer.universeSettings.yDim.toDouble() - selfDouble4D.y < 0.1 -> {
                -1.0
            }
            else -> {
                0.0
            }
        }


        val zComp = when {
            selfDouble4D.z < 0.1 -> {
                1.0
            }
            universeData3DAtPlayer.universeSettings.zDim.toDouble() - selfDouble4D.z < 0.1 -> {
                -1.0
            }
            else -> {
                0.0
            }
        }

        return Double3D(xComp, yComp, zComp)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}