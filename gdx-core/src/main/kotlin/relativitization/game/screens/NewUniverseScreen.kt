package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class NewUniverseScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        root.add(createGenerateSettingsScrollPane())
    }

    private fun createGenerateSettingsScrollPane(): ScrollPane {
        val table = Table()

        addDimensionSelectBoxes(table)

        val scrollPane: ScrollPane = createScrollPane(table)

        scrollPane.fadeScrollBars = false;
        scrollPane.setFlickScroll(false);

        return scrollPane
    }

    private fun addDimensionSelectBoxes(table: Table) {
        table.add(createLabel("Universe x dimension: ", gdxSetting.normalFontSize))
        val xDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings().universeSettings.xDim,
            gdxSetting.normalFontSize
        ) {
            game.universeClient.generateSettings.universeSettings.xDim = it
        }
        table.add(xDimSelectBox)
    }
}