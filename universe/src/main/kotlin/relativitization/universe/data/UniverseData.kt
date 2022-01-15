package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.physics.Int3D
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.grid.Grids.double4DToGroupId
import relativitization.universe.maths.physics.Intervals.intDelay
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
data class UniverseData(
    val universeData4D: UniverseData4D,
    val universeSettings: UniverseSettings,
    val universeState: UniverseState,
    val commandMap: MutableMap<Int, MutableList<Command>>,
    var universeGlobalData: UniverseGlobalData,
) {
    /**
     * Check whether the universeData4D has the correct dimension specified in the setting
     */
    private fun isDimensionValid(): Boolean {
        val isTValid: Boolean = universeData4D.getTSizeList().all { it == universeSettings.tDim }
        val isXValid: Boolean = universeData4D.getXSizeList().all { it == universeSettings.xDim }
        val isYValid: Boolean = universeData4D.getYSizeList().all { it == universeSettings.yDim }
        val isZValid: Boolean = universeData4D.getZSizeList().all { it == universeSettings.zDim }

        return isTValid && isXValid && isYValid && isZValid
    }

    /**
     * Check if the universe state is valid
     */
    private fun isStateValid(): Boolean {
        // Not a valid test
        // val currentTimeCheck: Boolean = universeData4D.getTSizeList()[0] >= universeState.getCurrentTime()
        val currentIdCheck: Boolean = (getLatestPlayerDataList().maxOfOrNull { it.playerId } ?: 0) <=
                universeState.getCurrentMaxId()
        return currentIdCheck
    }

    /**
     * Check if the player data in the universeData4D is valid
     */
    private fun isPlayerDataValid(): Boolean {
        val playerDataList: List<PlayerData> = getLatestPlayerDataList()
        val playerDataCheckList: List<Boolean> = playerDataList.map {
            it.isValid(universeState.getCurrentTime())
        }
        return playerDataCheckList.all { it }
    }

    /**
     * Check if the universe is valid
     */
    fun isUniverseValid(): Boolean {
        val isSettingValid: Boolean = universeSettings.isSettingValid()
        if (!isSettingValid) {
            logger.error("Universe setting is not valid")
        }

        val isDimensionValid: Boolean = isDimensionValid()
        if (!isDimensionValid) {
            logger.error("Universe dimension is not valid")
        }

        val isStateValid: Boolean = isStateValid()
        if (!isSettingValid) {
            logger.error("Universe state is not valid")
        }

        val isPlayerDataValid: Boolean = isPlayerDataValid()
        if (!isPlayerDataValid) {
            logger.error("Some player data is not valid")
        }

        return isSettingValid && isDimensionValid && isStateValid && isPlayerDataValid
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
            universeData4D.getPlayerDataList(
                int4D.t - currentTime + tDim - 1,
                int4D.x,
                int4D.y,
                int4D.z
            )
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
            (it.playerId == playerId) && (it.int4D == int4D)
        }
    }

    /**
     * Get all player data which is within the view of current player
     */
    fun getAllVisiblePlayerData(): List<PlayerData> {
        val tSize: Int = intDelay(
            Int3D(0, 0, 0),
            Int3D(universeSettings.xDim - 1, universeSettings.yDim - 1, universeSettings.zDim - 1),
            universeSettings.speedOfLight
        )

        val data4D: List<List<List<List<List<PlayerData>>>>> = universeData4D.takeLast(tSize + 1)

        return data4D.flatten().flatten().flatten().flatten()
    }

    fun toUniverseData3DAtGrid(center: Int4D): UniverseData3DAtGrid {
        val playerDataMap: MutableMap<Int, PlayerData> = mutableMapOf()

        for (i in 0 until universeSettings.xDim) {
            for (j in 0 until universeSettings.yDim) {
                for (k in 0 until universeSettings.zDim) {
                    val delay =
                        intDelay(center.toInt3D(), Int3D(i, j, k), universeSettings.speedOfLight)
                    val int4D = Int4D(center.t - delay, i, j, k)
                    val playerDataList = getPlayerDataListAt(int4D)

                    // Check repeated playerData due to movement and time delay
                    for (playerData in playerDataList) {
                        val id = playerData.playerId
                        if (playerDataMap.containsKey(playerData.playerId)) {
                            // Take newer data if duplicate
                            if (playerData.int4D.t > playerDataMap.getValue(id).int4D.t) {
                                playerDataMap[id] = playerData
                            }
                        } else {
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
            universeSettings
        )
    }

    /**
     * Get all player data from the latest universe slice
     * Since there can be duplicated player id since the after image may be added after player move,
     * only player data with maximum t is included
     */
    fun getLatestPlayerDataList(): List<PlayerData> {
        val playerDataList: List<PlayerData> =
            universeData4D.getLatest().flatten().flatten().flatten()

        // If at least one player is alive, get the t from that player
        val maxT: Int = playerDataList.maxOfOrNull { it.int4D.t } ?: 0

        return playerDataList.groupBy { it.playerId }.map { (_, playerDataGroup) ->
            // If player is dead, only after image is left, none should be satisfy
            playerDataGroup.firstOrNull { it.int4D.t == maxT }
        }.filterNotNull()
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
        private val logger = RelativitizationLogManager.getLogger()
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
     * Get latest 4D slice
     */
    fun takeLast(tSize: Int): List<List<List<List<List<PlayerData>>>>> {
        if (tSize > playerData4D.size) {
            logger.error("tSize is larger than the dimension of the universe")
        }

        return playerData4D.takeLast(tSize)
    }

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
    internal fun getYSizeList(): List<Int> = playerData4D.flatten().map { it.size }

    /**
     * Get the z dimension of playerData4D as list
     */
    internal fun getZSizeList(): List<Int> = playerData4D.flatten().flatten().map { it.size }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
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
     *
     * @param mutablePlayerData the player data to be added
     * @param currentTime the current time of the universe, the player data is added to the grid relative to this time
     * @param edgeLength the length of the cube defining same group of players
     */
    fun addPlayerData(mutablePlayerData: MutablePlayerData, currentTime: Int, edgeLength: Double) {
        val tSize: Int = playerData4D.size

        // Sync data component
        mutablePlayerData.syncData()

        // Modified player data double 4D if it doesn't fit int4D
        val int4D = mutablePlayerData.int4D
        if (!mutablePlayerData.double4D.atInt4D(int4D)) {
            logger.debug("Add player ${mutablePlayerData.playerId}: force changing new player double4D")

            if (mutablePlayerData.double4D.toMutableDouble3D().atInt3D(int4D.toMutableInt3D())) {
                logger.debug("Force change t only")
                mutablePlayerData.double4D.t = int4D.t.toDouble()
            } else {
                logger.debug("Force change double4D")
                mutablePlayerData.double4D = int4D.toMutableDouble4DCenter()
            }
        }

        // get the player data list at the grid, or empty list if the coordinate is not correct
        val playerDataList: MutableList<PlayerData> = playerData4D.getOrElse(
            mutablePlayerData.int4D.t - currentTime + tSize - 1
        ) {
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
        }

        // Default group player id should be player id itself
        if (mutablePlayerData.groupId != double4DToGroupId(
                mutablePlayerData.double4D,
                edgeLength
            )
        ) {
            mutablePlayerData.groupId = double4DToGroupId(mutablePlayerData.double4D, edgeLength)
            logger.debug("Default the group id to ${mutablePlayerData.groupId}")
        }

        // Add player to the list
        playerDataList.add(DataSerializer.copy(mutablePlayerData))
    }

    /**
     * Add player data to latest time slice, also add after image to prevent player disappearing after move
     * Also sync data component
     *
     * @param mutablePlayerData the data of the player to be added
     * @param currentTime the current time of the universe, which the player data time will be changed to this time
     * @param edgeLength the length of the cube defining same group of players
     */
    fun addPlayerDataToLatestWithAfterImage(
        mutablePlayerData: MutablePlayerData,
        currentTime: Int,
        edgeLength: Double,
        playerAfterImageDuration: Int,
    ) {
        for (time in 0..playerAfterImageDuration) {
            mutablePlayerData.int4D.t = currentTime - time
            addPlayerData(mutablePlayerData, currentTime, edgeLength)
        }
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}