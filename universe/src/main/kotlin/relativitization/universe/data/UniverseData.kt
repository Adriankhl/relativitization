package relativitization.universe.data

import com.github.javafaker.Bool
import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.*
import relativitization.universe.maths.grid.Grids.create3DGrid
import relativitization.universe.maths.physics.Intervals.intDelay

@Serializable
data class UniverseData(
    val universeData4D: UniverseData4D,
    val universeSettings: UniverseSettings,
    val universeState: UniverseState,
    val commandMap: MutableMap<Int, List<Command>>
) {
    /**
     * Check whether the universeData4D has the correct dimension specified in the setting
     */
    fun isDimensionValid(): Boolean {
        val tCheckList = universeData4D.getTSizeList().map { it == universeSettings.tDim }
        val xCheckList = universeData4D.getXSizeList().map { it == universeSettings.xDim }
        val yCheckList = universeData4D.getYSizeList().map { it == universeSettings.yDim }
        val zCheckList = universeData4D.getZSizeList().map { it == universeSettings.zDim }

        return !(tCheckList + xCheckList + yCheckList + zCheckList).contains(false)
    }

    /**
     * Check whether the int4D coordinate is within the range of our setting of the stored data
     */
    fun isValidCoordinate(int4D: Int4D): Boolean {
        val tLower: Boolean = int4D.t >= universeState.getCurrentTime() - universeSettings.tDim + 1
        val tUpper: Boolean = int4D.t <= universeState.getCurrentTime()
        val xLower: Boolean = int4D.x >= 0
        val xUpper: Boolean = int4D.x < universeSettings.xDim
        val yLower: Boolean = int4D.y >= 0
        val yUpper: Boolean = int4D.y < universeSettings.yDim
        val zLower: Boolean = int4D.z >= 0
        val zUpper: Boolean = int4D.z < universeSettings.zDim

        return tLower && tUpper && xLower && xUpper && yLower && yUpper && zLower && zUpper
    }

    fun getPlayerDataListAt(int4D: Int4D): List<PlayerData> {
        val currentTime = universeState.getCurrentTime()
        return universeData4D.getPlayerDataList(currentTime - int4D.t, int4D.x, int4D.y, int4D.z)
    }

    fun toUniverseData3DAtGrid(center: Int4D) {
        val playerId3D: List<List<List<MutableList<Int>>>> =
            create3DGrid<MutableList<Int>>(
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { x, y, z -> mutableListOf() }

        val playerDataMap: MutableMap<Int, PlayerData> = mutableMapOf()

        for (i in 0 until universeSettings.xDim) {
            for (j in 0 until universeSettings.yDim) {
                for (k in 0 until universeSettings.zDim) {
                    val delay = intDelay(center.toInt3D(), Int3D(i, j, k), universeSettings.speedOfLight)
                    val coordinate = Int4D(center.t - delay, i, j, k)
                    val playerDataList = getPlayerDataListAt(coordinate)

                    // Check repeated playerData due to movement and time delay
                    for (playerData in playerDataList) {
                        val id = playerData.id
                        if (playerDataMap.containsKey(playerData.id)) {
                            // Two duplicate possibility: different spacetime -> take the latest data
                            // same spacetime different location due to movement -> take the latest data:w
                            if (playerData.int4D.t > playerDataMap.getValue(id).int4D.t) {
                                val removePlayerData = playerDataMap.getValue(id)
                                val remove1 = removePlayerData.int4D
                                val remove2 = removePlayerData.oldInt4D
                                playerId3D[remove1.x][remove1.y][remove1.z].remove(removePlayerData.id)
                                playerId3D[remove2.x][remove2.y][remove2.z].remove(removePlayerData.id)
                                playerId3D[i][j][k].add(id)
                                playerDataMap[id] = playerData
                            } else if (playerData.int4D == playerDataMap.getValue(id).int4D &&
                                playerData.int4D == coordinate
                            ) {
                                val removePlayerData = playerDataMap.getValue(id)
                                val remove2 = removePlayerData.oldInt4D
                                playerId3D[remove2.x][remove2.y][remove2.z].remove(removePlayerData.id)
                                playerId3D[i][j][k].add(id)
                                playerDataMap[id] = playerData
                            }
                        } else {
                            playerId3D[i][j][k].add(id)
                            playerDataMap[id] = playerData
                        }
                    }
                }
            }
        }

        // player data at center grid without delay
        val zeroDelayPlayerDataMap = getPlayerDataSetAt(center).map { it.id to it }.toMap()
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}

/**
 * 4D Grid of the PlayerData
 */
@Serializable
data class UniverseData4D(
    private val playerData4D: MutableList<List<List<List<List<PlayerData>>>>>,
) {
    /**
     * Get player data from the 4D List
     * i, j, k, l doesn't necessarily the actual t, x, y, z
     */
    fun getPlayerDataList(i: Int, j: Int, k: Int, l: Int): List<PlayerData> {
        return playerData4D[i][j][k][l]
    }

    /**
     * Get the t dimension of playerData4D as list
     */
    fun getTSizeList(): List<Int> = listOf(playerData4D.size)

    /**
     * Get the x dimension of playerData4D as list
     */
    fun getXSizeList(): List<Int> = playerData4D.map { it.size }

    /**
     * Get the y dimension of playerData4D as list
     */
    fun getYSizeList(): List<Int> = playerData4D.flatten().map{ it.size}

    /**
     * Get the z dimension of playerData4D as list
     */
    fun getZSizeList(): List<Int> = playerData4D.flatten().flatten().map{ it.size }
}

/**
 * Mutable version of UniverseData
 * Contain Read only PlayerData
 */
@Serializable
data class MutableUniverseData4D(
    private val playerData4D: MutableList<List<List<List<MutableList<PlayerData>>>>>,
)