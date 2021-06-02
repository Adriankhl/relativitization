package relativitization.universe.generate.fixed

import relativitization.universe.data.*
import relativitization.universe.data.physics.MutableInt4D
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.generate.GenerateSetting
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.maths.grid.Grids.create4DGrid

class Minimal : GenerateUniverse() {
    override fun generate(setting: GenerateSetting): UniverseData {
        val universeSettings: UniverseSettings = copy(setting.universeSettings)

        val data = MutableUniverseData4D(
            create4DGrid(
                universeSettings.tDim,
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _, _ -> mutableListOf() }
        )

        val universeState = UniverseState(
            currentTime = universeSettings.tDim - 1,
            maxPlayerId = 2,
        )

        val playerData1 = MutablePlayerData(1)
        val playerData2 = MutablePlayerData(2)

        playerData1.playerType = PlayerType.HUMAN
        playerData2.int4D = MutableInt4D(0, 0, 0, 1)

        // Add one stellar to each player
        playerData1.playerInternalData.popSystemicData.addRandomStellarSystem()
        playerData2.playerInternalData.popSystemicData.addRandomStellarSystem()

        // Add non zero energy
        playerData1.playerInternalData.physicsData.energy = 100.0
        playerData2.playerInternalData.physicsData.energy = 100.0

        data.addPlayerDataToLatest(playerData1, universeState.getCurrentTime())
        data.addPlayerDataToLatest(playerData2, universeState.getCurrentTime())


        return UniverseData(
            universeData4D = copy(data),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
        )
    }
}