package relativitization.game

import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.commands.DefaultCommandAvailability
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.components.physicsData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.random.RandomOneStarPerPlayerGenerate
import relativitization.universe.global.DefaultGlobalMechanismList
import relativitization.universe.maths.number.toScientificNotation
import relativitization.universe.mechanisms.DefaultMechanismLists

fun main() {
    val generateSetting = GenerateSettings(
        generateMethod = RandomOneStarPerPlayerGenerate.name(),
        numPlayer = 50,
        numHumanPlayer = 1,
        otherIntMap = mutableMapOf("numExtraStellarSystem" to 0),
        otherDoubleMap = mutableMapOf("initialPopulation" to 1E6),
        universeSettings = MutableUniverseSettings(
            universeName = "Typical game",
            commandCollectionName = DefaultCommandAvailability.name(),
            mechanismCollectionName = DefaultMechanismLists.name(),
            globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
            speedOfLight = 1.0,
            xDim = 10,
            yDim = 10,
            zDim = 3,
        )
    )

    val universe = Universe(GenerateUniverseMethodCollection.generate(generateSetting))

    for (turn in 1..1000) {
        universe.pureAIStep()

        val gameStatus: GameStatus = GameStatus.compute(universe)

        val averageSatisfaction: Double = gameStatus.totalSatisfaction / gameStatus.totalPopulation

        println(
            "Turn: $turn. Player: ${universe.availablePlayers().size}. " +
                    "Dead: ${universe.getDeadIdList().size}. " +
                    "Carrier: ${gameStatus.numCarrier}. " +
                    "Population: ${gameStatus.totalPopulation.toScientificNotation().toString(2)}. "  +
                    "Fuel: ${gameStatus.totalFuelRestMass.toScientificNotation().toString(2)}. " +
                    "Production: ${gameStatus.totalFuelProduction.toScientificNotation().toString(2)}. " +
                    "Saving: ${gameStatus.totalSaving.toScientificNotation().toString(2)}. " +
                    "Satisfaction: ${averageSatisfaction.toScientificNotation().toString(2)}. " +
                    "Alliance: ${gameStatus.totalNumAlly}, "
        )
    }
}

internal class GameStatus(
    val numCarrier: Int = 0,
    val totalPopulation: Double = 0.0,
    val totalFuelRestMass: Double = 0.0,
    val totalFuelProduction: Double = 0.0,
    val totalSaving: Double = 0.0,
    val totalSatisfaction: Double= 0.0,
    val totalNumAlly: Int = 0,
) {
    companion object {
        fun compute(universe: Universe): GameStatus {
            return universe.availablePlayers().fold(GameStatus()) { status, id ->
                val universeDataAtPlayer: UniverseData3DAtPlayer =
                    universe.getUniverse3DViewAtPlayer(id)

                val numLocalCarrier: Int = universeDataAtPlayer.getCurrentPlayerData()
                    .playerInternalData.popSystemData().numCarrier()

                val localPopulation: Double = universeDataAtPlayer.getCurrentPlayerData()
                    .playerInternalData.popSystemData().totalAdultPopulation()

                val localFuelRestMass: Double = universeDataAtPlayer.getCurrentPlayerData()
                    .playerInternalData.physicsData().fuelRestMassData.total()

                val localFuelProduction: Double = universeDataAtPlayer.getCurrentPlayerData()
                    .playerInternalData.popSystemData().carrierDataMap.values.sumOf { carrier ->
                        carrier.allPopData.labourerPopData.fuelFactoryMap.values.sumOf { factory ->
                            factory.lastOutputAmount
                        }
                    }

                val localSaving: Double = universeDataAtPlayer.getCurrentPlayerData()
                    .playerInternalData.popSystemData().totalSaving()

                val localSatisfaction: Double = universeDataAtPlayer.getCurrentPlayerData()
                    .playerInternalData.popSystemData().totalSatisfaction()

                val localNumAlly: Int = universeDataAtPlayer.getCurrentPlayerData()
                    .playerInternalData.diplomacyData().relationData.allyMap.size

                val newStatus = GameStatus(
                    numCarrier = status.numCarrier + numLocalCarrier,
                    totalPopulation = status.totalPopulation + localPopulation,
                    totalFuelRestMass = status.totalFuelRestMass + localFuelRestMass,
                    totalFuelProduction = status.totalFuelProduction + localFuelProduction,
                    totalSaving = status.totalSaving  + localSaving,
                    totalSatisfaction = status.totalSatisfaction + localSatisfaction,
                    totalNumAlly = status.totalNumAlly + localNumAlly,
                )

                newStatus
            }
        }
    }
}
