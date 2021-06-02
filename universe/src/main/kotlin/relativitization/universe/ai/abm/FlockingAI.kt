package relativitization.universe.ai.abm

import org.apache.logging.log4j.LogManager
import relativitization.universe.ai.AI
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.Double3D
import relativitization.universe.maths.physics.Intervals.distance

class FlockingAI : AI() {
    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        logger.debug("Computing with FlockingAI")

        val nearByRadius: Double = 5.0

        val desiredSeparation: Double = 1.0

        val cohesionDouble3D = cohesion(universeData3DAtPlayer, nearByRadius)

        val alignmentDouble3D = alignment(universeData3DAtPlayer, nearByRadius)

        val separationDouble3D = separation(universeData3DAtPlayer, desiredSeparation)

        return listOf()
    }

    private fun cohesion(universeData3DAtPlayer: UniverseData3DAtPlayer, radius: Double): Double3D {
        val selfDouble4D = universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.physicsData.double4D
        val nearByPlayerData: List<PlayerData> = universeData3DAtPlayer.playerDataMap.values.filter {
            val otherDouble4D = it.playerInternalData.physicsData.double4D
            distance(selfDouble4D, otherDouble4D) < radius && (it.id != universeData3DAtPlayer.getCurrentPlayerData().id)
        }

        return if (nearByPlayerData.isEmpty()) {
            Double3D(0.0, 0.0, 0.0)
        } else {
            val avgX: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.physicsData.double4D.x
            } / nearByPlayerData.size.toDouble()

            val avgY: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.physicsData.double4D.y
            } / nearByPlayerData.size.toDouble()

            val avgZ: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.physicsData.double4D.z
            } / nearByPlayerData.size.toDouble()

            Double3D(avgX - selfDouble4D.x, avgY - selfDouble4D.y, avgZ - selfDouble4D.z)
        }
    }

    fun alignment(universeData3DAtPlayer: UniverseData3DAtPlayer, radius: Double): Double3D {
        val selfDouble4D = universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.physicsData.double4D
        val nearByPlayerData: List<PlayerData> = universeData3DAtPlayer.playerDataMap.values.filter {
            val otherDouble4D = it.playerInternalData.physicsData.double4D
            distance(selfDouble4D, otherDouble4D) < radius && (it.id != universeData3DAtPlayer.getCurrentPlayerData().id)
        }


        return if (nearByPlayerData.isEmpty()) {
            Double3D(0.0, 0.0, 0.0)
        } else {
            val avgX: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.physicsData.velocity.vx
            } / nearByPlayerData.size.toDouble()

            val avgY: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.physicsData.velocity.vy
            } / nearByPlayerData.size.toDouble()

            val avgZ: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.physicsData.velocity.vz
            } / nearByPlayerData.size.toDouble()

            Double3D(avgX, avgY, avgZ)
        }
    }

    fun separation(universeData3DAtPlayer: UniverseData3DAtPlayer, desiredSeparation: Double): Double3D {
        val selfDouble4D = universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.physicsData.double4D
        val nearByPlayerData: List<PlayerData> = universeData3DAtPlayer.playerDataMap.values.filter {
            val otherDouble4D = it.playerInternalData.physicsData.double4D
            val distance = distance(selfDouble4D, otherDouble4D)
            (distance < desiredSeparation) && (distance > 0.0) && (it.id != universeData3DAtPlayer.getCurrentPlayerData().id)
        }

        return if (nearByPlayerData.isEmpty()) {
            Double3D(0.0, 0.0, 0.0)
        } else {
            val avgX: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                val otherDouble4D = playerData.playerInternalData.physicsData.double4D
                val distance = distance(selfDouble4D, otherDouble4D)
                val double3D = Double3D(
                    selfDouble4D.x - otherDouble4D.x,
                    selfDouble4D.y - otherDouble4D.y,
                    selfDouble4D.z - otherDouble4D.z
                )
                acc + double3D.normalize().x / distance
            } / nearByPlayerData.size.toDouble()

            val avgY: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                val otherDouble4D = playerData.playerInternalData.physicsData.double4D
                val distance = distance(selfDouble4D, otherDouble4D)
                val double3D = Double3D(
                    selfDouble4D.x - otherDouble4D.x,
                    selfDouble4D.y - otherDouble4D.y,
                    selfDouble4D.z - otherDouble4D.z
                )
                acc + double3D.normalize().y / distance
            } / nearByPlayerData.size.toDouble()

            val avgZ: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                val otherDouble4D = playerData.playerInternalData.physicsData.double4D
                val distance = distance(selfDouble4D, otherDouble4D)
                val double3D = Double3D(
                    selfDouble4D.x - otherDouble4D.x,
                    selfDouble4D.y - otherDouble4D.y,
                    selfDouble4D.z - otherDouble4D.z
                )
                acc + double3D.normalize().z / distance
            } / nearByPlayerData.size.toDouble()

            Double3D(avgX, avgY, avgZ)
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}