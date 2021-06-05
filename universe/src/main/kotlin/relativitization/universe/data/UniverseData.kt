package relativitization.universe.data

import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.commands.Command
import relativitization.universe.data.physics.*
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.maths.grid.Grids.create3DGrid
import relativitization.universe.maths.physics.Intervals.intDelay

@Serializable
data class UniverseData(
    val universeData4D: UniverseData4D,
    val universeSettings: UniverseSettings,
    val universeState: UniverseState,
    val commandMap: MutableMap<Int, MutableList<Command>>
) {
    /**
     * Check whether the universeData4D has the correct dimension specified in the setting
     */
    private fun isDimensionValid(): Boolean {
        val tCheckList = universeData4D.getTSizeList().map { it == universeSettings.tDim }
        val xCheckList = universeData4D.getXSizeList().map { it == universeSettings.xDim }
        val yCheckList = universeData4D.getYSizeList().map { it == universeSettings.yDim }
        val zCheckList = universeData4D.getZSizeList().map { it == universeSettings.zDim }

        return !(tCheckList + xCheckList + yCheckList + zCheckList).contains(false)
    }

    /**
     * Check if the universe state is valid
     */
    private fun isStateValid(): Boolean {
        // Not a valid test
        // val currentTimeCheck: Boolean = universeData4D.getTSizeList()[0] >= universeState.getCurrentTime()
        val currentIdCheck: Boolean = getLatestPlayerDataList().maxOf { it.id } <= universeState.getCurrentMaxId()
        return currentIdCheck
    }

    /**
     * Check if the player data in the universeData4D is valid
     */
    private fun isPlayerDataValid(): Boolean {
        val playerDataList: List<PlayerData> = getLatestPlayerDataList()
        val playerDataCheckList: List<Boolean> = playerDataList.map { it.isValid(universeState.getCurrentTime()) }
        return !playerDataCheckList.contains(false)
    }

    /**
     * Check if the universe is valid
     */
    fun isUniverseValid(): Boolean {
        logger.debug("Is setting valid: ${universeSettings.isSettingValid()}")
        logger.debug("Is dimension valid: ${isDimensionValid()}")
        logger.debug("Is state valid: ${isStateValid()}")
        logger.debug("Is player data valid: ${isPlayerDataValid()}")
        return universeSettings.isSettingValid() && isDimensionValid() && isStateValid() && isPlayerDataValid()
    }

    /**
     * Check whether the int4D coordinate is within the range of our setting of the stored data
     */
    private fun isInt4DValid(int4D: Int4D): Boolean {
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

    /**
     * Get player data as a list at a int4D location
     *
     * @param int4D 4D coordinate
     *
     * @return list of player data, empty list if the location is not valid
     */
    private fun getPlayerDataListAt(int4D: Int4D): List<PlayerData> {
        val currentTime = universeState.getCurrentTime()
        val tDim = universeSettings.tDim
        return if (isInt4DValid(int4D)) {
            universeData4D.getPlayerDataList(int4D.t - currentTime + tDim - 1, int4D.x, int4D.y, int4D.z)
        } else {
            logger.debug("Getting player data at invalid coordinate")
            listOf()
        }
    }

    /**
     * Get player data at the exact int4D
     */
    fun getPlayerDataAt(int4D: Int4D, playerId: Int): PlayerData {
        val playerDataList: List<PlayerData> = getPlayerDataListAt(int4D)
        return playerDataList.first {
            (it.id == playerId) && (it.int4D == int4D)
        }
    }

    fun toUniverseData3DAtGrid(center: Int4D): UniverseData3DAtGrid {
        val playerId3D: List<List<List<MutableList<Int>>>> =
            create3DGrid(
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _ -> mutableListOf() }

        val playerDataMap: MutableMap<Int, PlayerData> = mutableMapOf()

        for (i in 0 until universeSettings.xDim) {
            for (j in 0 until universeSettings.yDim) {
                for (k in 0 until universeSettings.zDim) {
                    val delay = intDelay(center.toInt3D(), Int3D(i, j, k), universeSettings.speedOfLight)
                    val int3D = Int3D(i, j, k)
                    val int4D = Int4D(center.t - delay, i, j, k)
                    val playerDataList = getPlayerDataListAt(int4D)

                    // Check repeated playerData due to movement and time delay
                    for (playerData in playerDataList) {
                        val id = playerData.id
                        if (playerDataMap.containsKey(playerData.id)) {
                            // Two duplicate possibility:
                            // 1. time difference -> take the latest one
                            // 2. same time, different space -> take the one with the correct space coordinate
                            if (playerData.int4D.t > playerDataMap.getValue(id).int4D.t) {
                                val oldInt3D = playerDataMap.getValue(id).int4D.toInt3D()
                                playerId3D[oldInt3D.x][oldInt3D.y][oldInt3D.z].remove(id)

                                playerId3D[i][j][k].add(id)
                                playerDataMap[id] = playerData
                            } else if (playerData.int4D.t == playerDataMap.getValue(id).int4D.t) {
                                if (playerData.int4D.toInt3D() == int3D ) {
                                    val oldInt3D = playerDataMap.getValue(id).int4D.toInt3D()
                                    playerId3D[oldInt3D.x][oldInt3D.y][oldInt3D.z].remove(id)

                                    playerId3D[i][j][k].add(id)
                                    playerDataMap[id] = playerData
                                }
                            } else {
                                // Do nothing
                            }
                        } else {
                            playerId3D[i][j][k].add(id)
                            playerDataMap[id] = playerData
                        }
                    }
                }
            }
        }

        val centerPlayerDataList: List<PlayerData> = getPlayerDataListAt(center)

        return UniverseData3DAtGrid(
            center,
            centerPlayerDataList,
            playerDataMap,
            playerId3D,
            universeSettings
        )
    }

    /**
     * Get all player data from the latest universe slice
     * Since there can be duplicated player id since the after image may be added after player move,
     * only player data with maximum t is included
     */
    fun getLatestPlayerDataList(): List<PlayerData> {
        val playerDataList: List<PlayerData> = universeData4D.getLatest().flatten().flatten().flatten()
        return playerDataList.groupBy { it.id }.map { (_, v) -> v.maxByOrNull { it.int4D.t } }.filterNotNull()
    }

    /**
     * Update the universe by adding a new slice, remove the oldest slice and updating the universe state
     */
    fun updateUniverseDropOldest(slice: List<List<List<List<PlayerData>>>>) {
        universeData4D.addAndRemoveFirstUniverse3DSlice(slice)
        universeState.updateTime()
        if (!isUniverseValid()) {
            logger.error("Updated universe is not valid")
        }
    }


    /**
     * Update universe by replacing the latest slice
     * Should not update time in the universeState
     */
    fun updateUniverseReplaceLatest(slice: List<List<List<List<PlayerData>>>>) {
        universeData4D.addAndRemoveLastUniverse3DSlice(slice)
        if (!isUniverseValid()) {
            logger.error("Updated universe is not valid")
        }
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
    internal fun getPlayerDataList(i: Int, j: Int, k: Int, l: Int): List<PlayerData> {
        return playerData4D[i][j][k][l]
    }

    /**
     * Get latest 3D slice
     */
    fun getLatest(): List<List<List<List<PlayerData>>>> =
        playerData4D.last()

    /**
     * Get all slice excluding latest
     */
    fun getAllExcludeLatest(): List<List<List<List<List<PlayerData>>>>> =
        playerData4D.dropLast(1)

    /**
     * Add one new universe 3d slice and remove the oldest one
     */
    internal fun addAndRemoveFirstUniverse3DSlice(slice: List<List<List<List<PlayerData>>>>) {
        playerData4D.removeFirst()
        playerData4D.add(slice)
    }

    /**
     * Replace latest universe slice
     */
    internal fun addAndRemoveLastUniverse3DSlice(slice: List<List<List<List<PlayerData>>>>) {
        playerData4D.removeLast()
        playerData4D.add(slice)
    }

    /**
     * Get the t dimension of playerData4D as list
     */
    internal fun getTSizeList(): List<Int> = listOf(playerData4D.size)

    /**
     * Get the x dimension of playerData4D as list
     */
    internal fun getXSizeList(): List<Int> = playerData4D.map { it.size }

    /**
     * Get the y dimension of playerData4D as list
     */
    internal fun getYSizeList(): List<Int> = playerData4D.flatten().map{ it.size}

    /**
     * Get the z dimension of playerData4D as list
     */
    internal fun getZSizeList(): List<Int> = playerData4D.flatten().flatten().map{ it.size }
}

/**
 * Mutable version of UniverseData
 * Contain Read only PlayerData
 */
@Serializable
data class MutableUniverseData4D(
    private val playerData4D: MutableList<List<List<List<MutableList<PlayerData>>>>>,
) {
    /**
     * Add player data to data
     * Output error log and don't do anything if the coordinate is out of bound
     */
    fun addPlayerData(mutablePlayerData: MutablePlayerData, currentTime: Int) {
        val tSize: Int = playerData4D.size

        // Modified player data double 4D if it doesn't fit int4D
        val int4D = mutablePlayerData.int4D
        if (!mutablePlayerData.double4D.atInt4D(int4D)) {
            logger.debug("Add player: force changing new player double4D")
            mutablePlayerData.double4D = int4D.toMutableDouble4DCenter()
        }

        playerData4D.getOrElse(mutablePlayerData.int4D.t - currentTime + tSize - 1) {
            logger.error("Wrong int4D ${mutablePlayerData.int4D}")
            listOf()
        }.getOrElse(mutablePlayerData.int4D.x) {
            logger.error("Wrong int4D ${mutablePlayerData.int4D}")
            listOf()
        }.getOrElse(mutablePlayerData.int4D.y) {
            logger.error("Wrong int4D ${mutablePlayerData.int4D}")
            listOf()
        }.getOrElse(mutablePlayerData.int4D.z) {
            logger.error("Wrong int4D ${mutablePlayerData.int4D}")
            mutableListOf()
        }.add(copy(mutablePlayerData))
    }

    /**
     * Add player data to data
     * Output error log and don't do anything if the coordinate is out of bound
     */
    fun addPlayerDataToLatest(mutablePlayerData: MutablePlayerData, currentTime: Int) {
        mutablePlayerData.int4D.t = currentTime
        addPlayerData(mutablePlayerData, currentTime)
    }


    companion object {
        private val logger = LogManager.getLogger()
    }
}