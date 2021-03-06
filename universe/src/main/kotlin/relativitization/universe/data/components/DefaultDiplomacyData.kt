package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.PlayerInternalData
import relativitization.universe.data.components.defaults.diplomacy.*
import relativitization.universe.data.components.defaults.diplomacy.war.MutableWarData
import relativitization.universe.data.components.defaults.diplomacy.war.WarData

/**
 * @property relationData describe the relation between this player and other players
 * @property peacePlayerIdSet this player cannot declare war on the players in this set
 */
@Serializable
@SerialName("DiplomacyData")
data class DiplomacyData(
    val relationData: RelationData = RelationData(),
    val peacePlayerIdSet: Set<Int> = setOf(),
) : DefaultPlayerDataComponent()

@Serializable
@SerialName("DiplomacyData")
data class MutableDiplomacyData(
    var relationData: MutableRelationData = MutableRelationData(),
    val peacePlayerIdSet: MutableSet<Int> = mutableSetOf(),
) : MutableDefaultPlayerDataComponent()

fun PlayerInternalData.diplomacyData(): DiplomacyData =
    playerDataComponentMap.getOrDefault(DiplomacyData::class, DiplomacyData())

fun MutablePlayerInternalData.diplomacyData(): MutableDiplomacyData =
    playerDataComponentMap.getOrDefault(MutableDiplomacyData::class, MutableDiplomacyData())

fun MutablePlayerInternalData.diplomacyData(newDiplomacyData: MutableDiplomacyData) =
    playerDataComponentMap.put(newDiplomacyData)