package relativitization.universe

import org.apache.logging.log4j.LogManager
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.PlayerType
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.maths.grid.Grids.create3DGrid

class PlayerCollection(private val xDim: Int, private val yDim: Int, private val zDim: Int) {
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
    fun getAllId(): List<Int> {
        return playerMap.keys.toList()
    }

    /**
     * Get all human player id
     */
    fun getAllHumanId(): List<Int> {
        return playerMap.filter { (_, player) -> player.playerType == PlayerType.HUMAN }.keys.toList()
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


    companion object {
        private val logger = LogManager.getLogger()
    }
}