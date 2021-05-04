package relativitization.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import relativitization.client.UniverseClient
import relativitization.server.UniverseServer

class RelativitizationGame(val universeClient: UniverseClient, val universeServer: UniverseServer) : Game() {
    val setting: GameSetting = GameSetting()

    override fun create() {
        Gdx.graphics.isContinuousRendering = setting.continuousRendering
    }
}