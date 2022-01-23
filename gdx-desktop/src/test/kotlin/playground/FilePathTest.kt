package playground

import java.io.File
import kotlin.test.Ignore
import kotlin.test.Test


internal class FilePathTest {
    @Ignore
    fun walkTest() {
        File(".").walkTopDown().forEach { println(it) }
    }
}