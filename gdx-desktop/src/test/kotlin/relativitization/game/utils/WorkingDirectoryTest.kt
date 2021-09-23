package relativitization.game.utils

import java.io.File
import kotlin.test.Test

internal class WorkingDirectoryTest {
    @Test
    fun dirTest() {
        assert(WorkingDirectory.isValidAssetDir(File(".")))
        assert(!WorkingDirectory.isValidAssetDir(File("../resources")))
        WorkingDirectory.relativeAssetDir()
    }
}