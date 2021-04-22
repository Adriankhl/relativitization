package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.diplomacy.DiplomacyData
import relativitization.universe.data.diplomacy.MutableDiplomacyData
import relativitization.universe.data.economy.EconomyData
import relativitization.universe.data.economy.MutableEconomyData
import relativitization.universe.data.events.EventData
import relativitization.universe.data.events.MutableEventData
import relativitization.universe.data.physics.Int4D
import relativitization.universe.data.physics.MutableInt4D
import relativitization.universe.data.physics.MutablePhysicsData
import relativitization.universe.data.physics.PhysicsData
import relativitization.universe.data.politics.MutablePoliticsData
import relativitization.universe.data.politics.PoliticsData
import relativitization.universe.data.popsystems.MutablePopSystemicData
import relativitization.universe.data.popsystems.PopSystemicData
import relativitization.universe.data.science.MutableScienceData
import relativitization.universe.data.science.ScienceData

/**
 * Data of the basic unit (player)
 *
 * @property id playerId
 * @property playerType ai / human / none (e.g. resource)
 * @property int4D 4D coordinate of the player
 * @property attachedPlayerId the player which this player is currently attached to
 * @property int4DHistory historical coordinate of the player
 * @property playerInternalData the internal data of this player
 */
@Serializable
data class PlayerData(
    val id: Int,
    val name: String = "Default Player",
    val playerType: PlayerType = PlayerType.AI,
    val int4D: Int4D = Int4D(0, 0, 0, 0),
    val attachedPlayerId: Int = -1,
    val int4DHistory: List<Int4D> = listOf(),
    val playerInternalData: PlayerInternalData = PlayerInternalData()
)

@Serializable
data class MutablePlayerData(
    val id: Int,
    var name: String = "Default Player",
    var playerType: PlayerType = PlayerType.AI,
    var int4D: MutableInt4D = MutableInt4D(0, 0, 0, 0),
    var attachedPlayerId: Int = -1,
    val int4DHistory: MutableList<Int4D> = mutableListOf(),
    var playerInternalData: MutablePlayerInternalData = MutablePlayerInternalData()
)

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
 * @property eventDataList list of current event on this player
 * @property physicsData physics-related data
 * @property popSystemicData population system related data
 * @property scienceData research related data
 * @property politicsData political related data
 * @property diplomacyData diplomatic relation data
 * @property economyData economy related data
 */
@Serializable
data class PlayerInternalData(
    val directLeaderId: Int = -1,
    val directSubordinateIdList: List<Int> = listOf(),
    val leaderIdList: List<Int> = listOf(-1),
    val subordinateIdList: List<Int> = listOf(),
    val isAlive: Boolean = true,
    val eventDataList: List<EventData> = listOf(),
    val physicsData: PhysicsData = PhysicsData(),
    val popSystemicData: PopSystemicData = PopSystemicData(),
    val scienceData: ScienceData = ScienceData(),
    val politicsData: PoliticsData = PoliticsData(),
    val diplomacyData: DiplomacyData = DiplomacyData(),
    val economyData: EconomyData = EconomyData(),
)

@Serializable
data class MutablePlayerInternalData(
    var directLeaderId: Int = -1,
    var directSubordinateIdList: MutableList<Int> = mutableListOf(),
    var leaderIdList: MutableList<Int> = mutableListOf(-1),
    var subordinateIdList: MutableList<Int> = mutableListOf(),
    var isAlive: Boolean = true,
    var eventDataList: MutableList<MutableEventData> = mutableListOf(),
    var physicsData: MutablePhysicsData = MutablePhysicsData(),
    var popSystemicData: MutablePopSystemicData = MutablePopSystemicData(),
    var scienceData: MutableScienceData = MutableScienceData(),
    var politicsData: MutablePoliticsData = MutablePoliticsData(),
    var diplomacyData: MutableDiplomacyData = MutableDiplomacyData(),
    var economyData: MutableEconomyData = MutableEconomyData(),
)