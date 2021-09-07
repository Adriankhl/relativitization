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

fun AI.name(): String = this::class.simpleName.toString()

object AICollection {
    private val logger = RelativitizationLogManager.getLogger()

    val aiList: List<AI> = listOf(
        DefaultAI,
        EmptyAI,
        FlockingAI
    )

    val aiNameMap: Map<String, AI> = aiList.map {
        it.name() to it
    }.toMap()

    fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        val aiName: String = universeData3DAtPlayer.get(
            universeData3DAtPlayer.id
        ).playerInternalData.aiData().aiName

        val ai: AI = aiNameMap.getOrElse(aiName) {
            logger.error("No ai name: ${aiName}, using default (empty) ai")
            EmptyAI
        }

        return ai.compute(universeData3DAtPlayer)
    }
}