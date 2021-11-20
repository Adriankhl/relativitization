package relativitization.universe.ai

import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

object EmptyAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        logger.debug("Computing with EmptyAI")
        return listOf()
    }
}