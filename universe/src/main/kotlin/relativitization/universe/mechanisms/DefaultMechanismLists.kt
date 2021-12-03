package relativitization.universe.mechanisms

import relativitization.universe.mechanisms.defaults.dilated.modifier.UpdateModifierByProperTime
import relativitization.universe.mechanisms.defaults.regular.combat.AutoCombat
import relativitization.universe.mechanisms.defaults.regular.dead.ClearDeadPlayer
import relativitization.universe.mechanisms.defaults.regular.diplomacy.UpdateDiplomaticRelation
import relativitization.universe.mechanisms.defaults.regular.diplomacy.UpdateDiplomaticRelationState
import relativitization.universe.mechanisms.defaults.regular.diplomacy.UpdateWarState
import relativitization.universe.mechanisms.defaults.regular.economy.UpdateTaxRate
import relativitization.universe.mechanisms.defaults.regular.events.AutoEventCollection
import relativitization.universe.mechanisms.defaults.regular.events.ProcessEvents
import relativitization.universe.mechanisms.defaults.regular.logistics.ExportResource
import relativitization.universe.mechanisms.defaults.regular.logistics.SendTax
import relativitization.universe.mechanisms.defaults.regular.military.UpdateMilitaryBase
import relativitization.universe.mechanisms.defaults.regular.modifier.UpdateModifierByUniverseTime
import relativitization.universe.mechanisms.defaults.regular.politics.MergePlayer
import relativitization.universe.mechanisms.defaults.regular.politics.UpdatePoliticsData
import relativitization.universe.mechanisms.defaults.regular.pop.*
import relativitization.universe.mechanisms.defaults.regular.production.BaseStellarFuelProduction
import relativitization.universe.mechanisms.defaults.regular.production.EntertainmentProduction
import relativitization.universe.mechanisms.defaults.regular.production.FuelFactoryProduction
import relativitization.universe.mechanisms.defaults.regular.production.ResourceFactoryProduction
import relativitization.universe.mechanisms.defaults.regular.research.DiscoverKnowledge
import relativitization.universe.mechanisms.defaults.regular.research.KnowledgeDiffusion
import relativitization.universe.mechanisms.defaults.regular.science.UpdateScienceApplicationData
import relativitization.universe.mechanisms.defaults.regular.sync.SyncHierarchy
import relativitization.universe.mechanisms.defaults.regular.sync.SyncPlayerData
import relativitization.universe.mechanisms.defaults.regular.sync.SyncPlayerScienceData

object DefaultMechanismLists : MechanismLists() {
    override val regularMechanismList: List<Mechanism> = listOf(
        SyncPlayerData,
        ClearDeadPlayer,
        AutoEventCollection,
        ProcessEvents,
        UpdateWarState,
        UpdateDiplomaticRelation,
        UpdateDiplomaticRelationState,
        SyncHierarchy,
        MergePlayer,
        UpdateTaxRate,
        UpdatePoliticsData,
        SyncPlayerScienceData,
        UpdateScienceApplicationData,
        UpdateModifierByUniverseTime,
    )

    override val dilatedMechanismList: List<Mechanism> = listOf(
        Employment,
        PopBuyResource,
        UpdateDesire,
        UpdateMilitaryBase,
        BaseStellarFuelProduction,
        FuelFactoryProduction,
        ResourceFactoryProduction,
        EntertainmentProduction,
        ExportResource,
        Educate,
        PopulationGrowth,
        SendTax,
        KnowledgeDiffusion,
        DiscoverKnowledge,
        AutoCombat,
        UpdateModifierByProperTime,
    )
}
