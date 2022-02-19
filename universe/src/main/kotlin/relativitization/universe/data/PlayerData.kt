package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.ai.DefaultAI
import relativitization.universe.ai.name
import relativitization.universe.data.components.*
import relativitization.universe.data.components.MutablePopSystemData
import relativitization.universe.data.components.PopSystemData
import relativitization.universe.data.components.defaults.physics.*
import relativitization.universe.data.events.EventData
import relativitization.universe.data.events.MutableEventData
import relativitization.universe.maths.collection.ListFind
import relativitization.universe.maths.grid.Grids.double4DToGroupId
import relativitization.universe.maths.grid.Grids.groupIdToCenterDouble3D
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.full.createInstance

/**
 * Data of the basic unit (player)
 *
 * @property playerId playerId
 * @property playerType ai / human / none (e.g. resource)
 * @property int4D 4D coordinate of the player
 * @property int4DHistory historical coordinate of the player
 * @property double4D the accurate 4D coordinate of the player in floating point
 * @property dilatedTimeResidue depends on the time dilation, should be between 0.0 and 1.0, for
 * computation of isDilationTUrn
 * @property isDilationActionTurn whether this turn should execute time dilated actions
 * @property groupId the id of the group where the player can instantly communicate with
 * @property velocity the velocity of the player
 * @property playerInternalData the internal data of this player
 * @property newPlayerList new players internal data generated by this player, temporarily stored
 */
@Serializable
data class PlayerData(
    val playerId: Int,
    val name: String = "Default Player",
    val playerType: PlayerType = PlayerType.AI,
    val int4D: Int4D = Int4D(0, 0, 0, 0),
    val int4DHistory: List<Int4D> = listOf(),
    val double4D: Double4D = int4D.toDouble4D(),
    val dilatedTimeResidue: Double = 0.0,
    val isDilationActionTurn: Boolean = true,
    val groupId: Int = double4DToGroupId(double4D, 0.01),
    val velocity: Velocity = Velocity(0.0, 0.0, 0.0),
    val playerInternalData: PlayerInternalData = PlayerInternalData(directLeaderId = playerId),
    val newPlayerList: List<PlayerInternalData> = listOf()
) {
    /**
     * @param otherPlayerId whether this id is the player or one of the subordinates of the player
     */
    fun isSubOrdinateOrSelf(otherPlayerId: Int): Boolean {
        return (otherPlayerId == playerId) || playerInternalData.subordinateIdSet.contains(
            otherPlayerId
        )
    }

    /**
     * @param otherPlayerId whether this id is one of the direct subordinates of the player
     */
    fun isDirectSubOrdinate(otherPlayerId: Int): Boolean {
        return playerInternalData.directSubordinateIdSet.contains(
            otherPlayerId
        )
    }

    /**
     * @param otherPlayerId whether this id is one of the subordinates of the player
     */
    fun isSubOrdinate(otherPlayerId: Int): Boolean {
        return playerInternalData.subordinateIdSet.contains(
            otherPlayerId
        )
    }

    /**
     * @param otherPlayerId whether this id is the player or one of the leaders of the player
     */
    fun isLeaderOrSelf(otherPlayerId: Int): Boolean {
        return (otherPlayerId == playerId) || playerInternalData.leaderIdList.contains(otherPlayerId)
    }

    /**
     * @param otherPlayerId whether this id is one of the leaders of the player
     */
    fun isLeader(otherPlayerId: Int): Boolean {
        return playerInternalData.leaderIdList.contains(otherPlayerId)
    }

    /**
     * The top leader id
     */
    fun topLeaderId(): Int {
        return if (playerInternalData.leaderIdList.isEmpty()) {
            playerId
        } else {
            playerInternalData.leaderIdList.first()
        }
    }

    /**
     * Is player the top leader
     */
    fun isTopLeader(): Boolean {
        return playerInternalData.leaderIdList.isEmpty()
    }

    fun isValid(currentTime: Int): Boolean {
        val isTValid: Boolean = currentTime == int4D.t
        if (!isTValid) {
            logger.error("Invalid t ${int4D.t}, should be $currentTime")
        }

        val physicsData: PhysicsData = playerInternalData.physicsData()

        val isRestMassValid: Boolean = (physicsData.coreRestMass > 0.0) && (physicsData.fuelRestMassData.total() >= 0.0)
        if (!isRestMassValid) {
            logger.error(
                "Invalid rest mass: core ${physicsData.coreRestMass}, " +
                        "fuel ${physicsData.fuelRestMassData.total()}"
            )
        }

        return isTValid && isRestMassValid
    }

    /**
     * The center position of the group
     */
    fun groupCenterDouble3D(edgeLength: Double): Double3D {
        return int4D.toInt3D() + groupIdToCenterDouble3D(
            double4DToGroupId(double4D, edgeLength),
            edgeLength
        )
    }

    /**
     * Get leader and self id list
     */
    fun getLeaderAndSelfIdList(): List<Int> = playerInternalData.leaderIdList + playerId

    /**
     * Get subordinate and self id set
     */
    fun getSubordinateAndSelfIdSet(): Set<Int> = playerInternalData.subordinateIdSet + playerId

    companion object {
        val logger = RelativitizationLogManager.getLogger()
    }
}

