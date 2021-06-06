package relativitization.universe.mechanisms

import org.apache.logging.log4j.LogManager
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.mechanisms.events.AutoEventCollection
import relativitization.universe.mechanisms.events.ProcessEvents

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

    private val defaultMechanismList: List<Mechanism> = listOf(
        AutoEventCollection,
        ProcessEvents,
    )

    // list of all possible process collection name
    val mechanismProcessNameList: List<String> = listOf(
        "DefaultMechanism",
        "Empty"
    )

    fun processMechanismCollection(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData,
    ): List<Command> {
        return when (universeData.universeSettings.mechanismCollectionName) {
            "DefaultMechanism" -> {
                defaultMechanismList.map { mechanism ->
                    mechanism.process(mutablePlayerData, universeData3DAtPlayer, universeData)
                }.flatten()
            }
            "Empty" -> {
                listOf()
            }
            else -> {
                logger.error("No mechanism name matched, use default mechanism")
                listOf()
            }
        }
    }
}