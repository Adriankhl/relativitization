package relativitization.universe.generate.abm

import relativitization.universe.data.*
import relativitization.universe.data.subsystem.physics.MutableVelocity
import relativitization.universe.data.subsystem.science.UniverseScienceData
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
            val playerData = MutablePlayerData(playerId = i)

            // Set human player for small i
            if (i <= settings.numHumanPlayer) {
                playerData.playerType = PlayerType.HUMAN
            }

            playerData.int4D.x = Random.Default.nextInt(0, universeSettings.xDim)
            playerData.int4D.y = Random.Default.nextInt(0, universeSettings.yDim)
            playerData.int4D.z = Random.Default.nextInt(0, universeSettings.zDim)

            playerData.playerInternalData.popSystemicData.addSpaceShip(
                1.0, 1e6, 1e5
            )


            val vx = Random.Default.nextDouble(-1.0, 1.0)
            val vy = Random.Default.nextDouble(-1.0, 1.0)
            val vz = Random.Default.nextDouble(-1.0, 1.0)

            // Constant velocity 0.5
            playerData.velocity = MutableVelocity(vx, vy, vz).scaleVelocity(0.5)

            // Use flocking ai
            playerData.playerInternalData.aiData.aiName = "FlockingAI"

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
            universeScienceData = UniverseScienceData()
        )
    }
}