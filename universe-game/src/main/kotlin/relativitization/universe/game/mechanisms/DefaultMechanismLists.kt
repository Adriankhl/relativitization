package relativitization.universe.game.mechanisms

import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.core.mechanisms.MechanismLists
import relativitization.universe.game.mechanisms.defaults.dilated.combat.AutoCombat
import relativitization.universe.game.mechanisms.defaults.dilated.economy.ResourceDecay
import relativitization.universe.game.mechanisms.defaults.dilated.economy.UpdatePrice
import relativitization.universe.game.mechanisms.defaults.dilated.economy.UpdateResourceQualityBound
import relativitization.universe.game.mechanisms.defaults.dilated.history.StoreFuelRestMassHistory
import relativitization.universe.game.mechanisms.defaults.dilated.logistics.ExportResource
import relativitization.universe.game.mechanisms.defaults.dilated.logistics.SendTax
import relativitization.universe.game.mechanisms.defaults.dilated.military.UpdateMilitaryBase
import relativitization.universe.game.mechanisms.defaults.dilated.modifier.UpdateModifierByProperTime
import relativitization.universe.game.mechanisms.defaults.dilated.pop.Educate
import relativitization.universe.game.mechanisms.defaults.dilated.pop.Employment
import relativitization.universe.game.mechanisms.defaults.dilated.pop.Migration
import relativitization.universe.game.mechanisms.defaults.dilated.pop.PopBuyResource
import relativitization.universe.game.mechanisms.defaults.dilated.pop.PopulationGrowth
import relativitization.universe.game.mechanisms.defaults.dilated.pop.UpdateDesire
import relativitization.universe.game.mechanisms.defaults.dilated.pop.UpdateSatisfaction
import relativitization.universe.game.mechanisms.defaults.dilated.production.BaseStellarFuelProduction
import relativitization.universe.game.mechanisms.defaults.dilated.production.EntertainmentProduction
import relativitization.universe.game.mechanisms.defaults.dilated.production.FuelFactoryProduction
import relativitization.universe.game.mechanisms.defaults.dilated.production.ResourceFactoryProduction
import relativitization.universe.game.mechanisms.defaults.dilated.production.UpdateFactoryExperience
import relativitization.universe.game.mechanisms.defaults.dilated.research.DiscoverKnowledge
import relativitization.universe.game.mechanisms.defaults.dilated.research.KnowledgeDiffusion
import relativitization.universe.game.mechanisms.defaults.regular.ai.ClearRecentCommand
import relativitization.universe.game.mechanisms.defaults.regular.dead.ClearDeadPlayer
import relativitization.universe.game.mechanisms.defaults.regular.diplomacy.UpdateAlly
import relativitization.universe.game.mechanisms.defaults.regular.diplomacy.UpdateEnemy
import relativitization.universe.game.mechanisms.defaults.regular.diplomacy.UpdatePeacePlayer
import relativitization.universe.game.mechanisms.defaults.regular.diplomacy.UpdateRelation
import relativitization.universe.game.mechanisms.defaults.regular.diplomacy.UpdateWar
import relativitization.universe.game.mechanisms.defaults.regular.economy.UpdateTaxRate
import relativitization.universe.game.mechanisms.defaults.regular.events.AutoEventCollection
import relativitization.universe.game.mechanisms.defaults.regular.events.ProcessEvents
import relativitization.universe.game.mechanisms.defaults.regular.factory.ClearLocalFactoryStoredFuel
import relativitization.universe.game.mechanisms.defaults.regular.modifier.UpdateModifierByUniverseTime
import relativitization.universe.game.mechanisms.defaults.regular.movement.MoveToTarget
import relativitization.universe.game.mechanisms.defaults.regular.politics.MergePlayer
import relativitization.universe.game.mechanisms.defaults.regular.politics.UpdatePoliticsData
import relativitization.universe.game.mechanisms.defaults.regular.science.UpdateScienceApplicationData
import relativitization.universe.game.mechanisms.defaults.regular.storage.BalanceFuel
import relativitization.universe.game.mechanisms.defaults.regular.storage.BalanceResource
import relativitization.universe.game.mechanisms.defaults.regular.sync.SyncHierarchy
import relativitization.universe.game.mechanisms.defaults.regular.sync.SyncPlayerData
import relativitization.universe.game.mechanisms.defaults.regular.sync.SyncPlayerScienceData

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

    override fun name(): String = "Default"
}
