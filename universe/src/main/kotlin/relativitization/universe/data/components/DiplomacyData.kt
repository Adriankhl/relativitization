package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.components.diplomacy.DiplomaticRelationData
import relativitization.universe.data.components.diplomacy.DiplomaticRelationState
import relativitization.universe.data.components.diplomacy.MutableDiplomaticRelationData

/**
 * @property relationMap map from other player id to the DiplomaticRelationData view by this player
 */
@Serializable
@SerialName("DiplomacyData")
data class DiplomacyData(
    val relationMap: Map<Int, DiplomaticRelationData> = mapOf(),
) : PlayerDataComponent() {
    fun getDiplomaticRelationData(id: Int): DiplomaticRelationData {
        return relationMap.getOrDefault(id, DiplomaticRelationData())
    }

    fun getRelation(id: Int): Double = getDiplomaticRelationData(id).relation

    fun getRelationState(id: Int): DiplomaticRelationState = getDiplomaticRelationData(id).relationState

    /**
     * Whether this player is a enemy of other player
     *
     */
    fun isEnemyOf(mutablePlayerData: MutablePlayerData): Boolean =
        mutablePlayerData.playerInternalData.leaderIdList.any {
            getRelationState(it) == DiplomaticRelationState.ENEMY
        }
}

@Serializable
@SerialName("DiplomacyData")
data class MutableDiplomacyData(
    var relationMap: MutableMap<Int, MutableDiplomaticRelationData> = mutableMapOf(),
) : MutablePlayerDataComponent() {
    fun getDiplomaticRelationData(id: Int): MutableDiplomaticRelationData {
        return relationMap.getOrPut(id) { MutableDiplomaticRelationData() }
    }

    fun getRelation(id: Int): Double = getDiplomaticRelationData(id).relation
}