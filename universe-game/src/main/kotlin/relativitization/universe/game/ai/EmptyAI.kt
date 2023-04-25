package relativitization.universe.game.ai

import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.utils.RelativitizationLogManager
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