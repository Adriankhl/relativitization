package relativitization.universe.mechanisms.default.modifier

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic.gamma
import relativitization.universe.mechanisms.Mechanism

object UpdateModifierTime : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        val gamma: Double = gamma(
            universeData3DAtPlayer.getCurrentPlayerData().velocity,
            universeData3DAtPlayer.universeSettings.speedOfLight
        )

        mutablePlayerData.playerInternalData.modifierData().updateModifierTime(gamma)

        return listOf()
    }
}