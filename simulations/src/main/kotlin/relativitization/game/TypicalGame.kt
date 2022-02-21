package relativitization.game

import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.DefaultCommandAvailability
import relativitization.universe.data.commands.name
import relativitization.universe.data.components.popSystemData
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.random.RandomOneStarPerPlayerGenerate
import relativitization.universe.global.DefaultGlobalMechanismList
import relativitization.universe.global.name
import relativitization.universe.mechanisms.DefaultMechanismLists
import relativitization.universe.mechanisms.name

fun main() {
    val generateSetting = GenerateSettings(
        generateMethod = RandomOneStarPerPlayerGenerate.name(),
        numPlayer = 50,
        numHumanPlayer = 1,
        numExtraStellarSystem = 0,
        initialPopulation = 1E6,
        universeSettings = MutableUniverseSettings(
            universeName = "Typical game",
            commandCollectionName = DefaultCommandAvailability.name(),
            mechanismCollectionName = DefaultMechanismLists.name(),
            globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
            speedOfLight = 1.0,
            xDim = 10,
            yDim = 10,
            zDim = 3,
            tDim = 16,
            playerAfterImageDuration = 4,
            playerHistoricalInt4DLength = 4,
            groupEdgeLength = 0.01,
            otherSettings = mutableMapOf(),
        )
    )

    val universe = Universe(GenerateUniverseMethodCollection.generate(generateSetting), ".")

    for (turn in 1..1000) {
        universe.pureAIStep()

        val gameStatus: GameStatus = GameStatus.compute(universe)

        println(
            "Turn: $turn. Player: ${universe.availablePlayers().size}. Dead: ${universe.getDeadIdList().size}. " +
                    "Carrier: ${gameStatus.numCarrier}. Population: ${gameStatus.totalPopulation}"
        )
    }
}

internal class GameStatus(
    val numCarrier: Int,
    val totalPopulation: Double,
) {
    companion object {
        fun compute(universe: Universe): GameStatus {
            val gameStatus = GameStatus(0, 0.0)
            return universe.availablePlayers().fold(gameStatus) { status, id ->
                val universeDataAtPlayer: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(id)

                val numLocalCarrier: Int = universeDataAtPlayer.getCurrentPlayerData().playerInternalData
                    .popSystemData().numCarrier()

                val localPopulation: Double = universeDataAtPlayer.getCurrentPlayerData().playerInternalData
                    .popSystemData().totalAdultPopulation()

                val newStatus = GameStatus(
                    numCarrier = status.numCarrier + numLocalCarrier,
                    totalPopulation = status.totalPopulation + localPopulation
                )

                newStatus
            }
        }
    }
}
