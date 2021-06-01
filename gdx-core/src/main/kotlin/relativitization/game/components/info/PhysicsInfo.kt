package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent
import relativitization.universe.data.PlayerData

class PhysicsInfo(
    val game: RelativitizationGame,
) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()
    private var scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.3f, 0.5f, 1.0f)

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


    private fun updatePlayerData() {
        playerData = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.getUniverseData3D().get(game.universeClient.primarySelectedPlayerId)
        } else {
            game.universeClient.getUniverseData3D().get(game.universeClient.getUniverseData3D().id)
        }
    }

    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel("Physics: player ${playerData.id}", gdxSettings.normalFontSize)

        table.add(headerLabel)

        table.row().space(10f)

    }
}