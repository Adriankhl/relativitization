package relativitization.universe.core.ai

import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.utils.RelativitizationLogManager
import kotlin.random.Random

object EmptyAI : AI() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun compute(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
    ): List<Command> {
        logger.debug("Computing with EmptyAI")
        return listOf()
    }
}