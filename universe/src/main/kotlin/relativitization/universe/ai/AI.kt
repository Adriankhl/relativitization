package relativitization.universe.ai

import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command

abstract class AI {
    abstract fun compute(playerData: PlayerData, universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command>
}