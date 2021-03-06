package relativitization.universe.mechanisms

import relativitization.universe.mechanisms.defaults.dilated.modifier.UpdateModifierByProperTime
import relativitization.universe.mechanisms.defaults.dilated.combat.AutoCombat
import relativitization.universe.mechanisms.defaults.dilated.economy.ResourceDecay
import relativitization.universe.mechanisms.defaults.dilated.economy.UpdatePrice
import relativitization.universe.mechanisms.defaults.dilated.economy.UpdateResourceQualityBound
import relativitization.universe.mechanisms.defaults.dilated.history.StoreFuelRestMassHistory
import relativitization.universe.mechanisms.defaults.regular.dead.ClearDeadPlayer
import relativitization.universe.mechanisms.defaults.regular.diplomacy.UpdateRelation
import relativitization.universe.mechanisms.defaults.regular.diplomacy.UpdateEnemy
import relativitization.universe.mechanisms.defaults.regular.diplomacy.UpdateWar
import relativitization.universe.mechanisms.defaults.regular.economy.UpdateTaxRate
import relativitization.universe.mechanisms.defaults.regular.events.AutoEventCollection
import relativitization.universe.mechanisms.defaults.regular.events.ProcessEvents
import relativitization.universe.mechanisms.defaults.dilated.logistics.ExportResource
import relativitization.universe.mechanisms.defaults.dilated.logistics.SendTax
import relativitization.universe.mechanisms.defaults.dilated.military.UpdateMilitaryBase
import relativitization.universe.mechanisms.defaults.regular.modifier.UpdateModifierByUniverseTime
import relativitization.universe.mechanisms.defaults.regular.politics.MergePlayer
import relativitization.universe.mechanisms.defaults.regular.politics.UpdatePoliticsData
import relativitization.universe.mechanisms.defaults.dilated.pop.*
import relativitization.universe.mechanisms.defaults.dilated.production.*
import relativitization.universe.mechanisms.defaults.dilated.research.DiscoverKnowledge
import relativitization.universe.mechanisms.defaults.dilated.research.KnowledgeDiffusion
import relativitization.universe.mechanisms.defaults.regular.ai.ClearRecentCommand
import relativitization.universe.mechanisms.defaults.regular.diplomacy.UpdateAlly
import relativitization.universe.mechanisms.defaults.regular.factory.ClearLocalFactoryStoredFuel
import relativitization.universe.mechanisms.defaults.regular.diplomacy.UpdatePeacePlayer
import relativitization.universe.mechanisms.defaults.regular.movement.MoveToTarget
import relativitization.universe.mechanisms.defaults.regular.science.UpdateScienceApplicationData
import relativitization.universe.mechanisms.defaults.regular.storage.BalanceFuel
import relativitization.universe.mechanisms.defaults.regular.storage.BalanceResource
import relativitization.universe.mechanisms.defaults.regular.sync.SyncHierarchy
import relativitization.universe.mechanisms.defaults.regular.sync.SyncPlayerData
import relativitization.universe.mechanisms.defaults.regular.sync.SyncPlayerScienceData

object DefaultMechanismLists : MechanismLists() {
    override val regularMechanismList: List<Mechanism> = listOf(
        ClearRecentCommand,
        SyncPlayerData,
        ClearDeadPlayer,
        SyncHierarchy,
        AutoEventCollection,
        ProcessEvents,
        ClearLocalFactoryStoredFuel,
        BalanceFuel,
        BalanceResource,
        MoveToTarget,
        UpdateWar,
        UpdatePeacePlayer,
        UpdateRelation,
        UpdateEnemy,
        UpdateAlly,
        MergePlayer,
        UpdateTaxRate,
        UpdatePoliticsData,
        SyncPlayerScienceData,
        UpdateScienceApplicationData,
        UpdateModifierByUniverseTime,
        SyncPlayerData,
    )

    override val dilatedMechanismList: List<Mechanism> = listOf(
        Employment,
        PopBuyResource,
        UpdateDesire,
        UpdateSatisfaction,
        UpdateMilitaryBase,
        BaseStellarFuelProduction,
        FuelFactoryProduction,
        ResourceFactoryProduction,
        UpdateFactoryExperience,
        EntertainmentProduction,
        ExportResource,
        UpdatePrice,
        UpdateResourceQualityBound,
        Educate,
        PopulationGrowth,
        Migration,
        SendTax,
        ResourceDecay,
        KnowledgeDiffusion,
        DiscoverKnowledge,
        AutoCombat,
        UpdateModifierByProperTime,
        StoreFuelRestMassHistory,
        SyncPlayerData,
    )
}
