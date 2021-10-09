package relativitization.universe.mechanisms

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.UniverseScienceData
import relativitization.universe.mechanisms.events.AutoEventCollection
import relativitization.universe.mechanisms.events.ProcessEvents
import relativitization.universe.mechanisms.modifier.UpdateModifierTime
import relativitization.universe.mechanisms.production.ResourceFactoryProduction
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

abstract class MechanismList {
    abstract val mechanismList: List<Mechanism>
}

fun MechanismList.name(): String = this::class.simpleName.toString()

object EmptyMechanismList : MechanismList() {
    override val mechanismList: List<Mechanism> = listOf()
}

object DefaultMechanismList : MechanismList() {
    override val mechanismList: List<Mechanism> = listOf(
        AutoEventCollection,
        ProcessEvents,
        ResourceFactoryProduction,
        UpdateModifierTime,
        SyncPlayerScienceData,
        UpdateScienceProductData,
    )
}

object MechanismCollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val mechanismListList: List<MechanismList> = listOf(
        DefaultMechanismList,
        EmptyMechanismList,
    )

    val mechanismListMap: Map<String, MechanismList> = mechanismListList.map {
        it.name() to it
    }.toMap()

    fun processMechanismCollection(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData,
    ): List<Command> {

        return mechanismListMap.getOrElse(universeData.universeSettings.mechanismCollectionName) {
            logger.error("No mechanism name matched, use default mechanism")
            EmptyMechanismList
        }.mechanismList.map { mechanism ->
            mechanism.process(
                mutablePlayerData,
                universeData3DAtPlayer,
                universeData.universeSettings,
                universeData.universeScienceData
            )
        }.flatten()
    }
}