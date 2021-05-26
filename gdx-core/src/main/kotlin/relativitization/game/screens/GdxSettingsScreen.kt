package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class GdxSettingsScreen(val game: RelativitizationGame, val inGame: Boolean) : TableScreen(game.assets) {
    private val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        root.add(createGdxSettingsScrollPane())
    }

    private fun createGdxSettingsScrollPane(): ScrollPane {
        val table: Table = Table()

        val scrollPane: ScrollPane = createScrollPane(table)

        addGdxSettings(table)

        scrollPane.fadeScrollBars = false
        scrollPane.setFlickScroll(true)

        return scrollPane
    }

    private fun addGdxSettings(table: Table) {
        val gdxSettingsLabel = createLabel("Gdx Settings:", gdxSetting.hugeFontSIze)

        table.add(gdxSettingsLabel).colspan(2).space(20f)
    }
}