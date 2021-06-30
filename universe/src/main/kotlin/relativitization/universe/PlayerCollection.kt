package relativitization.universe

import kotlinx.coroutines.runBlocking
import relativitization.universe.data.*
import relativitization.universe.data.physics.*
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.maths.grid.Grids.create3DGrid
import relativitization.universe.maths.grid.Grids.double4DToGroupId
import relativitization.universe.maths.physics.Relativistic.deltaMassByPhotonRocket
import relativitization.universe.utils.RandomName.randomPlayerName
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.min

class PlayerCollection(
    private val xDim: Int,
    private val yDim: Int,
    private val zDim: Int,
    private val edgeLength: Double,
) {
    private val playerMap: MutableMap<Int, MutablePlayerData> = mutableMapOf()
    private val deadIdList: MutableList<Int> = mutableListOf()

    /**
     * has player or not
     */
    fun hasPlayer(id: Int): Boolean {
        if (isDead(id)) {
            logger.debug("hasPlayer: player $id is dead")
        }
        return playerMap.containsKey(id)
    }

    /**
     * Is dead player by id
     */
    fun isDead(id: Int): Boolean {
        return deadIdList.contains(id)
    }

    /**
     * Get dead id list
     */
    fun getDeadIdList(): List<Int> {
        return deadIdList
    }

    /**
     * Get player
     */
    fun getPlayer(id: Int): MutablePlayerData {
        return playerMap.getValue(id)
    }

    /**
     * Get player location
     */
    fun getPlayerInt4D(id: Int): Int4D {
        return copy(playerMap.getValue(id).int4D)
    }

    /**
     * Get all player id
     */
    fun getIdList(): List<Int> {
        return playerMap.keys.toList()
    }

    /**
     * Get all NONE type (nor ai or human) player
     */
    fun getNoneIdList(): List<Int> {
        return playerMap.filter { (_, player) -> player.playerType == PlayerType.NONE }.keys.toList()
    }

    /**
     * Get all human player id
     */
    fun getHumanIdList(): List<Int> {
        return playerMap.filter { (_, player) -> player.playerType == PlayerType.HUMAN }.keys.toList()
    }

    /**
     * Get all human or ai player id
     */
    fun getHumanOrAiIdList(): List<Int> {
        return playerMap.filter { (_, player) ->
            (player.playerType == PlayerType.HUMAN) || (player.playerType == PlayerType.AI)
        }.keys.toList()
    }

    /**
     * Generate 3D slice of universe
     */
    fun getPlayerId3D(): List<List<List<List<Int>>>> {
        val playerId3D: List<List<List<MutableList<Int>>>> = create3DGrid(xDim, yDim, zDim) {
                _, _, _ -> mutableListOf()
        }

        playerMap.forEach { (_, player) -> playerId3D[player.int4D.x] [player.int4D.y][player.int4D.z].add(player.id) }

        return playerId3D
    }

    /**
     * Turn data to immutable data, and return new universe slice
     */
    fun getUniverseSlice(universeData: UniverseData): List<List<List<List<PlayerData>>>> {
        val playerId3D: List<List<List<MutableList<PlayerData>>>> = create3DGrid(xDim, yDim, zDim) {
                _, _, _ -> mutableListOf()
        }

        playerMap.forEach { (_, player) ->
            playerId3D[player.int4D.x] [player.int4D.y][player.int4D.z].add(copy(player))

            // Also add afterimage
            player.int4DHistory.forEach { int4D ->
                val oldData: PlayerData = universeData.getPlayerDataAt(int4D, player.id)
                playerId3D[oldData.int4D.x] [oldData.int4D.y][oldData.int4D.z].add(oldData)
            }
        }

        return playerId3D
    }

    /**
     * Remove player by id from player3D and playerMap
     */
    private fun removePlayer(id: Int) {
        if(playerMap.containsKey(id)) {
            playerMap.remove(id)
        } else {
            logger.error("Cannot remove player, player $id does not exist")
        }
    }

    /**
     * Add player if he does not exist or if the data is newer
     */
    fun addPlayer(playerData: PlayerData) {
        if (hasPlayer(playerData.id)) {
            if (playerData.int4D.t > playerMap.getValue(playerData.id).int4D.t) {
                removePlayer(playerData.id)
                playerMap[playerData.id] = copy(playerData)
            } else {
                logger.debug("Not going to add player ${playerData.id}")
            }
        } else {
            playerMap[playerData.id] = copy(playerData)
        }
    }

    /**
     * Remove player and add id to deadIdList if player is dead
     */
    fun cleanDeadPlayer() {
        val dead: List<Int> = playerMap.values.filter { !it.playerInternalData.isAlive }.map { it.id }
        dead.map { removePlayer(it) }
        deadIdList.addAll(dead)
    }

    /**
     * Add new player from playerData and clear newPlayerList
     */
    fun addNewPlayerFromPlayerData(universeState: UniverseState) {
        playerMap.forEach { (_, playerData) ->
            playerData.newPlayerList.forEach { mutableNewPlayerInternalData ->

                val newPlayerInternalData: PlayerInternalData = copy(mutableNewPlayerInternalData)
                val id: Int = runBlocking {
                    universeState.getNewId()
                }
                val name = randomPlayerName(newPlayerInternalData)
                val newPlayerData: PlayerData = PlayerData(
                    id = id,
                    name = name,
                    playerType= PlayerType.AI,
                    int4D = copy(playerData.int4D),
                    double4D = copy(playerData.double4D),
                    groupId = playerData.groupId,
                    velocity = copy(playerData.velocity),
                    playerInternalData = newPlayerInternalData
                )
                addPlayer(newPlayerData)
            }
        }

        // Clean all newPlayerList
        playerMap.forEach { (_, playerData) -> playerData.newPlayerList.clear() }
    }

    /**
     * Does 4 things
     * 1. move player double4D position by his velocity, check the boundaries of the map
     * 2. move player int4D position by his double4D, add afterimage to int4DHistory if needed
     * 3. change player groupId by their double4D
     */
    fun movePlayer(universeState: UniverseState, universeSettings: UniverseSettings) {
        // New time
        val time: Int = universeState.getCurrentTime() + 1
        val timeDouble: Double = time.toDouble()

        // Move player double4D by velocity
        for ((_, playerData) in playerMap) {
            val velocity: MutableVelocity = playerData.velocity
            playerData.double4D.t = timeDouble
            playerData.double4D.x += velocity.vx
            playerData.double4D.y += velocity.vy
            playerData.double4D.z += velocity.vz

            // Check boundaries and ensure double 4D is within boundaries
            // If exceeds boundaries, also decreases the velocity component to zero
            if (playerData.double4D.x <= 0.0 ) {
                playerData.double4D.x = 0.000001
                velocityComponentToZero('x', playerData, universeSettings.speedOfLight)
            }

            if (playerData.double4D.x >= universeSettings.xDim.toDouble() ) {
                playerData.double4D.x = universeSettings.xDim.toDouble() - 0.000001
                velocityComponentToZero('x', playerData, universeSettings.speedOfLight)
            }

            if (playerData.double4D.y <= 0.0 ) {
                playerData.double4D.y = 0.000001
                velocityComponentToZero('y', playerData, universeSettings.speedOfLight)
            }

            if (playerData.double4D.y >= universeSettings.yDim.toDouble() ) {
                playerData.double4D.y = universeSettings.yDim.toDouble() - 0.000001
                velocityComponentToZero('y', playerData, universeSettings.speedOfLight)
            }

            if (playerData.double4D.z <= 0.0 ) {
                playerData.double4D.z = 0.000001
                velocityComponentToZero('z', playerData, universeSettings.speedOfLight)
            }

            if (playerData.double4D.z >= universeSettings.zDim.toDouble() ) {
                playerData.double4D.z = universeSettings.zDim.toDouble() - 0.000001
                velocityComponentToZero('z', playerData, universeSettings.speedOfLight)
            }
        }

        for ((_, playerData) in playerMap) {
            val double4D: MutableDouble4D = playerData.double4D
            val oldInt4D: Int4D = Int4D(playerData.int4D)
            val oldGroupId: Int = playerData.groupId

            // Move player int4D by double4D
            playerData.int4D.t = time
            playerData.int4D.x = double4D.x.toInt()
            playerData.int4D.y = double4D.y.toInt()
            playerData.int4D.z = double4D.z.toInt()

            // Change player group id
            playerData.groupId = double4DToGroupId(playerData.double4D, edgeLength)

            // Add old coordinate to int4DHistory if int4D position change or groupid change
            if ((oldInt4D.x != playerData.int4D.x) ||
                (oldInt4D.y != playerData.int4D.y) ||
                (oldInt4D.z != playerData.int4D.z) ||
                (oldGroupId != playerData.groupId)
            ) {
                playerData.int4DHistory.add(oldInt4D)
            }

            // Clean up unnecessary int4DHistory
            playerData.int4DHistory.removeAll { time - it.t > universeSettings.playerAfterImageDuration }
        }
    }

    /**
     * Change player velocity component to zero
     */
    private fun velocityComponentToZero(
        component: Char,
        playerData: MutablePlayerData,
        speedOfLight: Double,
    ) {
        val targetVelocity: Velocity = if (component == 'x') {
            playerData.velocity.toVelocity().copy(vx = 0.0)
        } else if (component == 'y') {
            playerData.velocity.toVelocity().copy(vy = 0.0)
        } else if (component == 'z') {
            playerData.velocity.toVelocity().copy(vz = 0.0)
        } else {
            logger.error("Component doesn't exist")
            playerData.velocity.toVelocity()
        }

        val deltaRestMass: Double = deltaMassByPhotonRocket(
            initialRestMass = playerData.playerInternalData.physicsData.totalRestMass(),
            initialVelocity = playerData.velocity.toVelocity(),
            targetVelocity = targetVelocity,
            speedOfLight = speedOfLight
        )

        val fuelRestMass: Double = playerData.playerInternalData.physicsData.fuelRestMass

        playerData.velocity = targetVelocity.toMutableVelocity()
        playerData.playerInternalData.physicsData.fuelRestMass -= min(fuelRestMass, deltaRestMass)
    }

    fun syncAllPlayerDataComponent() {
        playerMap.values.forEach { it.syncDataComponent() }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}
