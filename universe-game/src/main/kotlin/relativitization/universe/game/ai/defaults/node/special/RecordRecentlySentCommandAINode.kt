package relativitization.universe.game.ai.defaults.node.special

import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.core.data.commands.Command
import relativitization.universe.game.ai.defaults.utils.AINode
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.game.data.commands.AddRecentCommandTimeCommand

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