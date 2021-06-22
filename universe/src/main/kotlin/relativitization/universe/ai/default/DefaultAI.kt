package relativitization.universe.ai.default

import relativitization.universe.ai.AI
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

object DefaultAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        logger.debug("Computing player ${universeData3DAtPlayer.id} with DefaultAI")
        return listOf()
    }
}