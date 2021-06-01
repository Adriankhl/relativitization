package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

class OverviewInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {

    private val gdxSettings = game.gdxSettings

    private val table: Table = Table()
    private val scrollPane: ScrollPane = createScrollPane(table)

    private var id: Int = -1

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.3f, 0.5f, 1.0f)
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }

    override fun onPrimarySelectedPlayerIdChange() {
        updateId()
    }


    private fun updateId() {
        id = if (game.universeClient.isPrimarySelectedPlayerIdValid()) {
            game.universeClient.primarySelectedPlayerId
        } else {
            game.universeClient.getUniverseData3D().id
        }
    }

    private fun updateTable() {
        table.clear()

        val playerData = game.universeClient.getUniverseData3D().get(id)
        val headerLabel = createLabel("Overview: ${playerData.id}", gdxSettings.normalFontSize)

        table.add(headerLabel)

        table.row().space(10f)
    }
}