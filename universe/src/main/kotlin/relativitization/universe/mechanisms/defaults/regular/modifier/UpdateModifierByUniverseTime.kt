package relativitization.universe.mechanisms.defaults.regular.modifier

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.modifierData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic.gamma
import relativitization.universe.mechanisms.Mechanism

object UpdateModifierByUniverseTime : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        mutablePlayerData.playerInternalData.modifierData().updateByUniverseTime()

        return listOf()
    }
}