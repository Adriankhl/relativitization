package relativitization.universe.core.data

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.maths.physics.Int3D
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.core.maths.physics.Intervals
import relativitization.universe.core.utils.I18NString
import relativitization.universe.core.utils.RelativitizationLogManager
import kotlin.math.ceil

@Serializable
data class UniverseData3DAtGrid(
    val center: Int4D,
    val centerPlayerDataList: List<PlayerData>,
    val playerDataMap: Map<Int, PlayerData>,
    val playerId3DMap: List<List<List<Map<Int, List<Int>>>>>,
    val universeSettings: UniverseSettings,
) {
    /**
     * Get a map from player id to a view of the universe centered at that player
     */
    fun idToUniverseData3DAtPlayer(): Map<Int, UniverseData3DAtPlayer> {
        // group by group id
        val playerGroupMap: Map<Int, List<PlayerData>> = centerPlayerDataList.groupBy {
            it.groupId
        }.filterValues { playerDataList ->
            // Skip the process if none of the player in the group are in the recent time
            // this can happen if all player data are afterimage
            playerDataList.any {
                it.int4D.t == center.t
            }
        }

        return playerGroupMap.map { (groupId, group) ->
            val groupPlayerIdSet: Set<Int> = group.map { it.playerId }.toSet()

            // player data in the same group
            val sameGroupPlayerDataMap: Map<Int, PlayerData> =
                groupPlayerIdSet.associateWith { id ->
                    // multiple player data of the same player can exist in the
                    // same group due to after image take the latest one
                    val playerData: PlayerData = group.filter { it.playerId == id }.maxByOrNull {
                        it.int4D.t
                    }!!
                    playerData
                }

            // prioritize these data than the one from the original playerDataMap
            val prioritizedDataMap: Map<Int, PlayerData> = sameGroupPlayerDataMap.filter {
                val hasPlayerInHistory: Boolean = playerDataMap.containsKey(it.key)

                // If the playerDataMap has this data, take the recent one
                if (hasPlayerInHistory) {
                    playerDataMap.getValue(it.key).int4D.t < it.value.int4D.t
                } else {
                    true
                }
            }

            // partition player data map by the part to keep and to remove
            val toKeepPlayerDataMap: Map<Int, PlayerData> = playerDataMap - prioritizedDataMap.keys
            val toRemovePlayerDataMap: Map<Int, PlayerData> = prioritizedDataMap.keys.filter {
                playerDataMap.containsKey(it)
            }.associateWith {
                playerDataMap.getValue(it)
            }

            val groupPlayerDataMap: Map<Int, PlayerData> = prioritizedDataMap + toKeepPlayerDataMap

            // a map to store the location of player data to be removed
            val toRemoveLocationIdMap: Map<Int4D, Map<Int, List<Int>>> =
                toRemovePlayerDataMap.values.groupBy {
                    it.int4D
                }.mapValues { (_, data) ->
                    data.groupBy {
                        it.groupId
                    }.mapValues { (_, dataList) ->
                        dataList.map { it.playerId }
                    }
                }

            // An optimized way to compute the 3d array of player id
            // Remove the date that has been removed from playerDataMap, then add the prioritized data
            val groupPlayerId3DMap: List<List<List<Map<Int, List<Int>>>>> =
                playerId3DMap.mapIndexed { xIndex, yList ->
                    val atCenterX: Boolean = xIndex == center.x
                    val toRemoveSameXMap: Map<Int4D, Map<Int, List<Int>>> =
                        toRemoveLocationIdMap.filterKeys {
                            it.x == xIndex
                        }
                    if (atCenterX || toRemoveSameXMap.isNotEmpty()) {
                        yList.mapIndexed { yIndex, zList ->
                            val atCenterY: Boolean = yIndex == center.y
                            val toRemoveSameXYMap: Map<Int4D, Map<Int, List<Int>>> =
                                toRemoveSameXMap.filterKeys {
                                    it.y == yIndex
                                }
                            if (atCenterY || toRemoveSameXYMap.isNotEmpty()) {
                                zList.mapIndexed { zIndex, playerDataAtGridMap ->
                                    val atCenterZ: Boolean = zIndex == center.z
                                    val toRemoveDataSameXYZMap: Map<Int4D, Map<Int, List<Int>>> =
                                        toRemoveSameXYMap
                                            .filterKeys {
                                                it.z == zIndex
                                            }
                                    if (atCenterZ || toRemoveDataSameXYZMap.isNotEmpty()) {
                                        val atCenter: Boolean = atCenterX && atCenterY && atCenterZ

                                        // Add the center group id as key if not exist
                                        val newPlayerDataAtGridMap: Map<Int, List<Int>> =
                                            if (atCenter) {
                                                if (playerDataAtGridMap.containsKey(groupId)) {
                                                    playerDataAtGridMap
                                                } else {
                                                    playerDataAtGridMap + mapOf(groupId to listOf())
                                                }
                                            } else {
                                                playerDataAtGridMap
                                            }

                                        // remove the id from toRemovePlayerDataMap
                                        // add prioritized id if this is the center group
                                        newPlayerDataAtGridMap.mapValues { (gridGroupId, idList) ->
                                            // The remaining should be at the correct location
                                            val toRemoveSameGroupList: List<Int> =
                                                toRemoveDataSameXYZMap
                                                    .values.flatMap {
                                                        it.getOrDefault(gridGroupId, listOf())
                                                    }
                                            val atCenterGroup: Boolean =
                                                atCenter && (gridGroupId == groupId)

                                            val afterRemoveList: List<Int> =
                                                if (toRemoveSameGroupList.isNotEmpty()) {
                                                    idList - toRemoveSameGroupList.toSet()
                                                } else {
                                                    idList
                                                }

                                            if (atCenterGroup) {
                                                afterRemoveList + prioritizedDataMap.keys
                                            } else {
                                                afterRemoveList
                                            }
                                        }.filterValues {
                                            // Some groups could be empty after removing player id, filter those out
                                            it.isNotEmpty()
                                        }
                                    } else {
                                        playerDataAtGridMap
                                    }
                                }
                            } else {
                                zList
                            }
                        }
                    } else {
                        yList
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
     * Get list of int3D at the surface of a greater cube, with the current center
     *
     * @param halfEdgeLLength half of the edge length of the greater cube + 0.5
     */
    fun getInt3DAtCubeSurface(halfEdgeLLength: Int): List<Int3D> {
        return center.toInt3D().getInt3DSurfaceList(
            halfEdgeLength = halfEdgeLLength,
            minX = 0,
            maxX = universeSettings.xDim - 1,
            minY = 0,
            maxY = universeSettings.yDim - 1,
            minZ = 0,
            maxZ = universeSettings.zDim - 1
        )
    }

    /**
     * Get player data by id
     */
    fun get(id: Int): PlayerData {
        return playerDataMap.getOrElse(id) {
            logger.error("id $id not in playerDataMap")
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
     * Get player within a cube centered at the current player, get same group player if
     * halfEdgeLength = 0
     *
     * @param halfEdgeLength half of the edge length of the cube + 0.5
     */
    fun getPlayerInCube(halfEdgeLength: Int): List<PlayerData> {
        return when {
            halfEdgeLength < 0 -> {
                logger.error("halfEdgeLength <= 0")
                listOf()
            }
            halfEdgeLength == 0 -> {
                val currentPlayer: PlayerData = getCurrentPlayerData()
                get(currentPlayer.int4D.toInt3D()).getValue(currentPlayer.groupId).filter {
                    // Filter out afterimage player data, which have different t coordinate
                    (it.int4D.t == currentPlayer.int4D.t) && (it.playerId != currentPlayer.playerId)
                }
            }
            else -> {
                val currentPlayer: PlayerData = getCurrentPlayerData()
                val int3D: Int3D = currentPlayer.int4D.toInt3D()

                int3D.getInt3DCubeList(
                    halfEdgeLength,
                    minX = 0,
                    maxX = universeSettings.xDim - 1,
                    minY = 0,
                    maxY = universeSettings.yDim - 1,
                    minZ = 0,
                    maxZ = universeSettings.zDim - 1
                ).flatMap {
                    get(it).values.flatten()
                }
            }
        }
    }

    /**
     * Get player within a cube centered at the current player, get same group player if
     * halfEdgeLength = 0, excluding self
     *
     * @param halfEdgeLength half of the edge length of the cube + 0.5
     *
     */
    fun getNeighbourInCube(halfEdgeLength: Int): List<PlayerData> {
        val currentPlayer: PlayerData = getCurrentPlayerData()
        return getPlayerInCube(halfEdgeLength).filter {
            it.playerId != currentPlayer.playerId
        }
    }

    /**
     * Get player within a sphere centered at the current player
     *
     * @param radius the radius of the sphere
     */
    fun getPlayerInSphere(radius: Double): List<PlayerData> {
        val currentPlayer: PlayerData = getCurrentPlayerData()
        return getPlayerInCube(ceil(radius + 0.5).toInt()).filter {
            Intervals.distance(it.double4D, currentPlayer.double4D) <= radius
        }
    }

    /**
     * Get neighbour within a sphere centered at the current player, excluding self
     *
     * @param radius the radius of the sphere
     */
    fun getNeighbourInSphere(radius: Double): List<PlayerData> {
        val currentPlayer: PlayerData = getCurrentPlayerData()
        return getPlayerInSphere(radius).filter {
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
 * For human or AI to decide the command list
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

    fun resetPlayerData(playerId: Int) {
        if (playerId == universeData3DAtPlayer.getCurrentPlayerData().playerId) {
            playerDataMap[playerId] = DataSerializer.copy(universeData3DAtPlayer.get(playerId))
            val playerData: MutablePlayerData = getMutablePlayerData(playerId)

            commandList.forEach {
                it.checkAndSelfExecuteBeforeSend(
                    playerData,
                    universeData3DAtPlayer.universeSettings
                )

                if (it.toId == playerId) {
                    it.checkAndExecute(
                        playerData = playerData,
                        fromId = playerData.playerId,
                        fromInt4D = playerData.int4D.toInt4D(),
                        universeSettings = universeData3DAtPlayer.universeSettings
                    )
                }
            }
        } else {
            playerDataMap[playerId] = DataSerializer.copy(universeData3DAtPlayer.get(playerId))

            val currentPlayerData: PlayerData = getCurrentPlayerData()
            val playerData: MutablePlayerData = getMutablePlayerData(playerId)

            commandList.filter {
                it.toId == playerId
            }.forEach {
                it.checkAndExecute(
                    playerData,
                    currentPlayerData.playerId,
                    currentPlayerData.int4D,
                    universeData3DAtPlayer.universeSettings
                )
            }
        }
    }

    private fun addSingleCommand(command: Command): CommandErrorMessage {
        val targetPlayerData: MutablePlayerData = getMutablePlayerData(command.toId)

        if (targetPlayerData.playerId == -1) {
            logger.error("Add command error: Player id -1")
        }

        val currentPlayerData: MutablePlayerData = getCurrentMutablePlayerData()

        val sendCommandMessage: CommandErrorMessage = command.checkAndSelfExecuteBeforeSend(
            currentPlayerData,
            universeData3DAtPlayer.universeSettings
        )

        return if (sendCommandMessage.success) {
            val executeMessage: CommandErrorMessage = command.checkAndExecute(
                targetPlayerData,
                currentPlayerData.playerId,
                currentPlayerData.int4D.toInt4D(),
                universeData3DAtPlayer.universeSettings
            )
            commandList.add(command)
            executeMessage
        } else {
            logger.error(
                "Cannot add command ${command.name()}: ${sendCommandMessage.errorMessage.toNormalString()}"
            )
            CommandErrorMessage(
                false,
                I18NString("Cannot send this command. Self-execute check failed.  ")
            )
        }
    }

    fun addCommand(command: Command): CommandErrorMessage {
        val commandErrorMessage: CommandErrorMessage = addSingleCommand(command)
        onCommandListChange()
        return commandErrorMessage
    }

    fun addAllCommand(commandList: List<Command>): List<CommandErrorMessage> {
        val commandErrorMessageList: List<CommandErrorMessage> = commandList.map {
            addSingleCommand(it)
        }
        onCommandListChange()

        return commandErrorMessageList
    }

    fun removeCommand(command: Command) {
        commandList.remove(command)
        resetPlayerData(command.toId)
        resetPlayerData(getCurrentMutablePlayerData().playerId)
        onCommandListChange()
    }

    fun removeAllCommand(commandListToRemove: List<Command>) {
        commandList.removeAll(commandListToRemove.toSet())
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