package relativitization.universe.ai

import relativitization.universe.ai.emptyAI.EmptyAI
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command

abstract class AI {
    abstract fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command>
}

object PickAI {
    fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        return when(universeData3DAtPlayer.get(universeData3DAtPlayer.id).playerInternalData.playerState.aiState.aiName) {
            "EmptyAI" -> EmptyAI().compute(universeData3DAtPlayer)
            else -> EmptyAI().compute(universeData3DAtPlayer)
        }
    }
}