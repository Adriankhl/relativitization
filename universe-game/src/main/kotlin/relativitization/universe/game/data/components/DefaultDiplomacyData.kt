package relativitization.universe.game.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable
import relativitization.universe.core.data.MutablePlayerInternalData
import relativitization.universe.core.data.PlayerInternalData
import relativitization.universe.game.data.components.defaults.diplomacy.MutableRelationData
import relativitization.universe.game.data.components.defaults.diplomacy.RelationData

/**
 * @property relationData describe the relation between this player and other players
 * @property peacePlayerIdSet this player cannot declare war on the players in this set
 */
@GenerateImmutable
@SerialName("DiplomacyData")
data class MutableDiplomacyData(
    var relationData: MutableRelationData = MutableRelationData(),
    val peacePlayerIdSet: MutableSet<Int> = mutableSetOf(),
) : MutableDefaultPlayerDataComponent()

fun PlayerInternalData.diplomacyData(): DiplomacyData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.diplomacyData(): MutableDiplomacyData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.diplomacyData(newDiplomacyData: MutableDiplomacyData) =
    playerDataComponentMap.put(newDiplomacyData)