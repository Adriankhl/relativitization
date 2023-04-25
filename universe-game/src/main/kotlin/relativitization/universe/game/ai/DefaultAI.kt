package relativitization.universe.game.ai

import relativitization.universe.game.ai.defaults.node.other.OtherPlayerReasoner
import relativitization.universe.game.ai.defaults.node.self.SelfReasoner
import relativitization.universe.game.ai.defaults.node.special.RecordRecentlySentCommandAINode
import relativitization.universe.game.ai.defaults.node.subordinate.SubordinateReasoner
import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.ai.defaults.utils.SequenceReasoner
import relativitization.universe.game.data.PlanDataAtPlayer
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.utils.RelativitizationLogManager
import kotlin.random.Random

object DefaultAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun name(): String = "Default"
    override fun compute(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
    ): List<Command> {
        logger.debug("Computing player ${universeData3DAtPlayer.id} with DefaultAI")
        return RootReasoner(
            universeData3DAtPlayer,
            random
        ).computeCommandList()
    }
}

class RootReasoner(
    universeData3DAtPlayer: UniverseData3DAtPlayer,
    private val random: Random
) : SequenceReasoner() {

    private val planDataAtPlayer: PlanDataAtPlayer = universeData3DAtPlayer.getPlanDataAtPlayer {}
    private val planState: PlanState = PlanState()

    fun computeCommandList(): List<Command> {
        logger.debug("Root reasoner computing commandList")
        updatePlan(planDataAtPlayer, planState)
        return planDataAtPlayer.commandList
    }

    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        SelfReasoner(random),
        SubordinateReasoner(random),
        OtherPlayerReasoner(random),
        RecordRecentlySentCommandAINode(),
    )

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}