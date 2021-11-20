package relativitization.universe.ai

import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

sealed class AI {
    abstract fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command>
}

fun AI.name(): String = this::class.simpleName.toString()

object AICollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val aiList: List<AI> = AI::class.sealedSubclasses.map { it.objectInstance!! }

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