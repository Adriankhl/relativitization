package relativitization.universe.generate.fixed

import relativitization.universe.data.*
import relativitization.universe.data.physics.MutableInt4D
import relativitization.universe.data.serializer.DataSerializer.copy
import relativitization.universe.generate.GenerateSetting
import relativitization.universe.generate.GeneratedUniverse
import relativitization.universe.maths.grid.Grids.create4DGrid

class Minimal : GeneratedUniverse() {
    override fun generate(setting: GenerateSetting): UniverseData {
        val universeSettings = generateUniverseSettings(setting)

        val data = MutableUniverseData4D(
            create4DGrid(
                universeSettings.tDim,
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _, _ -> mutableListOf()}
        )
        val playerData1 = MutablePlayerData(0)
        val playerData2 = MutablePlayerData(1)

        playerData1.playerType = PlayerType.HUMAN
        playerData2.int4D = MutableInt4D(0, 0, 0, 1)

        playerData1.playerInternalData.popSystemicData.addRandomStellarSystem()
        playerData2.playerInternalData.popSystemicData.addRandomStellarSystem()

        data.addPlayerDataToLatest(playerData1, universeSettings.tDim - 1)
        data.addPlayerDataToLatest(playerData2, universeSettings.tDim - 1)

        val universeState = UniverseState(universeSettings.tDim - 1)

        return UniverseData(
            universeData4D = copy(data),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
        )
    }
}