package playground

import okio.FileSystem
import okio.Path.Companion.toPath
import relativitization.universe.core.data.PlayerData
import relativitization.universe.core.data.serializer.DataSerializer
import kotlin.test.Ignore


internal class OkioTest {
    @Ignore
    fun saveAndLoadTest() {
        val playerData = PlayerData(1)
        val playerDataString: String = DataSerializer.encode(playerData)

        FileSystem.SYSTEM.createDirectories("./okio".toPath())

        FileSystem.SYSTEM.write("./okio/playerData.json".toPath()) {
            writeUtf8(playerDataString)
        }

        val loadPlayerDataString: String = FileSystem.SYSTEM.read("./okio/playerData.json".toPath()) {
            readUtf8()
        }

        val loadPlayerData: PlayerData = DataSerializer.decode(loadPlayerDataString)

        println(loadPlayerData)
    }
}