package relativitization.universe.ai.emptyAI

import relativitization.universe.ai.AI
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.Command

class EmptyAI : AI() {
    override fun compute(playerData: PlayerData, universeData3DAtPlayer: UniverseData3DAtPlayer): List<Command> {
        return listOf()
    }
}