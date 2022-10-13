package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.commands.CommandData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.grid.Grids
import relativitization.universe.maths.grid.Grids.double4DToGroupId
import relativitization.universe.maths.physics.Int3D
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.maths.physics.Intervals.intDelay
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
data class UniverseData(
    val universeData4D: UniverseData4D,
    val universeSettings: UniverseSettings,
    val universeState: UniverseState,
    val commandMap: MutableMap<Int, MutableList<CommandData>>,
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
        return (getCurrentPlayerDataList().maxOfOrNull { it.playerId } ?: 0) <=
                universeState.getCurrentMaxId()
    }

    /**
     * Check if the player data in the universeData4D is valid
     */
    private fun isPlayerDataValid(): Boolean {
        val playerDataList: List<PlayerData> = getCurrentPlayerDataList()
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
     * Get a map of list of player data at an int4D location
     *
     * @param int4D 4D coordinate
     *
     * @return map of list of player data, empty map if the location is not valid
     */
    private fun getPlayerDataMapAt(int4D: Int4D): Map<Int, List<PlayerData>> {
        val currentTime = universeState.getCurrentTime()
        val tDim = universeSettings.tDim
        return if (isInt4DValid(int4D)) {
            universeData4D.getPlayerDataMapAt(
                int4D.t - currentTime + tDim - 1,
                int4D.x,
                int4D.y,
                int4D.z
            )
        } else {
            logger.debug("Getting player data at invalid coordinate")
            mapOf()
        }
    }

    /**
     * Get player data at the exact int4D
     */
    fun getPlayerDataAt(int4D: Int4D, playerId: Int): PlayerData {
        val playerDataMap: Map<Int, List<PlayerData>> = getPlayerDataMapAt(int4D)
        return playerDataMap.getValue(playerId).first {
            (it.playerId == playerId) && (it.int4D == int4D)
        }
    }

    /**
     * Get all player data which is within the view of current player
     */
    fun getAllVisiblePlayerDataList(): List<PlayerData> {
        val tSize: Int = intDelay(
            Int3D(0, 0, 0),
            Int3D(universeSettings.xDim - 1, universeSettings.yDim - 1, universeSettings.zDim - 1),
            universeSettings.speedOfLight
        )

        return universeData4D.takeLast(tSize + 1).flatten().flatten().flatten()
            .flatMap {
                it.values.flatten()
            }
    }

    fun toUniverseData3DAtGrid(center: Int4D): UniverseData3DAtGrid {
        val centerPlayerDataList: List<PlayerData> = getPlayerDataMapAt(center)
            .values.flatten()

        val playerDataMap: MutableMap<Int, PlayerData> = mutableMapOf()

        for (i in 0 until universeSettings.xDim) {
            for (j in 0 until universeSettings.yDim) {
                for (k in 0 until universeSettings.zDim) {
                    val delay: Int = intDelay(
                        center.toInt3D(),
                        Int3D(i, j, k),
                        universeSettings.speedOfLight
                    )
                    val int4D = Int4D(center.t - delay, i, j, k)
                    val playerDataMapInt4D: Map<Int, List<PlayerData>> = getPlayerDataMapAt(int4D)

                    // Check repeated playerData due to movement and time delay
                    playerDataMapInt4D.forEach { (id, playerDataList) ->
                        val playerData: PlayerData = playerDataList.maxBy {
                            it.int4D.t
                        }
                        if (playerDataMap.containsKey(id)) {
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

        val playerId3D: List<List<List<MutableList<Int>>>> = Grids.create3DGrid(
            universeSettings.xDim,
            universeSettings.yDim,
            universeSettings.zDim
        ) { _, _, _ ->
            mutableListOf()
        }

        playerDataMap.forEach {
            val id = it.value.playerId
            val x = it.value.int4D.x
            val y = it.value.int4D.y
            val z = it.value.int4D.z
            playerId3D[x][y][z].add(id)
        }

        // Group playerId in 3D grid by groupId
        val playerId3DMap: List<List<List<Map<Int, List<Int>>>>> =
            playerId3D.map { yList ->
                yList.map { zList ->
                    zList.map { playerList ->
                        playerList.groupBy { playerId ->
                            playerDataMap.getValue(playerId).groupId
                        }
                    }
                }
            }

        return UniverseData3DAtGrid(
            center = center,
            centerPlayerDataList = centerPlayerDataList,
            playerDataMap = playerDataMap,
            playerId3DMap = playerId3DMap,
            universeSettings = universeSettings,
        )
    }

    /**
     * Get all player data from the latest universe slice
     * Since there can be duplicated player id since the after image may be added after player move,
     * only player data at current time is included
     */
    fun getCurrentPlayerDataList(): List<PlayerData> {
        return universeData4D.getLatest().flatten().flatten().flatMap { playerDataMap ->
            playerDataMap.values.flatMap { playerDataList ->
                playerDataList.filter {
                    it.int4D.t == universeState.getCurrentTime()
                }
            }
        }
    }

    /**
     * Update the universe by adding a new slice, remove the oldest slice and updating the universe state
     */
    fun updateUniverseDropOldest(slice: List<List<List<Map<Int, List<PlayerData>>>>>) {
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
    fun updateUniverseReplaceLatest(slice: List<List<List<Map<Int, List<PlayerData>>>>>) {
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
    private val playerData4D: MutableList<List<List<List<Map<Int, List<PlayerData>>>>>>,
) {
    /**
     * Get player data from the 4D List
     * i, j, k, l doesn't necessarily the actual t, x, y, z
     */
    internal fun getPlayerDataMapAt(i: Int, j: Int, k: Int, l: Int): Map<Int, List<PlayerData>> {
        return playerData4D[i][j][k][l]
    }

    /**
     * Get latest 3D slice
     */
    fun getLatest(): List<List<List<Map<Int, List<PlayerData>>>>> =
        playerData4D.last()

    /**
     * Get latest 4D slice
     */
    fun takeLast(tSize: Int): List<List<List<List<Map<Int, List<PlayerData>>>>>> {
        if (tSize > playerData4D.size) {
            logger.error("tSize is larger than the dimension of the universe")
        }

        return playerData4D.takeLast(tSize)
    }

    /**
     * Get all slice excluding latest
     */
    fun getAllExcludeLatest(): List<List<List<List<Map<Int, List<PlayerData>>>>>> =
        playerData4D.dropLast(1)

    /**
     * Add one new universe 3d slice and remove the oldest one
     */
    internal fun addAndRemoveFirstUniverse3DSlice(
        slice: List<List<List<Map<Int, List<PlayerData>>>>>
    ) {
        playerData4D.removeFirst()
        playerData4D.add(slice)
    }

    /**
     * Replace latest universe slice
     */
    internal fun addAndRemoveLastUniverse3DSlice(
        slice: List<List<List<Map<Int, List<PlayerData>>>>>
    ) {
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
    private val playerData4D: MutableList<List<List<List<MutableMap<Int, MutableList<PlayerData>>>>>>,
) {
    /**
     * Add player data to data
     * Output error log and don't do anything if the coordinate is out of bound
     *
     * @param playerData the player data to be added
     * @param currentTime the current time of the universe, the player data is added to the grid relative to this time
     * @param edgeLength the length of the cube defining same group of players
     */
    private fun addPlayerData(
        playerData: PlayerData,
        currentTime: Int,
        edgeLength: Double
    ) {
        val tSize: Int = playerData4D.size

        // get the player data list at the grid, or empty list if the coordinate is not correct
        val playerDataList: MutableList<PlayerData> = playerData4D.getOrElse(
            playerData.int4D.t - currentTime + tSize - 1
        ) {
            logger.error("Wrong int4D ${playerData.int4D}")
            listOf()
        }.getOrElse(playerData.int4D.x) {
            logger.error("Wrong int4D ${playerData.int4D}")
            listOf()
        }.getOrElse(playerData.int4D.y) {
            logger.error("Wrong int4D ${playerData.int4D}")
            listOf()
        }.getOrElse(playerData.int4D.z) {
            logger.error("Wrong int4D ${playerData.int4D}")
            mutableMapOf()
        }.getOrPut(playerData.playerId) {
            mutableListOf()
        }

        // Modified player data double 4D if it doesn't fit int4D
        val int4D = playerData.int4D

        val playerDataWithCorrect4D: PlayerData = if (!playerData.double4D.atInt4D(int4D)) {
            logger.debug("Add player ${playerData.playerId}: force changing new player double4D")
            if (playerData.double4D.atInt3D(int4D.toInt3D())) {
                logger.debug("Force change t only")
                playerData.copy(
                    double4D = playerData.double4D.copy(t = int4D.t.toDouble())
                )
            } else {
                logger.debug("Force change double4D")
                playerData.copy(
                    double4D = int4D.toDouble4DCenter()
                )
            }
        } else {
            playerData
        }

        // Default group player id should be player id itself
        val playerDataWithCorrectGroup: PlayerData = if (
            playerDataWithCorrect4D.groupId != double4DToGroupId(
                playerDataWithCorrect4D.double4D,
                edgeLength
            )
        ) {
            logger.debug("Default the group id to ${playerDataWithCorrect4D.groupId}")
            playerDataWithCorrect4D.copy(
                groupId = double4DToGroupId(
                    playerDataWithCorrect4D.double4D,
                    edgeLength
                )
            )
        } else {
            playerDataWithCorrect4D
        }

        // Add player to the list
        playerDataList.add(playerDataWithCorrectGroup)
    }

    /**
     * Add player data to the latest time slice, also add after image to prevent player disappearing after move
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
            val playerData: PlayerData = DataSerializer.copy(mutablePlayerData)
            // Fix player time
            val playerTime: Int = currentTime - time
            addPlayerData(
                playerData.copy(
                    int4D = playerData.int4D.copy(
                        t = playerTime,
                    )
                ),
                currentTime,
                edgeLength
            )
        }
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}