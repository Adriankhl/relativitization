package relativitization.universe.mechanisms

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic
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

    val mechanismListsMap: Map<String, MechanismLists> = mechanismListsList.map {
        it.name() to it
    }.toMap()

    fun processMechanismCollection(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData,
    ): List<Command> {

        val gamma: Double = Relativistic.gamma(
            universeData3DAtPlayer.getCurrentPlayerData().velocity,
            universeData.universeSettings.speedOfLight
        )

        // Update dilated time residue
        mutablePlayerData.dilatedTimeResidue += 1.0 / gamma

        val mechanismLists: MechanismLists =
            mechanismListsMap.getOrElse(universeData.universeSettings.mechanismCollectionName) {
                logger.error("No mechanism name matched, use empty mechanism")
                EmptyMechanismLists
            }

        val regularMechanismCommandList: List<Command> =
            mechanismLists.regularMechanismList.map { mechanism ->
                if (mutablePlayerData.playerInternalData.isAlive) {
                    mechanism.process(
                        mutablePlayerData,
                        universeData3DAtPlayer,
                        universeData.universeSettings,
                        universeData.universeGlobalData
                    )
                } else {
                    logger.debug("Player ${mutablePlayerData.playerId} is not alive, regular mechanism not processed")
                    listOf()
                }
            }.flatten()

        // Only process dilated mechanisms if dilated time residue is bigger than or equal to 1.0
        val dilatedMechanismCommandList: List<Command> =
            if (mutablePlayerData.dilatedTimeResidue >= 1.0) {
                // Compute the residue, should be always smaller than 1
                mutablePlayerData.dilatedTimeResidue -= 1.0

                mechanismLists.dilatedMechanismList.map { mechanism ->
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