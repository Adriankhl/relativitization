package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.physics.Int3D
import relativitization.universe.data.components.defaults.physics.Int4D
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.maths.grid.Grids.create3DGrid
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.max
import kotlin.math.min

@Serializable
data class UniverseData3DAtGrid(
    val center: Int4D,
    val centerPlayerDataList: List<PlayerData>,
    val playerDataMap: Map<Int, PlayerData>,
    val universeSettings: UniverseSettings,
) {
    fun idToUniverseData3DAtPlayer(): Map<Int, UniverseData3DAtPlayer> {
        // group by group id
        val playerGroups: List<List<PlayerData>> =
            centerPlayerDataList.groupBy { it.groupId }.values.toList()

        return playerGroups.map { group ->
            val prioritizedPlayerDataMap: Map<Int, PlayerData> =
                group.associateBy { it2 -> it2.playerId }

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
            val groupPlayerId3DMap: List<List<List<Map<Int, List<Int>>>>> =
                groupPlayerId3D.map { yList ->
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
     *
     * @return a map from group id to player data
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
     * Get neighbour player in the same group, excluding self
     */
    fun getNeighbour(): List<PlayerData> {
        val currentPlayer: PlayerData = getCurrentPlayerData()
        return get(currentPlayer.int4D.toInt3D()).getValue(currentPlayer.groupId).filter {
            // Filter out afterimage player data, which have different t coordinate
            (it.int4D.t == currentPlayer.int4D.t) && (it.playerId != currentPlayer.playerId)
        }
    }

    /**
     * Get neighbour player within a cube containing current player, excluding self
     *
     * @param range half of the length of the cube
     *
     */
    fun getNeighbourAndSelf(range: Int): List<PlayerData> {
        val currentPlayer: PlayerData = getCurrentPlayerData()
        val int3D: Int3D = currentPlayer.int4D.toInt3D()

        return (max(int3D.x - range, 0)..min(int3D.x + range, universeSettings.xDim - 1)).map { x ->
            (max(int3D.y - range, 0)..min(int3D.y + range, universeSettings.yDim - 1)).map { y ->
                (max(int3D.z - range, 0)..min(
                    int3D.z + range,
                    universeSettings.zDim - 1
                )).map { z ->
                    get(Int3D(x, y, z)).values
                }
            }
        }.flatten().flatten().flatten().flatten()
    }

    /**
     * Get neighbour player within a cube containing current player, excluding self
     *
     * @param range half of the length of the cube
     *
     */
    fun getNeighbour(range: Int): List<PlayerData> {
        val currentPlayer: PlayerData = getCurrentPlayerData()
        return getNeighbourAndSelf(range).filter {
            it.playerId != currentPlayer.playerId
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

    fun getPlanDataAtPlayer(onCommandListChange: () -> Unit): PlanDataAtPlayer {
        return PlanDataAtPlayer(
            this,
            onCommandListChange,
        )
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * For human or ai to decide the command list
 */
class PlanDataAtPlayer(
    val universeData3DAtPlayer: UniverseData3DAtPlayer,
    var onCommandListChange: () -> Unit,
    val playerDataMap: MutableMap<Int, MutablePlayerData> = mutableMapOf(),
    val commandList: MutableList<Command> = mutableListOf(),
) {
    fun getPlayerData(id: Int): PlayerData {
        return if (playerDataMap.containsKey(id)) {
            DataSerializer.copy(playerDataMap.getValue(id))
        } else {
            universeData3DAtPlayer.get(id)
        }
    }

    fun getCurrentPlayerData(): PlayerData {
        return getPlayerData(universeData3DAtPlayer.getCurrentPlayerData().playerId)
    }

    fun getMutablePlayerData(id: Int): MutablePlayerData {
        return playerDataMap.getOrPut(id) {
            DataSerializer.copy(universeData3DAtPlayer.get(id))
        }
    }

    fun getCurrentMutablePlayerData(): MutablePlayerData {
        return getMutablePlayerData(universeData3DAtPlayer.getCurrentPlayerData().playerId)
    }

    /**
     * Reset the current player data and do all the self execute
     */
    private fun resetCurrentPlayerDataSelfExecute() {
        playerDataMap[universeData3DAtPlayer.getCurrentPlayerData().playerId] =
            DataSerializer.copy(universeData3DAtPlayer.getCurrentPlayerData())

        commandList.forEach {
            it.checkAndSelfExecuteBeforeSend(
                getCurrentMutablePlayerData(),
                universeData3DAtPlayer.universeSettings
            )
        }
    }

    /**
     * Execute all commands to current player data
     */
    private fun executeOnCurrentPlayerData() {
        val playerData: MutablePlayerData = getCurrentMutablePlayerData()

        commandList.filter {
            it.toId == playerData.playerId
        }.forEach {
            it.checkAndExecute(
                playerData,
                universeData3DAtPlayer.universeSettings
            )
        }
    }

    fun resetPlayerData(playerId: Int) {
        if (playerId == universeData3DAtPlayer.getCurrentPlayerData().playerId) {
            resetCurrentPlayerDataSelfExecute()
            executeOnCurrentPlayerData()
        } else {
            playerDataMap[playerId] = DataSerializer.copy(universeData3DAtPlayer.get(playerId))

            val playerData: MutablePlayerData = getMutablePlayerData(playerId)

            commandList.filter {
                it.toId == playerId
            }.forEach {
                it.checkAndExecute(
                    playerData,
                    universeData3DAtPlayer.universeSettings
                )
            }
        }
    }

    private fun addSingleCommand(command: Command) {
        val targetPlayerData: MutablePlayerData = getMutablePlayerData(command.toId)

        if (targetPlayerData.playerId == -1) {
            logger.error("Add command error: Player id -1")
        } else {
            resetCurrentPlayerDataSelfExecute()

            if (command.checkAndSelfExecuteBeforeSend(
                    getCurrentMutablePlayerData(),
                    universeData3DAtPlayer.universeSettings
                ).canSend
            ) {
                executeOnCurrentPlayerData()
                command.checkAndExecute(targetPlayerData, universeData3DAtPlayer.universeSettings)
                commandList.add(command)
            } else {
                executeOnCurrentPlayerData()
                logger.error("Cannot add command: $command")
            }
        }
    }

    fun addCommand(command: Command) {
        addSingleCommand(command)
        onCommandListChange()
    }

    fun addAllCommand(commandList: List<Command>) {
        commandList.forEach {
            addSingleCommand(it)
        }
        onCommandListChange()
    }


    fun removeCommand(command: Command) {
        commandList.remove(command)
        resetPlayerData(command.toId)
        resetPlayerData(command.fromId)
        onCommandListChange()
    }

    fun removeAllCommand(commandListToRemove: List<Command>) {
        commandList.removeAll(commandListToRemove)
        val playerIdToReset: Set<Int> = (commandListToRemove.map {
            it.toId
        } + universeData3DAtPlayer.getCurrentPlayerData().playerId).toSet()

        playerIdToReset.forEach {
            resetPlayerData(it)
        }
        onCommandListChange()
    }

    fun clearCommand() {
        commandList.clear()
        playerDataMap.clear()
        onCommandListChange()
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}