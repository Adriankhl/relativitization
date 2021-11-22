package relativitization.universe.mechanisms

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
import relativitization.universe.mechanisms.default.sync.SyncPlayerData

object DefaultMechanismList : MechanismList() {
    override val mechanismList: List<Mechanism> = listOf(
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
        SyncTaxRate,
        SyncPlayerScienceData,
        UpdateScienceApplicationData,
        UpdateModifierTime,
    )
}
