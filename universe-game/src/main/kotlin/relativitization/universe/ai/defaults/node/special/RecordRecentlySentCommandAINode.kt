package relativitization.universe.ai.defaults.node.special

import relativitization.universe.ai.defaults.utils.AINode
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.AddRecentCommandTimeCommand
import relativitization.universe.data.commands.Command

class RecordRecentlySentCommandAINode : AINode() {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val toIdSet: Set<Int> = planDataAtPlayer.commandList.map { it.toId }.toSet()
        val allUpdateCommand: List<Command> = toIdSet.filter {
            it != planDataAtPlayer.getCurrentMutablePlayerData().playerId
        }.map {
            AddRecentCommandTimeCommand(
                toId = planDataAtPlayer.universeData3DAtPlayer.getCurrentPlayerData().playerId,
                recentCommandPlayerId = it,
            )
        }
        planDataAtPlayer.addAllCommand(allUpdateCommand)
    }
}