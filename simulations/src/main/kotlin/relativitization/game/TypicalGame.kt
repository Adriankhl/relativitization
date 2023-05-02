package relativitization.game

import relativitization.universe.game.GameUniverseInitializer

fun main() {
    GameUniverseInitializer.initialize()

    gameSingleRun(
        printStep = true,
        universeName = "Typical game",
        numPlayer = 50,
    )
}