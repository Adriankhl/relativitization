package relativitization.universe.mechanisms.defaults.regular.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object UpdateDiplomaticRelation : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        // Parameters
        // Max relation change by receiving fuel
        val maxReceiveFuelChange: Double = 100.0

        val modifierIdSet: Set<Int> =
            mutablePlayerData.playerInternalData.modifierData().diplomacyModifierData.relationModifierMap.keys

        mutablePlayerData.playerInternalData.diplomacyData().relationMap.forEach { (id, relationData) ->
            relationData.relation = 0.0
            if (modifierIdSet.contains(id)) {
                relationData.relation +=
                    mutablePlayerData.playerInternalData.modifierData().diplomacyModifierData.getRelationChange(
                        id = id,
                        maxReceiveFuelChange = maxReceiveFuelChange,
                    )
            }
        }

        return listOf()
    }
}