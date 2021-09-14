package relativitization.universe.ai.default

import relativitization.universe.ai.AI
import relativitization.universe.ai.default.event.EventReasoner
import relativitization.universe.ai.default.utils.Consideration
import relativitization.universe.ai.default.utils.Option
import relativitization.universe.ai.default.utils.SequenceReasoner
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

object DefaultAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        logger.debug("Computing player ${universeData3DAtPlayer.id} with DefaultAI")
        return RootReasoner(universeData3DAtPlayer).computeCommandList()
    }
}

class RootReasoner(
    universeData3DAtPlayer: UniverseData3DAtPlayer
) : SequenceReasoner() {

    private val planDAtPlayer: PlanDataAtPlayer = universeData3DAtPlayer.getPlanDataAtPlayer()

    fun computeCommandList(): List<Command> {
        logger.debug("Root reasoner computing commandList")
        updateData()
        return planDAtPlayer.commandList
    }

    override fun getOptionList(): List<Option> {
        return listOf(
            EventReasoner(planDAtPlayer),
        )
    }

    override fun getConsiderationList(): List<Consideration> = listOf()

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}