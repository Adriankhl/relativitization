package relativitization.game.utils

import java.io.File

object WorkingDirectory {
    fun isValidAssetDir(thisDir: File): Boolean {
        val assetSubDirList: List<String> = listOf(
            "fonts",
            "images",
            "music",
            "skin",
            "sounds",
            "translations"
        )

        val checkList: List<Boolean> = assetSubDirList.map {
            val subFile = File(thisDir, it)
            subFile.isFile || subFile.isDirectory
        }

        return checkList.all { it }
    }

    fun findAssetDir(thisDir: File): File {
        return if (isValidAssetDir(thisDir)) {
            thisDir
        } else {
            when {
                File(thisDir, "app").isDirectory -> {
                    File(thisDir, "app")
                }
                File(thisDir, "lib").isDirectory -> {
                    File(thisDir, "lib/app")
                }
                File(thisDir, "../lib").isDirectory -> {
                    File(thisDir, "../lib/app")
                }
                File(thisDir, "assets").isDirectory -> {
                    File(thisDir, "assets")
                }
                File(thisDir, "../assets").isDirectory -> {
                    File(thisDir, "../assets")
                }
                else -> {
                    thisDir
                }
            }
        }
    }

    fun relativeAssetDir(thisDir: File): String {
        val assetDir: File = findAssetDir(thisDir)

        return if (thisDir == assetDir) {
            ""
        } else {
            thisDir.toPath().relativize(assetDir.toPath()).toString() + "/"
        }
    }

    fun relativeAssetDirFromWorkingDir(): String {
        return relativeAssetDir(File("."))
    }
}