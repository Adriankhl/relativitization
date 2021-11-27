package relativitization.universe.data.components.defaults.diplomacy

import kotlinx.serialization.Serializable

enum class DiplomaticRelationState(val value: String) {
    NEUTRAL("Neutral"),
    ALLY("Ally"),
    ENEMY("Enemy"),
    ;

    override fun toString(): String {
        return value
    }
}

@Serializable
data class DiplomaticRelationData(
    val relation: Double = 0.0,
    val diplomaticRelationState: DiplomaticRelationState = DiplomaticRelationState.NEUTRAL,
)

@Serializable
data class MutableDiplomaticRelationData(
    var relation: Double = 0.0,
    var diplomaticRelationState: DiplomaticRelationState = DiplomaticRelationState.NEUTRAL,
)

