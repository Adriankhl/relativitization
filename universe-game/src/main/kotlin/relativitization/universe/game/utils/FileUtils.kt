package relativitization.universe.game.utils

import okio.FileSystem
import okio.Path.Companion.toPath

object FileUtils {
    fun mkdirs(path: String) {
        FileSystem.SYSTEM.createDirectories(path.toPath())
    }

    fun textToFile(text: String, path: String) {
        FileSystem.SYSTEM.write(path.toPath()) {
            writeUtf8(text)
        }
    }

    fun fileToText(path: String): String {
        return FileSystem.SYSTEM.read(path.toPath()) {
            readUtf8()
        }
    }
}