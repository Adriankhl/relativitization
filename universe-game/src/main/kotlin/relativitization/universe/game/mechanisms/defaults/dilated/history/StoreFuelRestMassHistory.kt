package relativitization.universe.game.mechanisms.defaults.dilated.history

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.game.data.components.MutableAIData
import relativitization.universe.game.data.components.aiData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.mechanisms.Mechanism
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