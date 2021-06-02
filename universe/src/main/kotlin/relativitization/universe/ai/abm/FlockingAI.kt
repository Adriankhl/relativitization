package relativitization.universe.ai.abm

import org.apache.logging.log4j.LogManager
import relativitization.universe.ai.AI
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.Double3D
import relativitization.universe.data.physics.Velocity
import relativitization.universe.maths.physics.Intervals.distance

class FlockingAI : AI() {
    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        logger.debug("Computing with FlockingAI")

        val nearByRadius: Double = 5.0

        return listOf()
    }

    private fun cohesion(universeData3DAtPlayer: UniverseData3DAtPlayer, radius: Double): Velocity {
        val selfDouble4D = universeData3DAtPlayer.getCurrentPlayerData().playerInternalData.physicsData.double4D
        val nearByPlayerData: List<PlayerData> = universeData3DAtPlayer.playerDataMap.values.filter {
            val otherDouble4D = it.playerInternalData.physicsData.double4D
            distance(selfDouble4D, otherDouble4D) < radius
        }

        return if (nearByPlayerData.isEmpty()) {
            Velocity(0.0, 0.0, 0.0)
        } else {
            val avgX: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.physicsData.double4D.x
            } / nearByPlayerData.size.toDouble()

            val avgY: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.physicsData.double4D.y
            }/ nearByPlayerData.size.toDouble()

            val avgZ: Double = nearByPlayerData.fold(0.0) { acc, playerData ->
                acc + playerData.playerInternalData.physicsData.double4D.z
            }/ nearByPlayerData.size.toDouble()

            Velocity(avgX - selfDouble4D.x, avgY - selfDouble4D.y, avgZ - selfDouble4D.z).scaleVelocity(0.5)
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}