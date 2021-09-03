package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.*
import relativitization.universe.data.component.MutablePlayerScienceData
import relativitization.universe.data.component.MutablePoliticsData
import relativitization.universe.data.component.MutablePopSystemData
import relativitization.universe.data.component.PlayerScienceData
import relativitization.universe.data.component.PoliticsData
import relativitization.universe.data.component.PopSystemData
import relativitization.universe.data.component.physics.*
import relativitization.universe.data.events.EventData
import relativitization.universe.data.events.MutableEventData
import relativitization.universe.maths.collection.ListFind
import relativitization.universe.maths.grid.Grids.double4DToGroupId
import relativitization.universe.maths.grid.Grids.groupIdToCenterDouble3D
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Data of the basic unit (player)
 *
 * @property playerId playerId
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
    val playerId: Int,
    val name: String = "Default Player",
    val playerType: PlayerType = PlayerType.AI,
    val int4D: Int4D = Int4D(0, 0, 0, 0),
    val int4DHistory: List<Int4D> = listOf(),
    val double4D: Double4D = int4D.toDouble4D(),
    val groupId: Int = double4DToGroupId(double4D, 0.01),
    val velocity: Velocity = Velocity(0.0, 0.0, 0.0),
    val playerInternalData: PlayerInternalData = PlayerInternalData(
        directLeaderId = playerId, leaderIdList = listOf(playerId)
    ),
    val newPlayerList: List<PlayerInternalData> = listOf()
) {
    /**
     * @param toId whether this id is the player or one the subordinates of the player
     */
    fun isSubOrdinateOrSelf(toId: Int): Boolean {
        return (toId == playerId) || playerInternalData.subordinateIdList.contains(toId)
    }

    /**
     * @param toId whether this id is the player or one of the leaders of the player
     */
    fun isLeaderOrSelf(toId: Int): Boolean {
        return (toId == playerId) || playerInternalData.leaderIdList.contains(toId)
    }

    fun isValid(currentTime: Int): Boolean {
        val isTValid: Boolean = currentTime == int4D.t
        val isRestMassValid: Boolean = (playerInternalData.physicsData().coreRestMass > 0.0) &&
                (playerInternalData.physicsData().fuelRestMass >= 0.0)

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
    val playerId: Int,
    var name: String = "Default Player",
    var playerType: PlayerType = PlayerType.AI,
    var int4D: MutableInt4D = MutableInt4D(0, 0, 0, 0),
    val int4DHistory: MutableList<Int4D> = mutableListOf(),
    var double4D: MutableDouble4D = int4D.toMutableDouble4D(),
    var groupId: Int = double4DToGroupId(double4D, 0.01),
    var velocity: MutableVelocity = MutableVelocity(0.0, 0.0, 0.0),
    var playerInternalData: MutablePlayerInternalData = MutablePlayerInternalData(
        directLeaderId = playerId, leaderIdList = mutableListOf(playerId)
    ),
    val newPlayerList: MutableList<MutablePlayerInternalData> = mutableListOf()
) {
    /**
     * Synchronize different data component to ensure consistency
     */
    fun syncDataComponent() {
        if (playerInternalData.physicsData().coreRestMass != playerInternalData.popSystemData()
                .totalCoreRestMass()
        ) {
            logger.debug("Sync data component, change core mass")
            playerInternalData.physicsData().coreRestMass =
                playerInternalData.popSystemData().totalCoreRestMass()
        }

        if (playerInternalData.physicsData().fuelRestMass != playerInternalData.popSystemData()
                .totalFuelRestMass()
        ) {
            logger.debug("Sync data component, change fuel mass")
            playerInternalData.physicsData().fuelRestMass =
                playerInternalData.popSystemData().totalFuelRestMass()
        }

        if (playerInternalData.physicsData().maxDeltaFuelRestMass != playerInternalData.popSystemData()
                .totalMaxDeltaFuelRestMass()
        ) {
            logger.debug("Sync data component, change max delta fuel rest mass")
            playerInternalData.physicsData().maxDeltaFuelRestMass =
                playerInternalData.popSystemData().totalMaxDeltaFuelRestMass()
        }

        playerInternalData.popSystemData().syncCombatData()
    }

    /**
     * @param toId whether this id is the player or one the subordinates of the player
     */
    fun isSubOrdinateOrSelf(toId: Int): Boolean {
        return (toId == playerId) || playerInternalData.subordinateIdList.contains(toId)
    }

    /**
     * @param toId whether this id is the player or one of the leaders of the player
     */
    fun isLeaderOrSelf(toId: Int): Boolean {
        return (toId == playerId) || playerInternalData.leaderIdList.contains(toId)
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
 * @property eventDataMap list of current event on this player
 * @property physicsData physics-related data
 * @property popSystemData population system related data
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
    val eventDataMap: Map<Int, EventData> = mapOf(),
    val dataComponentMap: DataComponentMap = DataComponentMap(
        listOf(
            AIData(),
            DiplomacyData(),
            EconomyData(),
            PhysicsData(),
            PlayerScienceData(),
            PoliticsData(),
            PopSystemData(),
            ModifierData(),
        )
    ),
) {
    fun aiData(): AIData = dataComponentMap.getOrDefault(AIData::class, AIData())

    fun diplomacyData(): DiplomacyData =
        dataComponentMap.getOrDefault(DiplomacyData::class, DiplomacyData())

    fun economyData(): EconomyData =
        dataComponentMap.getOrDefault(EconomyData::class, EconomyData())

    fun modifierData(): ModifierData =
        dataComponentMap.getOrDefault(ModifierData::class, ModifierData())

    fun physicsData(): PhysicsData =
        dataComponentMap.getOrDefault(PhysicsData::class, PhysicsData())

    fun playerScienceData(): PlayerScienceData =
        dataComponentMap.getOrDefault(PlayerScienceData::class, PlayerScienceData())

    fun politicsData(): PoliticsData =
        dataComponentMap.getOrDefault(PoliticsData::class, PoliticsData())

    fun popSystemData(): PopSystemData =
        dataComponentMap.getOrDefault(PopSystemData::class, PopSystemData())

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

@Serializable
data class MutablePlayerInternalData(
    var directLeaderId: Int,
    var leaderIdList: MutableList<Int>,
    var directSubordinateIdList: MutableList<Int> = mutableListOf(),
    var subordinateIdList: MutableList<Int> = mutableListOf(),
    var isAlive: Boolean = true,
    var eventDataMap: MutableMap<Int, MutableEventData> = mutableMapOf(),
    var dataComponentMap: MutableDataComponentMap = MutableDataComponentMap(
        listOf(
            MutableAIData(),
            MutableDiplomacyData(),
            MutableEconomyData(),
            MutableModifierData(),
            MutablePhysicsData(),
            MutablePlayerScienceData(),
            MutablePoliticsData(),
            MutablePopSystemData(),
        )
    ),
) {
    fun aiData(): MutableAIData =
        dataComponentMap.getOrDefault(MutableAIData::class, MutableAIData())

    fun aiData(newAIData: MutableAIData) = dataComponentMap.put(newAIData)

    fun diplomacyData(): MutableDiplomacyData =
        dataComponentMap.getOrDefault(MutableDiplomacyData::class, MutableDiplomacyData())

    fun diplomacyData(newDiplomacyData: MutableDiplomacyData) =
        dataComponentMap.put(newDiplomacyData)

    fun economyData(): MutableEconomyData =
        dataComponentMap.getOrDefault(MutableEconomyData::class, MutableEconomyData())

    fun economyData(newEconomyData: MutableEconomyData) = dataComponentMap.put(newEconomyData)

    fun modifierData(): MutableModifierData =
        dataComponentMap.getOrDefault(MutableModifierData::class, MutableModifierData())

    fun modifierData(newModifierData: MutableModifierData) = dataComponentMap.put(newModifierData)

    fun physicsData(): MutablePhysicsData =
        dataComponentMap.getOrDefault(MutablePhysicsData::class, MutablePhysicsData())

    fun physicsData(newPhysicsData: MutablePhysicsData) = dataComponentMap.put(newPhysicsData)

    fun playerScienceData(): MutablePlayerScienceData =
        dataComponentMap.getOrDefault(MutablePlayerScienceData::class, MutablePlayerScienceData())

    fun playerScienceData(newPlayerScienceData: MutablePlayerScienceData) =
        dataComponentMap.put(newPlayerScienceData)

    fun politicsData(): MutablePoliticsData =
        dataComponentMap.getOrDefault(MutablePoliticsData::class, MutablePoliticsData())

    fun politicsData(newPoliticsData: MutablePoliticsData) = dataComponentMap.put(newPoliticsData)

    fun popSystemData(): MutablePopSystemData =
        dataComponentMap.getOrDefault(MutablePopSystemData::class, MutablePopSystemData())

    fun popSystemData(newPopSystemData: MutablePopSystemData) =
        dataComponentMap.put(newPopSystemData)


    /**
     * Change direct leader id without removing the old direct leader as one of the leader
     */
    fun changeDirectLeaderId(playerId: Int) {
        directLeaderId = playerId
        leaderIdList.add(playerId)
    }

    /**
     * Add subordinate to this player
     */
    fun addDirectSubordinateId(playerId: Int) {
        directSubordinateIdList.add(playerId)
        subordinateIdList.add(playerId)
    }

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