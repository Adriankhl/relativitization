package relativitization.universe.game.mechanisms.defaults.dilated.history

import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.data.components.MutableAIData
import relativitization.universe.game.data.components.aiData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.global.UniverseGlobalData
import relativitization.universe.game.data.serializer.DataSerializer
import relativitization.universe.game.mechanisms.Mechanism
import kotlin.random.Random

object StoreFuelRestMassHistory : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        val aiData: MutableAIData = mutablePlayerData.playerInternalData.aiData()

        aiData.fuelRestMassHistoryData.addHistory(
            DataSerializer.copy(
                mutablePlayerData.playerInternalData.physicsData().fuelRestMassData
            )
        )

        return listOf()
    }
}