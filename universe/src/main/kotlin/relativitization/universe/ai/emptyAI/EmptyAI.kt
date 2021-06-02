package relativitization.universe.ai.emptyAI

import org.apache.logging.log4j.LogManager
import relativitization.universe.ai.AI
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command

class EmptyAI : AI() {
    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        logger.debug("Computing with EmptyAI")
        return listOf()
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}