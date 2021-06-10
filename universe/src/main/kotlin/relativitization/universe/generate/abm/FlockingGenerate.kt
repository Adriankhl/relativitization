package relativitization.universe.generate.abm

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.MutableUniverseData4D
import relativitization.universe.data.PlayerType
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.UniverseState
import relativitization.universe.data.physics.MutableVelocity
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverse
import relativitization.universe.maths.grid.Grids.create4DGrid
import kotlin.random.Random

class FlockingGenerate : GenerateUniverse() {
    override fun generate(settings: GenerateSettings): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(settings.universeSettings)

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
            maxPlayerId = settings.numPlayer,
        )

        for (i in 1..settings.numPlayer) {
            val playerData = MutablePlayerData(id = i)

            // Set human player for small i
            if (i <= settings.numHumanPlayer) {
                playerData.playerType = PlayerType.HUMAN
            }

            playerData.int4D.x = Random.Default.nextInt(0, universeSettings.xDim)
            playerData.int4D.y = Random.Default.nextInt(0, universeSettings.yDim)
            playerData.int4D.z = Random.Default.nextInt(0, universeSettings.zDim)

            playerData.playerInternalData.physicsData.fuelRestMass = 1e6
            playerData.playerInternalData.physicsData.maxDeltaFuelRestMass = 1.0


            val vx = Random.Default.nextDouble(-1.0, 1.0)
            val vy = Random.Default.nextDouble(-1.0, 1.0)
            val vz = Random.Default.nextDouble(-1.0, 1.0)

            // Constant velocity 0.5
            playerData.velocity = MutableVelocity(vx, vy, vz).scaleVelocity(0.5)

            // Use flocking ai
            playerData.playerInternalData.playerState.aiState.aiName = "FlockingAI"

            data.addPlayerDataToLatestWithAfterImage(
                playerData,
                universeState.getCurrentTime(),
                universeSettings.groupEdgeLength,
                universeSettings.playerAfterImageDuration
            )
        }

        return UniverseData(
            universeData4D = DataSerializer.copy(data),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
        )
    }
}