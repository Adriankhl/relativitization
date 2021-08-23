package relativitization.universe.mechanisms.modifier

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.subsystem.science.UniverseScienceData
import relativitization.universe.maths.physics.Relativistic.gamma
import relativitization.universe.mechanisms.Mechanism

object UpdateModifierTime : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeScienceData: UniverseScienceData
    ): List<Command> {
        val gamma: Double = gamma(
            universeData3DAtPlayer.getCurrentPlayerData().velocity,
            universeData3DAtPlayer.universeSettings.speedOfLight
        )

        mutablePlayerData.playerInternalData.modifierData().updateModifierTime(gamma)

        return listOf()
    }
}