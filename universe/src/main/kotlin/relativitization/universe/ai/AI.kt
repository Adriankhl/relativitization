package relativitization.universe.ai

import relativitization.universe.ai.abm.FlockingAI
import relativitization.universe.ai.default.DefaultAI
import relativitization.universe.ai.emptyAI.EmptyAI
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

abstract class AI {
    abstract fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command>
}

object AICollection {
    private val logger = RelativitizationLogManager.getLogger()

    val aiNameList: List<String> = listOf(
        "DefaultAI",
        "EmptyAI",
        "FlockingAI",
    )

    fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        val aiName: String = universeData3DAtPlayer.get(universeData3DAtPlayer.id).playerInternalData.playerState.aiState.aiName
        return when(aiName) {
            "DefaultAI" -> DefaultAI.compute(universeData3DAtPlayer)
            "EmptyAI" -> EmptyAI.compute(universeData3DAtPlayer)
            "FlockingAI" -> FlockingAI.compute(universeData3DAtPlayer)
            else -> {
                logger.error("No ai name: ${aiName}, using default (empty) ai")
                EmptyAI.compute(universeData3DAtPlayer)
            }
        }
    }
}