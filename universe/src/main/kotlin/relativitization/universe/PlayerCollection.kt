package relativitization.universe

import relativitization.universe.data.*
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.grid.Grids.create3DGrid
import relativitization.universe.maths.grid.Grids.double4DToGroupId
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.maths.physics.MutableDouble4D
import relativitization.universe.maths.physics.Velocity
import relativitization.universe.utils.RandomName.randomPlayerName
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.abs
import kotlin.math.floor

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
        return DataSerializer.copy(playerMap.getValue(id).int4D)
    }

    /**
     * Get all player id
     */
    fun getIdSet(): Set<Int> {
        return playerMap.keys
    }

    /**
     * Get all NONE type (nor AI or human) player
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
     * Get all human or AI player id
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
        val playerId3D: List<List<List<MutableList<Int>>>> =
            create3DGrid(xDim, yDim, zDim) { _, _, _ ->
                mutableListOf()
            }

        playerMap.forEach { (_, player) ->
            playerId3D[player.int4D.x][player.int4D.y][player.int4D.z].add(
                player.playerId
            )
        }

        return playerId3D
    }

    /**
     * Turn data to immutable data, and return new universe slice
     */
    fun getUniverseSlice(universeData: UniverseData): List<List<List<Map<Int, List<PlayerData>>>>> {
        val playerData3D: List<List<List<MutableMap<Int, MutableList<PlayerData>>>>> =
            create3DGrid(xDim, yDim, zDim) { _, _, _ ->
                mutableMapOf()
            }

        playerMap.forEach { (_, player) ->
            playerData3D[player.int4D.x][player.int4D.y][player.int4D.z].getOrPut(
                player.playerId
            ) { mutableListOf() }.add(DataSerializer.copy(player))

            // Also add afterimage
            player.int4DHistory.filter {
                player.int4D.t - it.t < universeData.universeSettings.playerAfterImageDuration
            }.forEach {
                val oldData: PlayerData = universeData.getPlayerDataAt(it, player.playerId)
                playerData3D[oldData.int4D.x][oldData.int4D.y][oldData.int4D.z].getOrPut(
                    player.playerId
                ) { mutableListOf() }.add(oldData)
            }
        }

        return playerData3D
    }

    /**
     * Remove player by id from player3D and playerMap
     */
    private fun removePlayer(id: Int) {
        if (playerMap.containsKey(id)) {
            playerMap.remove(id)
        } else {
            logger.error("Cannot remove player, player $id does not exist")
        }
    }

    /**
     * Add player if he does not exist or if the data is newer
     */
    fun addPlayer(playerData: PlayerData) {
        if (hasPlayer(playerData.playerId)) {
            if (playerData.int4D.t > playerMap.getValue(playerData.playerId).int4D.t) {
                removePlayer(playerData.playerId)
                playerMap[playerData.playerId] = DataSerializer.copy(playerData)
            } else {
                logger.debug("Not going to add player ${playerData.playerId}")
            }
        } else {
            playerMap[playerData.playerId] = DataSerializer.copy(playerData)
        }
    }

    /**
     * Remove player and add id to deadIdList if player is dead
     */
    fun cleanDeadPlayer() {
        val deadPlayerIdList: List<Int> = playerMap.values.filter {
            !it.playerInternalData.isAlive
        }.map { it.playerId }

        deadPlayerIdList.forEach { removePlayer(it) }
        deadIdList.addAll(deadPlayerIdList)
    }

    /**
     * Add new player from playerData and clear newPlayerList
     */
    fun addNewPlayerFromPlayerData(
        universeState: UniverseState,
    ) {
        // Sort to ensure the same outcome everytime
        val newPlayerList: List<PlayerData> = playerMap.keys.sorted().map { id ->
            val playerData: MutablePlayerData = playerMap.getValue(id)
            playerData.newPlayerList.map { mutableNewPlayerInternalData ->
                val newPlayerId: Int = universeState.getNewPlayerId()

                if (mutableNewPlayerInternalData.directLeaderId == playerData.playerId) {
                    // Add new player as direct subordinate if it is the direct leader
                    playerData.addDirectSubordinateId(newPlayerId)
                } else if (mutableNewPlayerInternalData.leaderIdList.contains(playerData.playerId)) {
                    // Add new player as subordinate if it is a leader
                    playerData.addSubordinateId(newPlayerId)
                }

                PlayerData(
                    playerId = newPlayerId,
                    name = randomPlayerName(mutableNewPlayerInternalData),
                    playerType = PlayerType.AI,
                    int4D = DataSerializer.copy(playerData.int4D),
                    double4D = DataSerializer.copy(playerData.double4D),
                    groupId = playerData.groupId,
                    velocity = DataSerializer.copy(playerData.velocity),
                    playerInternalData = DataSerializer.copy(mutableNewPlayerInternalData),
                )
            }
        }.flatten()

        newPlayerList.forEach {
            addPlayer(it)
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
            val originalVelocity: Velocity = playerData.velocity.toVelocity()
            playerData.double4D.t = timeDouble
            playerData.double4D.x += originalVelocity.vx
            playerData.double4D.y += originalVelocity.vy
            playerData.double4D.z += originalVelocity.vz

            // Check boundaries and ensure double 4D is within boundaries
            // If exceeds boundaries, change the velocity according to the boundary condition
            if (playerData.double4D.x <= 0.0) {
                playerData.double4D.x = 0.000001

                when (universeSettings.universeBoundary) {
                    UniverseBoundary.REFLECTIVE -> {
                        playerData.velocity.vx = abs(originalVelocity.vx)
                    }
                    UniverseBoundary.ABSORPTIVE -> {
                        playerData.velocity.vx = 0.0
                    }
                }
            }

            if (playerData.double4D.x >= universeSettings.xDim.toDouble()) {
                playerData.double4D.x = universeSettings.xDim.toDouble() - 0.000001

                when (universeSettings.universeBoundary) {
                    UniverseBoundary.REFLECTIVE -> {
                        playerData.velocity.vx = -abs(originalVelocity.vx)
                    }
                    UniverseBoundary.ABSORPTIVE -> {
                        playerData.velocity.vx = 0.0
                    }
                }
            }

            if (playerData.double4D.y <= 0.0) {
                playerData.double4D.y = 0.000001

                when (universeSettings.universeBoundary) {
                    UniverseBoundary.REFLECTIVE -> {
                        playerData.velocity.vy = abs(originalVelocity.vy)
                    }
                    UniverseBoundary.ABSORPTIVE -> {
                        playerData.velocity.vy = 0.0
                    }
                }
            }

            if (playerData.double4D.y >= universeSettings.yDim.toDouble()) {
                playerData.double4D.y = universeSettings.yDim.toDouble() - 0.000001

                when (universeSettings.universeBoundary) {
                    UniverseBoundary.REFLECTIVE -> {
                        playerData.velocity.vy = -abs(originalVelocity.vy)
                    }
                    UniverseBoundary.ABSORPTIVE -> {
                        playerData.velocity.vy = 0.0
                    }
                }
            }

            if (playerData.double4D.z <= 0.0) {
                playerData.double4D.z = 0.000001

                when (universeSettings.universeBoundary) {
                    UniverseBoundary.REFLECTIVE -> {
                        playerData.velocity.vz = abs(originalVelocity.vz)
                    }
                    UniverseBoundary.ABSORPTIVE -> {
                        playerData.velocity.vz = 0.0
                    }
                }
            }

            if (playerData.double4D.z >= universeSettings.zDim.toDouble()) {
                playerData.double4D.z = universeSettings.zDim.toDouble() - 0.000001

                when (universeSettings.universeBoundary) {
                    UniverseBoundary.REFLECTIVE -> {
                        playerData.velocity.vz = -abs(originalVelocity.vz)
                    }
                    UniverseBoundary.ABSORPTIVE -> {
                        playerData.velocity.vz = 0.0
                    }
                }
            }
        }

        for ((_, playerData) in playerMap) {
            val double4D: MutableDouble4D = playerData.double4D
            val oldInt4D = Int4D(playerData.int4D)
            val oldGroupId: Int = playerData.groupId

            // Move player int4D by double4D
            playerData.int4D.t = time
            playerData.int4D.x = floor(double4D.x).toInt()
            playerData.int4D.y = floor(double4D.y).toInt()
            playerData.int4D.z = floor(double4D.z).toInt()

            // Change player group id
            playerData.groupId = double4DToGroupId(playerData.double4D, edgeLength)

            // Add old coordinate to int4DHistory if int4D position change or groupId change
            if ((oldInt4D.x != playerData.int4D.x) ||
                (oldInt4D.y != playerData.int4D.y) ||
                (oldInt4D.z != playerData.int4D.z) ||
                (oldGroupId != playerData.groupId)
            ) {
                playerData.int4DHistory.add(oldInt4D)
            }

            // Clean up unnecessary int4DHistory
            playerData.int4DHistory.removeAll {
                time - it.t > universeSettings.playerHistoricalInt4DLength
            }
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}
