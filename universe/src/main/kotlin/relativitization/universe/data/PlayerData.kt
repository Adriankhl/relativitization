package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.ai.AIData
import relativitization.universe.data.ai.MutableAIData
import relativitization.universe.data.diplomacy.DiplomacyData
import relativitization.universe.data.diplomacy.MutableDiplomacyData
import relativitization.universe.data.economy.EconomyData
import relativitization.universe.data.economy.MutableEconomyData
import relativitization.universe.data.events.EventData
import relativitization.universe.data.events.MutableEventData
import relativitization.universe.data.modifier.ModifierData
import relativitization.universe.data.modifier.MutableModifierData
import relativitization.universe.data.physics.*
import relativitization.universe.data.politics.MutablePoliticsData
import relativitization.universe.data.politics.PoliticsData
import relativitization.universe.data.popsystem.MutablePopSystemicData
import relativitization.universe.data.popsystem.PopSystemicData
import relativitization.universe.data.science.MutablePlayerScienceData
import relativitization.universe.data.science.PlayerScienceData
import relativitization.universe.maths.grid.Grids.double4DToGroupId
import relativitization.universe.maths.grid.Grids.groupIdToCenterDouble3D
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Data of the basic unit (player)
 *
 * @property id playerId
 * @property playerType ai / human / none (e.g. resource)
 * @property int4D 4D coordinate of the player
 * @property int4DHistory historical coordinate of the player
 * @property double4D the accurate 4D coordinate of the player in floating point
 * @property groupId the id of the group where the player can instantly communicate with
 * @property velocity the velocity of the player
 * @property playerInternalData the internal data of this player
 * @property newPlayerList new players internal data generated by this player, temporarily stored
 */
