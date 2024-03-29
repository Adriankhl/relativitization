package relativitization.universe.core.mechanisms

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.utils.RelativitizationLogManager
import kotlin.random.Random

abstract class Mechanism {

    /**
     * Process the player data based on the mechanism
     *
     * @param mutablePlayerData the player data to be changed
     * @param universeData3DAtPlayer the universe data visible to the player
     * @param universeSettings contains some general data like the knowledge network and universe settings
     */
    abstract fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random,
    ): List<Command>
}

abstract class MechanismLists {
    // Mechanisms that are not affected by time dilation
    abstract val regularMechanismList: List<Mechanism>

    // Mechanisms that are affected by time dilation
    abstract val dilatedMechanismList: List<Mechanism>

    open fun name(): String = this::class.simpleName.toString()
}

object MechanismCollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val mechanismListsNameMap: MutableMap<String, MechanismLists> = mutableMapOf(
        EmptyMechanismLists.name() to EmptyMechanismLists,
    )

    fun getMechanismListsNames(): Set<String> = mechanismListsNameMap.keys

    fun addMechanismLists(mechanismLists: MechanismLists) {
        val mechanismListsName: String = mechanismLists.name()
        if (mechanismListsNameMap.containsKey(mechanismListsName)) {
            logger.debug(
                "Already has $mechanismListsName in MechanismCollection, " +
                        "replacing stored $mechanismListsName"
            )
        }

        mechanismListsNameMap[mechanismListsName] = mechanismLists
    }

    fun processMechanismCollection(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData,
        random: Random,
    ): List<Command> {
        val mechanismLists: MechanismLists =
            mechanismListsNameMap.getOrElse(universeData.universeSettings.mechanismCollectionName) {
                logger.error("No mechanism name matched, use empty mechanism")
                EmptyMechanismLists
            }

        val regularMechanismCommandList: List<Command> =
            mechanismLists.regularMechanismList.map { mechanism ->
                if (mutablePlayerData.playerInternalData.isAlive) {
                    logger.debug(
                        "Process regular mechanism ${mechanism::class.simpleName} on " +
                                "player ${mutablePlayerData.playerId}"
                    )
                    mechanism.process(
                        mutablePlayerData,
                        universeData3DAtPlayer,
                        universeData.universeSettings,
                        universeData.universeGlobalData,
                        random,
                    )
                } else {
                    logger.debug(
                        "Player ${mutablePlayerData.playerId} is not alive," +
                                " regular mechanism not processed"
                    )
                    listOf()
                }
            }.flatten()

        // Only process dilated mechanisms if this is the dilation action turn
        val dilatedMechanismCommandList: List<Command> =
            if (mutablePlayerData.isTimeDilationActionTurn) {
                mechanismLists.dilatedMechanismList.map { mechanism ->
                    logger.debug(
                        "Process dilated mechanism ${mechanism::class.simpleName} on " +
                                "player ${mutablePlayerData.playerId}"
                    )
                    if (mutablePlayerData.playerInternalData.isAlive) {
                        mechanism.process(
                            mutablePlayerData,
                            universeData3DAtPlayer,
                            universeData.universeSettings,
                            universeData.universeGlobalData,
                            random,
                        )
                    } else {
                        logger.debug("Player ${mutablePlayerData.playerId} is not alive, dilated mechanism not processed")
                        listOf()
                    }
                }.flatten()
            } else {
                listOf()
            }

        return regularMechanismCommandList + dilatedMechanismCommandList
    }
}