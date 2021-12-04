package relativitization.game.components.info

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

class AIInfo(val game: RelativitizationGame) : ScreenComponent<ScrollPane>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private val table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    init {
        // Set background color
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)


        // Configure scroll pane
        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        updateTable()
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }

    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel(
            "AI: Player ${game.universeClient.universeClientSettings.playerId}",
            gdxSettings.bigFontSize
        )

        table.add(headerLabel).pad(20f)
    }
}