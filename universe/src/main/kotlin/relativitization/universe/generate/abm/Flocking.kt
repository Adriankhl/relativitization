package relativitization.universe.generate.abm

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.MutableUniverseData4D
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.UniverseState
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.GenerateSetting
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.maths.grid.Grids.create4DGrid
import kotlin.random.Random

class Flocking : GenerateUniverse() {
    override fun generate(setting: GenerateSetting): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(setting.universeSettings)

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
            maxPlayerId = setting.numPlayer,
        )

        for (i in 1..setting.numPlayer) {
            val playerData = MutablePlayerData(id = i)
            playerData.int4D.x = Random.Default.nextInt(0, universeSettings.xDim)
            playerData.int4D.y = Random.Default.nextInt(0, universeSettings.yDim)
            playerData.int4D.z = Random.Default.nextInt(0, universeSettings.zDim)

            playerData.playerInternalData.physicsData.energy = 1e6
            playerData.playerInternalData.physicsData.moveMaxPower = 1.0

            playerData.playerInternalData.physicsData.velocity.vx = Random.Default.nextDouble(0.0, universeSettings.speedOfLight)
            playerData.playerInternalData.physicsData.velocity.vy = Random.Default.nextDouble(0.0, universeSettings.speedOfLight)
            playerData.playerInternalData.physicsData.velocity.vz = Random.Default.nextDouble(0.0, universeSettings.speedOfLight)

            data.addPlayerDataToLatest(playerData, universeState.getCurrentTime())
        }

        return UniverseData(
            universeData4D = DataSerializer.copy(data),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
        )
    }
}