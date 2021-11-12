package relativitization.universe.mechanisms

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.main.events.AutoEventCollection
import relativitization.universe.mechanisms.main.events.ProcessEvents
import relativitization.universe.mechanisms.main.logistics.ExportResource
import relativitization.universe.mechanisms.main.logistics.SendTax
import relativitization.universe.mechanisms.main.modifier.UpdateModifierTime
import relativitization.universe.mechanisms.main.pop.*
import relativitization.universe.mechanisms.main.production.BaseStellarFuelProduction
import relativitization.universe.mechanisms.main.production.EntertainmentProduction
import relativitization.universe.mechanisms.main.production.FuelFactoryProduction
import relativitization.universe.mechanisms.main.production.ResourceFactoryProduction
import relativitization.universe.mechanisms.main.research.DiscoverKnowledge
import relativitization.universe.mechanisms.main.research.KnowledgeDiffusion
import relativitization.universe.mechanisms.main.science.SyncPlayerScienceData
import relativitization.universe.mechanisms.main.science.UpdateScienceProductData
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
        Employment,
        PopBuyResource,
        UpdateDesire,
        BaseStellarFuelProduction,
        FuelFactoryProduction,
        ResourceFactoryProduction,
        EntertainmentProduction,
        ExportResource,
        Educate,
        PopulationGrowth,
        SendTax,
        UpdateModifierTime,
        KnowledgeDiffusion,
        DiscoverKnowledge,
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
                universeData.universeGlobalData
            )
        }.flatten()
    }
}