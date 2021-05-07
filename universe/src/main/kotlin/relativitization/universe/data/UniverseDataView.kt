package relativitization.universe.data

import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.physics.Int4D
import relativitization.universe.maths.grid.Grids.create3DGrid
import kotlin.math.max
import kotlin.math.min

@Serializable
data class UniverseData3DAtGrid(
    val center: Int4D,
    val centerPlayerDataList: List<PlayerData>,
    val playerDataMap: Map<Int, PlayerData>,
    val playerId3D: List<List<List<List<Int>>>>,
    val universeSettings: UniverseSettings,
) {
    fun idToUniverseData3DAtPlayer(): Map<Int, UniverseData3DAtPlayer> {
        // group by attached id
        val playerGroups: List<List<PlayerData>> = centerPlayerDataList.groupBy { it.attachedPlayerId }.values.toList()

        // Group playerId in 3D grid by attachedPlayerId
        val playerId3DMap: List<List<List<Map<Int, List<Int>>>>> = playerId3D.map { yList ->
            yList.map { zList ->
                zList.map { playerList ->
                    playerList.groupBy { playerId ->
                        playerDataMap.getValue(playerId).attachedPlayerId }
                }
            }
        }

        return playerGroups.map { group ->
            val prioritizedPlayerDataMap: Map<Int, PlayerData> = group.associateBy { it2 -> it2.id }

            val prioritizedPlayerId3D: List<List<List<MutableList<Int>>>> = create3DGrid(3, 3, 3) {
                    _, _, _ -> mutableListOf()
            }
            // Add id from the original playerId3D if it is not presented in the prioritizedPlayerDataMap
            for (i in max(0, center.x - 1)..min(universeSettings.xDim, center.x + 1))
                for (j in max(0, center.y - 1)..min(universeSettings.yDim, center.y + 1))
                    for (k in max(0, center.z - 1)..min(universeSettings.zDim, center.z + 1))
                        for (id in playerId3D[i][j][k])
                            if (!prioritizedPlayerDataMap.containsKey(id))
                                prioritizedPlayerId3D[i - center.x + 1][j - center.y + 1][k - center.z + 1].add(id)

            prioritizedPlayerId3D[1][1][1].addAll(prioritizedPlayerDataMap.keys)

            val prioritizedPlayerId3DMap: List<List<List<Map<Int, List<Int>>>>> = prioritizedPlayerId3D.map { yList ->
                yList.map { zList ->
                    zList.map { playerList ->
                        playerList.groupBy { playerId ->
                            playerDataMap.getValue(playerId).attachedPlayerId }
                    }
                }
            }

            group.map { playerData ->
                UniverseData3DAtPlayer(
                    playerData.id,
                    center,
                    prioritizedPlayerDataMap,
                    prioritizedPlayerId3DMap,
                    playerDataMap,
                    playerId3DMap,
                    universeSettings
                )
            }
        }.flatten().associateBy { it.id }
    }
}

@Serializable
data class UniverseData3DAtPlayer(
    val id: Int = -1,
    val center: Int4D = Int4D(0, 0, 0, 0),
    private val prioritizedPlayerDataMap: Map<Int, PlayerData> = mapOf(),
    private val prioritizedPlayerId3DMap: List<List<List<Map<Int, List<Int>>>>> = listOf(),
    private val playerDataMap: Map<Int, PlayerData> = mapOf(),
    private val playerId3DMap: List<List<List<Map<Int, List<Int>>>>> = listOf(),
    val universeSettings: UniverseSettings = UniverseSettings(),
) {
    /**
     * Check int3D valid
     */
    fun isInt3DValid(int3D: Int3D): Boolean {
        val xLower: Boolean = int3D.x >= 0
        val xUpper: Boolean = int3D.x < universeSettings.xDim
        val yLower: Boolean = int3D.y >= 0
        val yUpper: Boolean = int3D.y < universeSettings.yDim
        val zLower: Boolean = int3D.z >= 0
        val zUpper: Boolean = int3D.z < universeSettings.zDim

        return xLower && xUpper && yLower && yUpper && zLower && zUpper
    }

    /**
     * Get player data by id
     */
    fun get(id: Int): PlayerData {
        return when {
            prioritizedPlayerDataMap.containsKey(id) -> {
                prioritizedPlayerDataMap.getValue(id)
            }
            playerDataMap.containsKey(id) -> {
                playerDataMap.getValue(id)
            }
            else -> {
                logger.error("id $id not in playerDataMap or zeroDelayDataMap")
                PlayerData(-1)
            }
        }
    }

    /**
     * Get set of player data by Int3D
     */
    fun get(int3D: Int3D): Map<Int, List<PlayerData>> {
        return if (isInt3DValid(int3D)) {
            if (center.toInt3D().isNearby(int3D)) {
                prioritizedPlayerId3DMap[int3D.x - center.x + 1][int3D.y - center.y + 1][int3D.z - center.z + 1].mapValues { it1 ->
                    it1.value.map { it2 -> get(it2) }
                }
            } else {
                playerId3DMap[int3D.x][int3D.y][int3D.z].mapValues { it1 ->
                    it1.value.map { it2 -> get(it2) }
                }
            }
        } else {
            logger.error("$int3D is not a valid coordinate to get player")
            mapOf()
        }
    }

    /**
     * Get set of player id by Int3D
     */
    fun getIdMap(int3D: Int3D): Map<Int, List<Int>> {
        return if (isInt3DValid(int3D)) {
            if (center.toInt3D().isNearby(int3D)) {
                prioritizedPlayerId3DMap[int3D.x - center.x + 1][int3D.y - center.y + 1][int3D.z - center.z + 1]
            } else {
                playerId3DMap[int3D.x][int3D.y][int3D.z]
            }
        } else {
            logger.error("$int3D is not a valid coordinate to get player")
            mapOf()
        }
    }



    companion object {

        private val logger = LogManager.getLogger()

    }
}