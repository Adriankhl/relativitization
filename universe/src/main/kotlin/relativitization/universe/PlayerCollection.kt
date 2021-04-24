package relativitization.universe

import org.apache.logging.log4j.LogManager
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.maths.grid.Grids.create3DGrid

class PlayerCollection(val xDim: Int, val yDim: Int, val zDim: Int) {
    private val playerMap: MutableMap<Int, MutablePlayerData> = mutableMapOf()

    /**
     * Generate 3D slice of universe
     */
    fun getPlayer3D(): List<List<List<List<MutablePlayerData>>>> {
        val player3D: List<List<List<MutableList<MutablePlayerData>>>> = create3DGrid(xDim, yDim, zDim) {
                _, _, _ -> mutableListOf()
        }

        playerMap.forEach { _, player -> player3D[player.int4D.x] [player.int4D.y][player.int4D.z].add(player) }

        return player3D
    }

    /**
     * Remove player by id from player3D and playerMap
     */
    fun removePlayer(id: Int) {
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
        if (playerData.int4D.t > playerMap.getValue(playerData.id).int4D.t) {
            removePlayer(playerData.id)
            playerMap[playerData.id] = copy(playerData)
        } else {
            logger.debug("Not going to add player ${playerData.id}")
        }
    }


    companion object {
        private val logger = LogManager.getLogger()
    }
}