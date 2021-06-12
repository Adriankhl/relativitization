package relativitization.universe.mechanisms.state

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.maths.physics.Relativistic.gamma
import relativitization.universe.mechanisms.Mechanism

object UpdateTemporaryState : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeData: UniverseData
    ): List<Command> {
        val gamma: Double = gamma(
            universeData3DAtPlayer.getCurrentPlayerData().velocity,
            universeData3DAtPlayer.universeSettings.speedOfLight
        )

        mutablePlayerData.playerInternalData.playerState.temporaryState.updateTimeRemain(gamma)

        return listOf()
    }
}