package relativitization.universe.ai.abm

import org.apache.logging.log4j.LogManager
import relativitization.universe.ai.AI
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command

class FlockingAI : AI() {
    override fun compute(universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        logger.debug("Computing with FlockingAI")

        return listOf()
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}