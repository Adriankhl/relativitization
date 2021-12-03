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

sealed class MechanismList {
    // Mechanisms that are not affected by time dilation
    abstract val regularMechanismList: List<Mechanism>

    // Mechanisms that are affected by time dilation
    abstract val dilatedMechanismList: List<Mechanism>
}

fun MechanismList.name(): String = this::class.simpleName.toString()

object MechanismCollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val mechanismListList: List<MechanismList> =
        MechanismList::class.sealedSubclasses.map { it.objectInstance!! }

    val mechanismListMap: Map<String, MechanismList> = mechanismListList.map {
        it.name() to it
    }.toMap()

    fun processMechanismCollection(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData,
    ): List<Command> {

        return mechanismListMap.getOrElse(universeData.universeSettings.mechanismCollectionName) {
            logger.error("No mechanism name matched, use empty mechanism")
            EmptyMechanismList
        }.regularMechanismList.map { mechanism ->
            if (mutablePlayerData.playerInternalData.isAlive) {
                mechanism.process(
                    mutablePlayerData,
                    universeData3DAtPlayer,
                    universeData.universeSettings,
                    universeData.universeGlobalData
                )
            } else {
                logger.debug("Player ${mutablePlayerData.playerId} is not alive, mechanism not ")
                listOf()
            }
        }.flatten()
    }
}