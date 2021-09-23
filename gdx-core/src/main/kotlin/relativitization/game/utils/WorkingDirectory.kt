package relativitization.game.utils

import java.io.File

object WorkingDirectory {
    fun isValidAssetDir(checkFile: File): Boolean {
        val assetSubDirList: List<String> = listOf(
            "fonts",
            "images",
            "music",
            "skin",
            "sounds",
            "translations"
        )

        val checkList: List<Boolean> = assetSubDirList.map {
            val subFile = File(checkFile, it)
            println(subFile.canonicalPath)
            subFile.isFile || subFile.isDirectory
        }

        return checkList.all { it }
    }

    fun relativeAssetDir(): String {
        val thisDir = File("")


        //val assetDir: File = if (thisDir.)

        thisDir.parentFile
        println(thisDir.path)

        return thisDir.canonicalPath
    }
}