package relativitization.universe

import org.junit.jupiter.api.Test
import java.io.File

internal class FileTest {
    /**
     * Ensure the model-gitignore.txt contains all the file
     */
    @Test
    fun modelGitignoreTest() {
        if (File("../model-gitignore.txt").isFile) {
            val allFileList: List<String> = File("..").walkTopDown().map {
                it.toRelativeString(File(".."))
            }.filter {
                it.matches(Regex("^(gradle.*|.*kts?)$"))
            }.toList()

            val modelGitignore: List<String> = File("../model-gitignore.txt").readLines()

            println(allFileList.filter { !modelGitignore.contains(it) })
        }
    }
}