package relativitization.universe.mechanisms

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.utils.RelativitizationLogManager

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
        universeGlobalData: UniverseGlobalData
    ): List<Command>
}

sealed class MechanismLists {
    // Mechanisms that are not affected by time dilation
    abstract val regularMechanismList: List<Mechanism>

    // Mechanisms that are affected by time dilation
    abstract val dilatedMechanismList: List<Mechanism>
}

fun MechanismLists.name(): String = this::class.simpleName.toString()

object MechanismCollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val mechanismListsList: List<MechanismLists> =
        MechanismLists::class.sealedSubclasses.map { it.objectInstance!! }

    val mechanismListsMap: Map<String, MechanismLists> = mechanismListsList.associateBy {
        it.name()
    }

    fun processMechanismCollection(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData,
    ): List<Command> {
        val mechanismLists: MechanismLists =
            mechanismListsMap.getOrElse(universeData.universeSettings.mechanismCollectionName) {
                logger.error("No mechanism name matched, use empty mechanism")
                EmptyMechanismLists
            }

        val regularMechanismCommandList: List<Command> =
            mechanismLists.regularMechanismList.map { mechanism ->
                if (mutablePlayerData.playerInternalData.isAlive) {
                    logger.debug("Process regular mechanism ${mechanism::class.simpleName} on " +
                            "player ${mutablePlayerData.playerId}")
                    mechanism.process(
                        mutablePlayerData,
                        universeData3DAtPlayer,
                        universeData.universeSettings,
                        universeData.universeGlobalData
                    )
                } else {
                    logger.debug("Player ${mutablePlayerData.playerId} is not alive," +
                            " regular mechanism not processed")
                    listOf()
                }
            }.flatten()

        // Only process dilated mechanisms if this is the dilation action turn
        val dilatedMechanismCommandList: List<Command> =
            if (mutablePlayerData.isDilationActionTurn) {
                mechanismLists.dilatedMechanismList.map { mechanism ->
                    logger.debug("Process dilated mechanism ${mechanism::class.simpleName} on " +
                            "player ${mutablePlayerData.playerId}")
                    if (mutablePlayerData.playerInternalData.isAlive) {
                        mechanism.process(
                            mutablePlayerData,
                            universeData3DAtPlayer,
                            universeData.universeSettings,
                            universeData.universeGlobalData
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