@Serializable
data class PlayerData(
    val id: Int,
    val name: String = "Default Player",
    val playerType: PlayerType = PlayerType.AI,
    val int4D: Int4D = Int4D(0, 0, 0, 0),
    val int4DHistory: List<Int4D> = listOf(),
    val double4D: Double4D = int4D.toDouble4D(),
    val groupId: Int = double4DToGroupId(double4D, 0.01),
    val velocity: Velocity = Velocity(0.0, 0.0, 0.0),
    val playerInternalData: PlayerInternalData = PlayerInternalData(
        directLeaderId = id, leaderIdList = listOf(id)
    ),
    val newPlayerList: List<PlayerInternalData> = listOf()
) {
    /**
     * @param toId whether this id is the player or one the subordinates of the player
     */
    fun isSubOrdinateOrSelf(toId: Int): Boolean {
        return (toId == id) || playerInternalData.subordinateIdList.contains(toId)
    }

    /**
     * @param toId whether this id is the player or one of the leaders of the player
     */
    fun isLeaderOrSelf(toId: Int): Boolean {
        return (toId == id) || playerInternalData.leaderIdList.contains(toId)
    }

    fun isValid(currentTime: Int): Boolean {
        val isTValid: Boolean = currentTime == int4D.t
        val isRestMassValid: Boolean = (playerInternalData.physicsData.coreRestMass > 0.0) &&
                (playerInternalData.physicsData.fuelRestMass >= 0.0)

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
}

@Serializable
data class MutablePlayerData(
    val id: Int,
    var name: String = "Default Player",
    var playerType: PlayerType = PlayerType.AI,
    var int4D: MutableInt4D = MutableInt4D(0, 0, 0, 0),
    val int4DHistory: MutableList<Int4D> = mutableListOf(),
    var double4D: MutableDouble4D = int4D.toMutableDouble4D(),
    var groupId: Int = double4DToGroupId(double4D, 0.01),
    var velocity: MutableVelocity = MutableVelocity(0.0, 0.0, 0.0),
    var playerInternalData: MutablePlayerInternalData = MutablePlayerInternalData(
        directLeaderId = id, leaderIdList = mutableListOf(id)
    ),
    val newPlayerList: MutableList<MutablePlayerInternalData> = mutableListOf()
) {
    /**
     * Synchronize different data component to ensure consistency
     */
    fun syncDataComponent() {
        if (playerInternalData.physicsData.coreRestMass != playerInternalData.popSystemicData.totalCoreRestMass()) {
            logger.debug("Sync data component, change core mass")
            playerInternalData.physicsData.coreRestMass = playerInternalData.popSystemicData.totalCoreRestMass()
        }

        if (playerInternalData.physicsData.fuelRestMass != playerInternalData.popSystemicData.totalFuelRestMass()) {
            logger.debug("Sync data component, change fuel mass")
            playerInternalData.physicsData.fuelRestMass = playerInternalData.popSystemicData.totalFuelRestMass()
        }

        if (playerInternalData.physicsData.maxDeltaFuelRestMass != playerInternalData.popSystemicData.totalMaxDeltaFuelRestMass()) {
            logger.debug("Sync data component, change max delta fuel rest mass")
            playerInternalData.physicsData.maxDeltaFuelRestMass = playerInternalData.popSystemicData.totalMaxDeltaFuelRestMass()
        }
    }

    /**
     * @param toId whether this id is the player or one the subordinates of the player
     */
    fun isSubOrdinateOrSelf(toId: Int): Boolean {
        return (toId == id) || playerInternalData.subordinateIdList.contains(toId)
    }

    /**
     * @param toId whether this id is the player or one of the leaders of the player
     */
    fun isLeaderOrSelf(toId: Int): Boolean {
        return (toId == id) || playerInternalData.leaderIdList.contains(toId)
    }

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
 * @property directLeaderId player id of the direct leader, equals -1 if no leader
 * @property directSubordinateIdList direct subordinates
 * @property leaderIdList list of player ids of leader, leader of leader, etc., from -1 to direct leader
 * @property subordinateIdList list of player ids of the subordinates of this player
 * @property isAlive whether the player is alive or dead
 * @property aiData data for ai computation, e.g. name, cool down
 * @property eventDataList list of current event on this player
 * @property physicsData physics-related data
 * @property popSystemicData population system related data
 * @property playerScienceData research related data
 * @property politicsData political related data
 * @property diplomacyData diplomatic relation data
 * @property economyData economy related data
 * @property modifierData player modifier, e.g. disable certain action for a time limit
 */
@Serializable
data class PlayerInternalData(
    val directLeaderId: Int,
    val leaderIdList: List<Int>,
    val directSubordinateIdList: List<Int> = listOf(),
    val subordinateIdList: List<Int> = listOf(),
    val isAlive: Boolean = true,
    val aiData: AIData = AIData(),
    val eventDataList: List<EventData> = listOf(),
    val physicsData: PhysicsData = PhysicsData(),
    val popSystemicData: PopSystemicData = PopSystemicData(),
    val playerScienceData: PlayerScienceData = PlayerScienceData(),
    val politicsData: PoliticsData = PoliticsData(),
    val diplomacyData: DiplomacyData = DiplomacyData(),
    val economyData: EconomyData = EconomyData(),
    val modifierData: ModifierData = ModifierData(),
)

@Serializable
data class MutablePlayerInternalData(
    var directLeaderId: Int,
    var leaderIdList: MutableList<Int>,
    var directSubordinateIdList: MutableList<Int> = mutableListOf(),
    var subordinateIdList: MutableList<Int> = mutableListOf(),
    var isAlive: Boolean = true,
    var aiData: MutableAIData = MutableAIData(),
    var eventDataList: MutableList<MutableEventData> = mutableListOf(),
    var physicsData: MutablePhysicsData = MutablePhysicsData(),
    var popSystemicData: MutablePopSystemicData = MutablePopSystemicData(),
    var playerScienceData: MutablePlayerScienceData = MutablePlayerScienceData(),
    var politicsData: MutablePoliticsData = MutablePoliticsData(),
    var diplomacyData: MutableDiplomacyData = MutableDiplomacyData(),
    var economyData: MutableEconomyData = MutableEconomyData(),
    var modifierData: MutableModifierData = MutableModifierData(),
) {


    /**
     * Change direct leader id without removing the old direct leader as one of the leader
     */
    fun changeDirectLeaderId(id: Int) {
        directLeaderId = id
        leaderIdList.add(id)
    }

    /**
     * Add subordinate to this player
     */
    fun addDirectSubordinateId(id: Int) {
        directSubordinateIdList.add(id)
        subordinateIdList.add(id)
    }
}