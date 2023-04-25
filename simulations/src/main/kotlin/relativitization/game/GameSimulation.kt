package relativitization.game

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import relativitization.universe.game.Universe
import relativitization.universe.game.data.MutableUniverseSettings
import relativitization.universe.game.data.PlayerData
import relativitization.universe.game.data.commands.DefaultCommandAvailability
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.generate.GenerateSettings
import relativitization.universe.game.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.generate.random.RandomOneStarPerPlayerGenerate
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.maths.number.toScientificNotation
import relativitization.universe.game.mechanisms.DefaultMechanismLists

internal fun gameSingleRun(
    universeName: String = "Game",
    printStep: Boolean = false,
    randomSeed: Long = 100L,
    xDim: Int = 10,
    yDim: Int = 10,
    zDim: Int = 3,
    numStep: Int = 1000,
    numPlayer: Int = 0,
    numExtraStellarSystem: Int = 0,
    initialPopulation: Double = 1E6,
): DataFrame<*> {
    // This map will be converted to dataframe
    val dfMap: MutableMap<String, MutableList<Any>> = mutableMapOf()

    val generateSetting = GenerateSettings(
        generateMethod = RandomOneStarPerPlayerGenerate.name(),
        numPlayer = numPlayer,
        numHumanPlayer = 1,
        otherIntMap = mutableMapOf("numExtraStellarSystem" to numExtraStellarSystem),
        otherDoubleMap = mutableMapOf("initialPopulation" to initialPopulation),
        universeSettings = MutableUniverseSettings(
            universeName = universeName,
            commandCollectionName = DefaultCommandAvailability.name(),
            mechanismCollectionName = DefaultMechanismLists.name(),
            globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
            speedOfLight = 1.0,
            xDim = xDim,
            yDim = yDim,
            zDim = zDim,
            randomSeed = randomSeed,
        )
    )

    val universe = Universe(GenerateUniverseMethodCollection.generate(generateSetting))

    for (turn in 1..numStep) {
        val currentPlayerDataList: List<PlayerData> = universe.getCurrentPlayerDataList()

        val numCarrier: Int = currentPlayerDataList.fold(0) { acc, playerData ->
            acc + playerData.playerInternalData.popSystemData().numCarrier()
        }

        val population: Double = currentPlayerDataList.fold(0.0) { acc, playerData ->
            acc + playerData.playerInternalData.popSystemData().totalAdultPopulation()
        }

        val fuelRestMass: Double = currentPlayerDataList.fold(0.0) { acc, playerData ->
            acc + playerData.playerInternalData.physicsData().fuelRestMassData.total()
        }

        val fuelProduction: Double = currentPlayerDataList.fold(0.0) { acc, playerData ->
            acc + playerData.playerInternalData.popSystemData().carrierDataMap.values
                .sumOf { carrier ->
                    carrier.allPopData.labourerPopData.fuelFactoryMap.values.sumOf { fuelFactory ->
                        fuelFactory.lastOutputAmount
                    }
                }
        }

        val saving: Double = currentPlayerDataList.fold(0.0) { acc, playerData ->
            acc + playerData.playerInternalData.popSystemData().totalSaving()
        }

        val totalSatisfaction: Double = currentPlayerDataList.fold(0.0) { acc, playerData ->
            acc + playerData.playerInternalData.popSystemData().totalSatisfaction()
        }

        val numAlliance: Int = currentPlayerDataList.fold(0) { acc, playerData ->
            acc + playerData.playerInternalData.diplomacyData().relationData.allyMap.size
        }

        val outputDataMap = mapOf(
            "randomSeed" to randomSeed,
            "turn" to turn,
            "numPlayer" to universe.availablePlayers().size,
            "dead" to universe.getDeadIdList().size,
            "numCarrier" to numCarrier,
            "population" to population,
            "fuelRestMass" to fuelRestMass,
            "fuelProduction" to fuelProduction,
            "saving" to saving,
            "averageSatisfaction" to totalSatisfaction / population,
            "numAlliance" to numAlliance,
        )

        outputDataMap.forEach {
            dfMap.getOrPut(it.key) {
                mutableListOf()
            }.add(it.value)
        }

        if (printStep) {
            outputDataMap.forEach {
                val value = it.value
                if (value is Double) {
                    print("${it.key}: ${value.toScientificNotation().toDouble(2)}, ")
                } else {
                    print("${it.key}: $value, ")
                }
            }
            print("\b\b.\n")
        }

        universe.pureAIStep()
    }

    return dfMap.toDataFrame()
}