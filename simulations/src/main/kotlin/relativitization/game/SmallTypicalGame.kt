package relativitization.game

import relativitization.universe.game.GameUniverseInitializer

fun main() {
    GameUniverseInitializer.initialize()

    gameSingleRun(
        printStep = true,
        universeName = "Small typical game",
        xDim = 6,
        yDim = 6,
        zDim = 3,
        numPlayer = 25,
    )
}