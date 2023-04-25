package relativitization.universe.game.mechanisms.defaults.regular.modifier

import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.data.components.modifierData
import relativitization.universe.game.data.global.UniverseGlobalData
import relativitization.universe.game.mechanisms.Mechanism
import kotlin.random.Random

object UpdateModifierByUniverseTime : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        mutablePlayerData.playerInternalData.modifierData().updateByUniverseTime()

        return listOf()
    }
}