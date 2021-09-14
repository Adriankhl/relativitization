package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.commands.Command
import relativitization.universe.data.component.physics.Int3D
import relativitization.universe.data.component.physics.Int4D
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.grid.Grids.create3DGrid
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
data class UniverseData3DAtGrid(
    val center: Int4D,
    val centerPlayerDataList: List<PlayerData>,
    val playerDataMap: Map<Int, PlayerData>,
    val universeSettings: UniverseSettings,
) {
    fun idToUniverseData3DAtPlayer(): Map<Int, UniverseData3DAtPlayer> {
        // group by group id
        val playerGroups: List<List<PlayerData>> = centerPlayerDataList.groupBy { it.groupId }.values.toList()

        return playerGroups.map { group ->
            val prioritizedPlayerDataMap: Map<Int, PlayerData> = group.associateBy { it2 -> it2.playerId }

            val recentPrioritizedDataMap: Map<Int, PlayerData> = prioritizedPlayerDataMap.filter {
                val hasPlayerInHistory: Boolean = playerDataMap.containsKey(it.key)

                // If the playerDataMap has this data, take the recent one
                if (hasPlayerInHistory) {
                    playerDataMap.getValue(it.key).int4D.t < it.value.int4D.t
                } else {
                    true
                }
            }

            val groupPlayerDataMap = recentPrioritizedDataMap + playerDataMap.filter {
                !recentPrioritizedDataMap.containsKey(it.key)
            }

            val groupPlayerId3D: List<List<List<MutableList<Int>>>> = create3DGrid(
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _ ->
                mutableListOf()
            }

            groupPlayerDataMap.forEach {
                val id = it.value.playerId
                val x = it.value.int4D.x
                val y = it.value.int4D.y
                val z = it.value.int4D.z
                groupPlayerId3D[x][y][z].add(id)
            }

            // Group playerId in 3D grid by groupId
            val groupPlayerId3DMap: List<List<List<Map<Int, List<Int>>>>> = groupPlayerId3D.map { yList ->
                yList.map { zList ->
                    zList.map { playerList ->
                        playerList.groupBy { playerId ->
                            groupPlayerDataMap.getValue(playerId).groupId
                        }
                    }
                }
            }

            group.filter {
                // Prevent turning after image to UniverseData3DAtPlayer
                it.int4D.t == center.t
            }.map { playerData ->
                UniverseData3DAtPlayer(
                    playerData.playerId,
                    center,
                    groupPlayerDataMap,
                    groupPlayerId3DMap,
                    universeSettings,
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
     * Get current player data
     */
    fun getCurrentPlayerData(): PlayerData {
        return get(id)
    }

    /**
     * Get neighbour of current player
     */
    fun getNeighbour(): List<PlayerData> {
        val currentPlayer: PlayerData = getCurrentPlayerData()
        return get(currentPlayer.int4D.toInt3D()).getValue(currentPlayer.groupId).filter {
            // Filter out afterimage player data, which have different t coordinate
            it.int4D.t == currentPlayer.int4D.t
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

    fun getPlanDataAtPlayer(): PlanDataAtPlayer {
        return PlanDataAtPlayer(
            this,
            DataSerializer.copy(getCurrentPlayerData())
        )
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * For human or ai to decide the command list
 */
data class PlanDataAtPlayer(
    val universeData3DAtPlayer: UniverseData3DAtPlayer,
    var thisPlayerData: MutablePlayerData,
    val playerDataMap: MutableMap<Int, MutablePlayerData> = mutableMapOf(),
    val commandList: MutableList<Command> = mutableListOf(),
) {
    fun addCommand(command: Command) {
        val playerData: MutablePlayerData = playerDataMap.getOrPut(command.toId) {
            DataSerializer.copy(universeData3DAtPlayer.get(command.toId))
        }

        if (playerData.playerId == -1) {
            logger.error("Add command error: Player id -1")
        } else {
            command.checkAndSelfExecuteBeforeSend(thisPlayerData, universeData3DAtPlayer.universeSettings)
            command.checkAndExecute(playerData, universeData3DAtPlayer.universeSettings)
        }
    }

    fun addAllCommand(commandList: List<Command>) {
        commandList.forEach {
            addCommand(it)
        }
    }

    fun resetPlayerData(playerId: Int) {
        if (playerId == thisPlayerData.playerId) {
            thisPlayerData = DataSerializer.copy(universeData3DAtPlayer.getCurrentPlayerData())
            commandList.forEach {
                it.checkAndSelfExecuteBeforeSend(thisPlayerData, universeData3DAtPlayer.universeSettings)
            }
        } else {
            playerDataMap[playerId] = DataSerializer.copy(universeData3DAtPlayer.get(playerId))
            val playerData: MutablePlayerData = playerDataMap.getValue(playerId)
            commandList.filter {
                it.toId == playerId
            }.forEach {
                it.checkAndExecute(playerData, universeData3DAtPlayer.universeSettings)
            }
        }
    }

    fun removeCommand(command: Command) {
        commandList.remove(command)
        resetPlayerData(command.toId)
        resetPlayerData(command.fromId)
    }

    fun removeAllCommand(commandListToRemove: List<Command>) {
        commandList.removeAll(commandListToRemove)
        val playerIdToReset: List<Int> = listOf(thisPlayerData.playerId) + commandListToRemove.map {
            it.toId
        }.toSet()

        playerIdToReset.forEach {
            resetPlayerData(it)
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}