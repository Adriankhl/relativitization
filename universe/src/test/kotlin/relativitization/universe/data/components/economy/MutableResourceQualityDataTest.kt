package relativitization.universe.data.components.economy

import relativitization.universe.data.components.default.economy.MutableResourceQualityData
import kotlin.test.Test

internal class MutableResourceQualityDataTest {
    @Test
    fun changeToTest() {
        val r1: MutableResourceQualityData = MutableResourceQualityData(
            1.0,
            1.0,
            1.0
        )

        val r2: MutableResourceQualityData = MutableResourceQualityData(
            1.5,
            1.1,
            0.4
        )

        val r3: MutableResourceQualityData = MutableResourceQualityData(
            10.0,
            -2.0,
            1.0,
        )

        val nr1 = r1.changeTo(
            other = r2,
            changeFactor = 0.1,
            minChange = 0.2,
        )

        val nr2 = r1.changeTo(
            other = r3,
            changeFactor = 0.1,
            minChange = 0.2,
        )

        assert(nr1 == MutableResourceQualityData(1.2, 1.1, 0.8))
        assert(nr2 == MutableResourceQualityData(1.9, 0.7, 1.0))
    }
}