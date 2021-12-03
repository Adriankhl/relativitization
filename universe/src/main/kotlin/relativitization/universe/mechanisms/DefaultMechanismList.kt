package relativitization.universe.mechanisms

import relativitization.universe.mechanisms.defaults.combat.AutoCombat
import relativitization.universe.mechanisms.defaults.dead.ClearDeadPlayer
import relativitization.universe.mechanisms.defaults.diplomacy.UpdateDiplomaticRelation
import relativitization.universe.mechanisms.defaults.diplomacy.UpdateDiplomaticRelationState
import relativitization.universe.mechanisms.defaults.diplomacy.UpdateWarState
import relativitization.universe.mechanisms.defaults.economy.UpdateTaxRate
import relativitization.universe.mechanisms.defaults.events.AutoEventCollection
import relativitization.universe.mechanisms.defaults.events.ProcessEvents
import relativitization.universe.mechanisms.defaults.logistics.ExportResource
import relativitization.universe.mechanisms.defaults.logistics.SendTax
import relativitization.universe.mechanisms.defaults.military.UpdateMilitaryBase
import relativitization.universe.mechanisms.defaults.modifier.UpdateModifierTime
import relativitization.universe.mechanisms.defaults.politics.MergePlayer
import relativitization.universe.mechanisms.defaults.politics.UpdatePoliticsData
import relativitization.universe.mechanisms.defaults.pop.*
import relativitization.universe.mechanisms.defaults.production.BaseStellarFuelProduction
import relativitization.universe.mechanisms.defaults.production.EntertainmentProduction
import relativitization.universe.mechanisms.defaults.production.FuelFactoryProduction
import relativitization.universe.mechanisms.defaults.production.ResourceFactoryProduction
import relativitization.universe.mechanisms.defaults.research.DiscoverKnowledge
import relativitization.universe.mechanisms.defaults.research.KnowledgeDiffusion
import relativitization.universe.mechanisms.defaults.science.UpdateScienceApplicationData
import relativitization.universe.mechanisms.defaults.sync.SyncHierarchy
import relativitization.universe.mechanisms.defaults.sync.SyncPlayerData
import relativitization.universe.mechanisms.defaults.sync.SyncPlayerScienceData

object DefaultMechanismList : MechanismList() {
    override val regularMechanismList: List<Mechanism> = listOf(
        SyncPlayerData,
        ClearDeadPlayer,
        AutoEventCollection,
        ProcessEvents,
        UpdateWarState,
        UpdateDiplomaticRelation,
        UpdateDiplomaticRelationState,
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
        SyncHierarchy,
        AutoCombat,
        MergePlayer,
        UpdateTaxRate,
        UpdatePoliticsData,
        SyncPlayerScienceData,
        UpdateScienceApplicationData,
        UpdateModifierTime,
    )

    override val dilatedMechanismList: List<Mechanism> = listOf()
}
