package relativitization.game

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import org.jetbrains.kotlinx.dataframe.size
import relativitization.universe.game.GameUniverseInitializer
import java.io.File
import kotlin.test.Test

internal class GameSimulationKtTest {
    @Test
    fun singleDataFrameTest() {
        GameUniverseInitializer.initialize()

        val df = gameSingleRun(
            printStep = false,
            universeName = "Single dataframe test",
            xDim = 6,
            yDim = 6,
            zDim = 3,
            numStep = 5,
            numPlayer = 25,
        )

        assert(df.size().nrow == 5)
    }

    @Test
    fun multipleDataFrameTest() {
        GameUniverseInitializer.initialize()

        val dfList: MutableList<DataFrame<*>> = mutableListOf()

        val randomSeedList: List<Long> = (100L..102L).toList()

        for (randomSeed in randomSeedList) {
            dfList.add(
                gameSingleRun(
                    universeName = "Multiple dataframe test",
                    printStep = false,
                    randomSeed = randomSeed,
                    xDim = 6,
                    yDim = 6,
                    zDim = 3,
                    numStep = 5,
                    numPlayer = 25,
                )
            )
        }

        val df = dfList.concat()

        assert(df.size().nrow == 15)

        File("testData").mkdirs()
        df.writeCSV("./testData/dataframe-test.csv", CSVFormat.DEFAULT.withDelimiter('|'))

        val dfr = DataFrame.readCSV("./testData/dataframe-test.csv", delimiter = '|')

        assert(dfr.size().nrow == 15)
    }
}