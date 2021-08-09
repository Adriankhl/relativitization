package relativitization.universe.mechanisms

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.mechanisms.events.AutoEventCollection
import relativitization.universe.mechanisms.events.ProcessEvents
import relativitization.universe.mechanisms.modifier.UpdateModifierTime
import relativitization.universe.mechanisms.science.SyncPlayerScienceData
import relativitization.universe.mechanisms.science.UpdateScienceProductData
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
        universeScienceData: UniverseScienceData
    ): List<Command>
}

object MechanismCollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val defaultMechanismList: List<Mechanism> = listOf(
        AutoEventCollection,
        ProcessEvents,
        UpdateModifierTime,
        SyncPlayerScienceData,
        UpdateScienceProductData,
    )

    // list of all possible process collection name
    val mechanismProcessNameMap: Map<String, List<Mechanism>> = mapOf(
        "DefaultMechanism" to defaultMechanismList,
        "EmptyMechanism" to listOf(),
    )

    fun processMechanismCollection(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData,
    ): List<Command> {

        return mechanismProcessNameMap.getOrElse(universeData.universeSettings.mechanismCollectionName) {
            logger.error("No mechanism name matched, use default mechanism")
            listOf()
        }.map { mechanism ->
            mechanism.process(
                mutablePlayerData,
                universeData3DAtPlayer,
                universeData.universeSettings,
                universeData.universeScienceData
            )
        }.flatten()
    }
}