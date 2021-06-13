package relativitization.universe.ai.emptyAI

import relativitization.universe.ai.AI
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

class EmptyAI : AI() {
    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        logger.debug("Computing with EmptyAI")
        return listOf()
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}