package relativitization.universe.mechanisms

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.default.administration.SyncHierarchy
import relativitization.universe.mechanisms.default.combat.AutoCombat
import relativitization.universe.mechanisms.default.dead.ClearDeadPlayer
import relativitization.universe.mechanisms.default.diplomacy.UpdateDiplomaticRelation
import relativitization.universe.mechanisms.default.diplomacy.UpdateDiplomaticRelationState
import relativitization.universe.mechanisms.default.diplomacy.UpdateWarState
import relativitization.universe.mechanisms.default.economy.SyncTaxRate
import relativitization.universe.mechanisms.default.events.AutoEventCollection
import relativitization.universe.mechanisms.default.events.ProcessEvents
import relativitization.universe.mechanisms.default.logistics.ExportResource
import relativitization.universe.mechanisms.default.logistics.SendTax
import relativitization.universe.mechanisms.default.military.UpdateMilitaryBase
import relativitization.universe.mechanisms.default.modifier.UpdateModifierTime
import relativitization.universe.mechanisms.default.pop.*
import relativitization.universe.mechanisms.default.production.BaseStellarFuelProduction
import relativitization.universe.mechanisms.default.production.EntertainmentProduction
import relativitization.universe.mechanisms.default.production.FuelFactoryProduction
import relativitization.universe.mechanisms.default.production.ResourceFactoryProduction
import relativitization.universe.mechanisms.default.research.DiscoverKnowledge
import relativitization.universe.mechanisms.default.research.KnowledgeDiffusion
import relativitization.universe.mechanisms.default.science.SyncPlayerScienceData
import relativitization.universe.mechanisms.default.science.UpdateScienceApplicationData
import relativitization.universe.mechanisms.default.sync.SyncDataComponent
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
    abstract val mechanismList: List<Mechanism>
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