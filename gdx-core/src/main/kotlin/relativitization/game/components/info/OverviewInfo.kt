package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.PlayerImage
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData

class OverviewInfo(val game: RelativitizationGame) : ScreenComponent<Table>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private val table: Table = Table()

    private var playerData: PlayerData = PlayerData(-1)

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)

        updatePlayerData()
        updateTable()
    }

    override fun getScreenComponent(): Table {
        return table
    }

    override fun onUniverseData3DChange() {
        updatePlayerData()
        updateTable()
    }

    override fun onPrimarySelectedPlayerIdChange() {
        updatePlayerData()
        updateTable()
    }


    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getUniverseData3D().get(game.universeClient.primarySelectedPlayerId)
        } else {
            game.universeClient.getUniverseData3D().getCurrentPlayerData()
        }
    }

    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel("Overview: player ${playerData.id}", gdxSettings.bigFontSize)

        table.add(headerLabel)

        table.row().space(20f)

        val playerImageStack = PlayerImage.getPlayerImageStack(
            playerData = playerData,
            assets = assets,
            width = 128f,
            height = 128f,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            game.universeClient.mapCenterPlayerId = playerData.id
        }

        table.add(playerImageStack).size(128f * gdxSettings.imageScale, 128f * gdxSettings.imageScale)
    }
}