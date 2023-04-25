package relativitization.universe.core.ai

import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.utils.RelativitizationLogManager
import kotlin.random.Random

abstract class AI {
    open fun name(): String = this::class.simpleName.toString()

    abstract fun compute(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
    ): List<Command>
}

object AICollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val aiNameMap: MutableMap<String, AI> = mutableMapOf(
        EmptyAI.name() to EmptyAI,
    )

    fun getAINames(): Set<String> = aiNameMap.keys

    fun addAI(ai: AI) {
        val aiName: String = ai.name()
        if (aiNameMap.containsKey(aiName)) {
            logger.debug("Already has $aiName in AICollection, replacing stored $aiName")
        }

        aiNameMap[aiName] = ai
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