package relativitization.universe.mechanisms.default.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object UpdateWarState : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        // Parameters
        val peaceTreatyLength: Int = 15

        val toChangeSet: Set<Int> =
            mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.filter { (id, warState) ->
                val otherHasWarState: Boolean =
                    universeData3DAtPlayer.get(id).playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                        mutablePlayerData.playerId
                    )
                val otherHasProposePeace: Boolean = if (otherHasWarState) {
                    universeData3DAtPlayer.get(id).playerInternalData.diplomacyData().warData.warStateMap.getValue(
                        id
                    ).proposePeace
                } else {
                    true
                }
                (warState.proposePeace) && (!otherHasWarState || otherHasProposePeace)
            }.keys

        toChangeSet.forEach {
            mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.remove(it)
            mutablePlayerData.playerInternalData.modifierData().diplomacyModifierData.setPeaceTreatyWithLength(
                it,
                peaceTreatyLength
            )
        }

        return listOf()
    }
}