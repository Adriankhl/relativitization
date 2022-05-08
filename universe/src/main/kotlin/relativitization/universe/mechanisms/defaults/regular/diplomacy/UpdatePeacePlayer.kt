package relativitization.universe.mechanisms.defaults.regular.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.components.modifierData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
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

        val peacePlayerSet: Set<Int> = allPeaceTreaty.flatMap {
            universeData3DAtPlayer.get(it).getSubordinateAndSelfIdSet()
        }.toSet()

        mutablePlayerData.playerInternalData.diplomacyData().peacePlayerIdSet.clear()
        mutablePlayerData.playerInternalData.diplomacyData().peacePlayerIdSet.addAll(peacePlayerSet)

        return listOf()
    }
}