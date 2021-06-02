package relativitization.universe.generate.abm

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.MutableUniverseData4D
import relativitization.universe.data.PlayerType
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.UniverseState
import relativitization.universe.data.physics.MutableVelocity
import relativitization.universe.data.physics.Velocity
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.GenerateSetting
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.maths.grid.Grids.create4DGrid
import kotlin.random.Random

class FlockingGenerate : GenerateUniverse() {
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

            // Set human player for small i
            if (i <= setting.numHumanPlayer) {
                playerData.playerType = PlayerType.HUMAN
            }

            playerData.int4D.x = Random.Default.nextInt(0, universeSettings.xDim)
            playerData.int4D.y = Random.Default.nextInt(0, universeSettings.yDim)
            playerData.int4D.z = Random.Default.nextInt(0, universeSettings.zDim)

            playerData.playerInternalData.physicsData.energy = 1e6
            playerData.playerInternalData.physicsData.moveMaxPower = 1.0


            val vx = Random.Default.nextDouble(0.0, 1.0)
            val vy = Random.Default.nextDouble(0.0, 1.0)
            val vz = Random.Default.nextDouble(0.0, 1.0)

            // Constant velocity 0.5
            playerData.playerInternalData.physicsData.velocity = MutableVelocity(vx, vy, vz).scaleVelocity(0.5)

            // Use flocking ai
            playerData.playerInternalData.playerState.aiState.aiName = "FlockingAI"

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