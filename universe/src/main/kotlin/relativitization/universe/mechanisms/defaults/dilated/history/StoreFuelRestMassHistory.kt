package relativitization.universe.mechanisms.defaults.dilated.history

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutableAIData
import relativitization.universe.data.components.aiData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.mechanisms.Mechanism

object StoreFuelRestMassHistory : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
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