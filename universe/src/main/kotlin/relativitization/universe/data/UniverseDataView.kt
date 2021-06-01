package relativitization.universe.data

import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.physics.Int3D
import relativitization.universe.data.physics.Int4D
import relativitization.universe.maths.grid.Grids.create3DGrid
import relativitization.universe.data.serializer.DataSerializer.copy

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

        return playerGroups.map { group ->
            val prioritizedPlayerDataMap: Map<Int, PlayerData> = group.associateBy { it2 -> it2.id }

            val groupPlayerDataMap = (prioritizedPlayerDataMap +
                    playerDataMap.filter { !prioritizedPlayerDataMap.containsKey(it.key) })

            val groupPlayerId3D: List<List<List<MutableList<Int>>>> = create3DGrid(
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _ ->
                mutableListOf()
            }

            groupPlayerDataMap.map {
                val id = it.value.id
                val x = it.value.int4D.x
                val y = it.value.int4D.y
                val z = it.value.int4D.z
                groupPlayerId3D[x][y][z].add(id)
            }

            // Group playerId in 3D grid by attachedPlayerId
            val groupPlayerId3DMap: List<List<List<Map<Int, List<Int>>>>> = groupPlayerId3D.map { yList ->
                yList.map { zList ->
                    zList.map { playerList ->
                        playerList.groupBy { playerId ->
                            groupPlayerDataMap.getValue(playerId).attachedPlayerId
                        }
                    }
                }
            }

            group.map { playerData ->
                UniverseData3DAtPlayer(
                    playerData.id,
                    center,
                    groupPlayerDataMap,
                    groupPlayerId3DMap,
                    universeSettings,
                    copy(playerData)
                )
            }
        }.flatten().associateBy { it.id }
    }
}

@Serializable
data class UniverseData3DAtPlayer(
    val id: Int = -1,
    val center: Int4D = Int4D(0, 0, 0, 0),
    val playerDataMap: Map<Int, PlayerData> = mapOf(),
    val playerId3DMap: List<List<List<Map<Int, List<Int>>>>> = listOf(),
    val universeSettings: UniverseSettings = UniverseSettings(),
    var mutablePlayerData: MutablePlayerData = MutablePlayerData(-1)
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
        return playerDataMap.getOrElse(id) {
            logger.error("id $id not in playerDataMap or zeroDelayDataMap")
            PlayerData(-1)
        }
    }

    /**
     * Get set of player data by Int3D
     */
    fun get(int3D: Int3D): Map<Int, List<PlayerData>> {
        return if (isInt3DValid(int3D)) {
            playerId3DMap[int3D.x][int3D.y][int3D.z].mapValues { it1 ->
                it1.value.map { it2 -> get(it2) }
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
            playerId3DMap[int3D.x][int3D.y][int3D.z]
        } else {
            logger.error("$int3D is not a valid coordinate to get player")
            mapOf()
        }
    }


    companion object {
        private val logger = LogManager.getLogger()
    }
}