package relativitization.universe.game.mechanisms.defaults.regular.diplomacy

import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.data.components.modifierData
import relativitization.universe.game.data.global.UniverseGlobalData
import relativitization.universe.game.mechanisms.Mechanism
import kotlin.random.Random

object UpdatePeacePlayer : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        val allPeaceTreaty: Set<Int> = mutablePlayerData.playerInternalData.modifierData()
            .diplomacyModifierData.peaceTreaty.keys

        val peacePlayerSet: Set<Int> = allPeaceTreaty.filter {
            universeData3DAtPlayer.playerDataMap.containsKey(it)
        }.flatMap {
            universeData3DAtPlayer.get(it).getSubordinateAndSelfIdSet()
        }.toSet()

        mutablePlayerData.playerInternalData.diplomacyData().peacePlayerIdSet.clear()
        mutablePlayerData.playerInternalData.diplomacyData().peacePlayerIdSet.addAll(peacePlayerSet)

        return listOf()
    }
}