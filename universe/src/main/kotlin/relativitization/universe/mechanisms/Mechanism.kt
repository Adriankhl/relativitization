package relativitization.universe.mechanisms

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.tools.picocli.CommandLine
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command

abstract class Mechanism {

    /**
     * Process the player data based on the mechanism
     *
     * @param mutablePlayerData the player data to be changed
     * @param universeData3DAtPlayer the universe data visible to the player
     * @param universeData contains some general data like the knowledge network and universe settings
     */
    abstract fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData
    ): List<Command>
}

object MechanismCollection {
    private val logger = LogManager.getLogger()

    private val defaultMechanismList: List<Mechanism> = listOf()

    fun mechanismProcess(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData,
    ): List<Command> {
        return when (universeData.universeSettings.mechanismProcessName) {
            "default" -> {
                defaultMechanismList.map { mechanism ->
                    mechanism.process(mutablePlayerData, universeData3DAtPlayer, universeData)
                }.flatten()
            }
            else -> {
                logger.error("No mechanism name matched, use default mechanism")
                defaultMechanismList.map { mechanism ->
                    mechanism.process(mutablePlayerData, universeData3DAtPlayer, universeData)
                }.flatten()
            }
        }
    }
}