@Serializable
data class MutablePlayerData(
    val playerId: Int,
    var name: String = "Default Player",
    var playerType: PlayerType = PlayerType.AI,
    var int4D: MutableInt4D = MutableInt4D(0, 0, 0, 0),
    val int4DHistory: MutableList<Int4D> = mutableListOf(),
    var double4D: MutableDouble4D = int4D.toMutableDouble4D(),
    var dilatedTimeResidue: Double = 0.0,
    var isDilationActionTurn: Boolean = true,
    var groupId: Int = double4DToGroupId(double4D, 0.01),
    var velocity: MutableVelocity = MutableVelocity(0.0, 0.0, 0.0),
    var playerInternalData: MutablePlayerInternalData = MutablePlayerInternalData(directLeaderId = playerId),
    val newPlayerList: MutableList<MutablePlayerInternalData> = mutableListOf()
) {
    /**
     * @param otherPlayerId whether this id is the player or one the subordinates of the player
     */
    fun isSubOrdinateOrSelf(otherPlayerId: Int): Boolean {
        return (otherPlayerId == playerId) || playerInternalData.subordinateIdSet.contains(
            otherPlayerId
        )
    }


    /**
     * @param otherPlayerId whether this id is one of the direct subordinates of the player
     */
    fun isDirectSubOrdinate(otherPlayerId: Int): Boolean {
        return playerInternalData.directSubordinateIdSet.contains(
            otherPlayerId
        )
    }

    /**
     * @param otherPlayerId whether this id is one of the subordinates of the player
     */
    fun isSubOrdinate(otherPlayerId: Int): Boolean {
        return playerInternalData.subordinateIdSet.contains(
            otherPlayerId
        )
    }

    /**
     * @param otherPlayerId whether this id is the player or one of the leaders of the player
     */
    fun isLeaderOrSelf(otherPlayerId: Int): Boolean {
        return (otherPlayerId == playerId) || playerInternalData.leaderIdList.contains(otherPlayerId)
    }


    /**
     * @param otherPlayerId whether this id is one of the leaders of the player
     */
    fun isLeader(otherPlayerId: Int): Boolean {
        return playerInternalData.leaderIdList.contains(otherPlayerId)
    }


    /**
     * The top leader id
     */
    fun topLeaderId(): Int {
        return if (playerInternalData.leaderIdList.isEmpty()) {
            playerId
        } else {
            playerInternalData.leaderIdList.first()
        }
    }

    /**
     * Is player the top leader
     */
    fun isTopLeader(): Boolean {
        return playerInternalData.leaderIdList.isEmpty()
    }


    /**
     * Change direct leader id and add all leaders of direct leader
     *
     * @param newLeaderList new leader list including the new direct leader as the last item,
     * should not contain id of this player
     */
    fun changeDirectLeader(newLeaderList: List<Int>) {
        if (newLeaderList.contains(playerId)) {
            logger.error("Leader id list contains this player id ($playerId). ")
        } else {
            if (newLeaderList.isNotEmpty()) {
                // Remove subordinate and direct subordinate if they appear on the leader list
                newLeaderList.forEach {
                    removeSubordinateId(it)
                }

                playerInternalData.directLeaderId = newLeaderList.last()
                playerInternalData.leaderIdList.clear()
                playerInternalData.leaderIdList.addAll(newLeaderList)
            } else {
                playerInternalData.directLeaderId = playerId
                playerInternalData.leaderIdList.clear()
            }
        }
    }

    /**
     * Add direct subordinate to this player
     */
    fun addDirectSubordinateId(subordinateId: Int) {
        if (!isLeaderOrSelf(subordinateId)) {
            playerInternalData.directSubordinateIdSet.add(subordinateId)
            playerInternalData.subordinateIdSet.add(subordinateId)
        } else {
            logger.error("Player $playerId try to add leader or self $subordinateId as direct subordinate")
        }
    }

    /**
     * Add subordinate to this player
     */
    fun addSubordinateId(subordinateId: Int) {
        if (!isLeaderOrSelf(subordinateId)) {
            playerInternalData.subordinateIdSet.add(subordinateId)
        } else {
            logger.error("Player $playerId try to add leader or self $subordinateId as subordinate")
        }
    }

    /**
     * Remove subordinate
     */
    fun removeSubordinateId(subordinateId: Int) {
        playerInternalData.subordinateIdSet.remove(subordinateId)
        playerInternalData.directSubordinateIdSet.remove(subordinateId)
    }

    /**
     * Get leader and self id list
     */
    fun getLeaderAndSelfIdList(): List<Int> = playerInternalData.leaderIdList + playerId

    /**
     * Get subordinate and self id set
     */
    fun getSubordinateAndSelfIdSet(): Set<Int> = playerInternalData.subordinateIdSet + playerId

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Type of player: AI, human, none (e.g. resource)
 */
enum class PlayerType {
    AI,
    HUMAN,
    NONE,
}

/**
 * Player internal data
 *
 * @property directLeaderId the direct leader
 * @property leaderIdList list of player ids of leader, leader of leader, etc., from top leader to direct leader
 * @property directLeaderId player id of the direct leader, equals -1 if no leader
 * @property directSubordinateIdSet direct subordinates
 * @property subordinateIdSet set of player ids of the subordinates of this player
 * @property isAlive whether the player is alive or dead
 * @property aiName the name of the AI
 * @property eventDataMap list of current event on this player
 * @property playerDataComponentMap the map to store addition data component
 */
@Serializable
data class PlayerInternalData(
    val directLeaderId: Int,
    val leaderIdList: List<Int> = listOf(),
    val directSubordinateIdSet: Set<Int> = setOf(),
    val subordinateIdSet: Set<Int> = setOf(),
    val isAlive: Boolean = true,
    val aiName: String = DefaultAI.name(),
    val eventDataMap: Map<Int, EventData> = mapOf(),
    val playerDataComponentMap: PlayerDataComponentMap = PlayerDataComponentMap(
        DefaultPlayerDataComponent::class.sealedSubclasses.map { it.createInstance() }
    ),
) {
    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

@Serializable
data class MutablePlayerInternalData(
    var directLeaderId: Int,
    var leaderIdList: MutableList<Int> = mutableListOf(),
    var directSubordinateIdSet: MutableSet<Int> = mutableSetOf(),
    var subordinateIdSet: MutableSet<Int> = mutableSetOf(),
    var isAlive: Boolean = true,
    var aiName: String = DefaultAI.name(),
    var eventDataMap: MutableMap<Int, MutableEventData> = mutableMapOf(),
    var playerDataComponentMap: MutablePlayerDataComponentMap = MutablePlayerDataComponentMap(
        MutableDefaultPlayerDataComponent::class.sealedSubclasses.map { it.createInstance() }
    ),
) {
    /**
     * Add an event to event map
     */
    fun addEventData(eventData: MutableEventData) {
        val newKey: Int = ListFind.minMissing(eventDataMap.keys.toList(), 0)
        eventDataMap[newKey] = eventData
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}