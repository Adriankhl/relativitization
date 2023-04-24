package relativitization.universe.ai

import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random

sealed class AI {
    open fun name(): String = this::class.simpleName.toString()

    abstract fun compute(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
    ): List<Command>
}

object AICollection {
    private val logger = RelativitizationLogManager.getLogger()

    val aiNameMap: Map<String, AI> = AI::class.sealedSubclasses.map {
        it.objectInstance!!
    }.associateBy {
        it.name()
    }

    fun compute(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
        aiName: String = "",
    ): List<Command> {
        val actualAIName: String = if (aiNameMap.containsKey(aiName)) {
            aiName
        } else {
            universeData3DAtPlayer.get(
                universeData3DAtPlayer.id
            ).playerInternalData.aiName
        }

        val ai: AI = aiNameMap.getOrElse(actualAIName) {
            logger.error("No ai name: ${actualAIName}, using default (empty) ai")
            EmptyAI
        }

        return ai.compute(universeData3DAtPlayer, random)
    }
}