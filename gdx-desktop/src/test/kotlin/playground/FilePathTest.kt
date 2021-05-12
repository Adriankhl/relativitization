package playground

import java.io.File
import kotlin.test.Test


internal class FilePathTest {
    @Test
    fun walkTest() {
        File(".").walkTopDown().forEach { println(it) }
    }
}