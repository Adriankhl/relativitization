package relativitization.universe.ai.defaults.node.special

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.AddRecentCommandTimeCommand
import relativitization.universe.data.commands.Command
import kotlin.random.Random

class RecordRecentlySentCommandAINode(random: Random) : AINode(random) {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val toIdSet: Set<Int> = planDataAtPlayer.commandList.map { it.toId }.toSet()
        val allUpdateCommand: List<Command> = toIdSet.filter {
            it != planDataAtPlayer.getCurrentMutablePlayerData().playerId
        }.map {
            AddRecentCommandTimeCommand(
                planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData().playerId,
                planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData().playerId,
                planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData().int4D,
                it,
            )
        }
        planDataAtPlayer.addAllCommand(allUpdateCommand)
    }
}