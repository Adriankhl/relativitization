package relativitization.universe.utils

import java.io.File

object FileUtils {
    fun mkdirs(path: String) {
        File(path).mkdirs()
    }

    fun textToFile(text: String, path: String) {
        File(path).writeText(text)
    }

    fun fileToText(path: String): String {
        return File(path).readText()
    }
}