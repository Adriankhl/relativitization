package relativitization.universe.ai

import org.apache.logging.log4j.LogManager
import relativitization.universe.ai.abm.FlockingAI
import relativitization.universe.ai.emptyAI.EmptyAI
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command

abstract class AI {
    abstract fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command>
}

object AICollection {
    private val logger = LogManager.getLogger()

    val aiNameList: List<String> = listOf(
        "EmptyAI",
        "FlockingAI"
    )

    fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        val aiName: String = universeData3DAtPlayer.get(universeData3DAtPlayer.id).playerInternalData.playerState.aiState.aiName
        return when(aiName) {
            "FlockingAI" -> FlockingAI().compute(universeData3DAtPlayer)
            "EmptyAI" -> EmptyAI().compute(universeData3DAtPlayer)
            else -> {
                logger.error("No ai name: ${aiName}, using default (empty) ai")
                EmptyAI().compute(universeData3DAtPlayer)
            }
        }
    }
}