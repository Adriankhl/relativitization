package relativitization.game

import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.commands.DefaultCommandAvailability
import relativitization.universe.data.commands.name
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.random.RandomOneStarPerPlayerGenerate
import relativitization.universe.global.DefaultGlobalMechanismList
import relativitization.universe.global.name
import relativitization.universe.maths.number.Notation
import relativitization.universe.maths.number.toScientificNotation
import relativitization.universe.mechanisms.DefaultMechanismLists
import relativitization.universe.mechanisms.name

fun main() {
    val generateSetting = GenerateSettings(
        generateMethod = RandomOneStarPerPlayerGenerate.name(),
        numPlayer = 25,
        numHumanPlayer = 1,
        otherIntMap = mutableMapOf("numExtraStellarSystem" to 0),
        otherDoubleMap = mutableMapOf("initialPopulation" to 1E6),
        universeSettings = MutableUniverseSettings(
            universeName = "Smaller typical game",
            commandCollectionName = DefaultCommandAvailability.name(),
            mechanismCollectionName = DefaultMechanismLists.name(),
            globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
            speedOfLight = 1.0,
            xDim = 6,
            yDim = 6,
            zDim = 3,
        )
    )

    val universe = Universe(GenerateUniverseMethodCollection.generate(generateSetting))

    for (turn in 1..1000) {

        universe.pureAIStep()

        val gameStatus: GameStatus = GameStatus.compute(universe)

        println(
            "Turn: $turn. Player: ${universe.availablePlayers().size}. " +
                    "Dead: ${universe.getDeadIdList().size}. " +
                    "Carrier: ${gameStatus.numCarrier}. " +
                    "Population: ${gameStatus.totalPopulation.toScientificNotation().toDouble(2)}. "  +
                    "Fuel: ${gameStatus.totalFuelRestMass.toScientificNotation().toDouble(2)}. " +
                    "Saving: ${gameStatus.totalSaving.toScientificNotation().toDouble(2)}"
        )
    }
}