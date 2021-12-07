package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.PlayerImage
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData

class OverviewInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private val table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)

        // Configure scroll pane
        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        updatePlayerData()
        updateTable()
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }

    override fun onUniverseData3DChange() {
        updatePlayerData()
        updateTable()
    }

    override fun onPrimarySelectedPlayerIdChange() {
        updatePlayerData()
        updateTable()
    }

    override fun onCommandListChange() {
        updatePlayerData()
        updateTable()
    }

    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getPrimarySelectedPlayerData()
        } else {
            game.universeClient.getUniverseData3D().getCurrentPlayerData()
        }
    }

    private fun updateTable() {
        table.clear()

        val headerLabel =
            createLabel("Overview: player ${playerData.playerId}", gdxSettings.bigFontSize)

        table.add(headerLabel)

        table.row().space(20f)

        val playerImageStack = PlayerImage.getPlayerImageStack(
            playerData = playerData,
            primaryPlayerData = game.universeClient.getPrimarySelectedPlayerData(),
            assets = assets,
            width = 128f,
            height = 128f,
            soundVolume = gdxSettings.soundEffectsVolume,
            mapPlayerColorMode = gdxSettings.mapPlayerColorMode
        ) {
            game.universeClient.mapCenterPlayerId = playerData.playerId
        }

        table.add(playerImageStack)
            .size(128f * gdxSettings.imageScale, 128f * gdxSettings.imageScale)
    }
}