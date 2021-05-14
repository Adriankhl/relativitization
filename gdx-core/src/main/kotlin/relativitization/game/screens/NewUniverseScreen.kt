package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import relativitization.universe.generate.GenerateUniverse

class NewUniverseScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        root.add(createGenerateSettingsScrollPane())
    }

    private fun createGenerateSettingsScrollPane(): ScrollPane {
        val table = Table()

        val generateSettingLabel = createLabel("Generate Universe Settings:", gdxSetting.bigFontSIze)

        table.add(generateSettingLabel).colspan(2).space(20f)
        table.row().space(10f)

        addGenerateMethod(table)
        table.row().space(10f)

        addDimensionSelectBoxes(table)

        val scrollPane: ScrollPane = createScrollPane(table)

        scrollPane.fadeScrollBars = false;
        scrollPane.setFlickScroll(false);

        return scrollPane
    }

    private fun addGenerateMethod(table: Table) {
        table.add(createLabel("Generate Method: ", gdxSetting.normalFontSize))
        val generateMethodSelectBox = createSelectBox(
            GenerateUniverse.generateMethodMap.keys.toList(),
            GenerateUniverse.generateMethodMap.keys.toList()[0],
            gdxSetting.normalFontSize,
        ) {
            game.universeClient.generateSettings.generateMethod = it
        }
        table.add(generateMethodSelectBox)
    }

    private fun addDimensionSelectBoxes(table: Table) {
        table.add(createLabel("Universe x dimension: ", gdxSetting.normalFontSize))
        val xDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.xDim,
            gdxSetting.normalFontSize
        ) {
            game.universeClient.generateSettings.universeSettings.xDim = it
        }
        table.add(xDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Universe y dimension: ", gdxSetting.normalFontSize))
        val yDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.yDim,
            gdxSetting.normalFontSize
        ) {
            game.universeClient.generateSettings.universeSettings.yDim = it
        }
        table.add(yDimSelectBox)

        table.row().space(10f)

        table.add(createLabel("Universe z dimension: ", gdxSetting.normalFontSize))
        val zDimSelectBox = createSelectBox(
            (1..50).toList(),
            game.universeClient.generateSettings.universeSettings.zDim,
            gdxSetting.normalFontSize
        ) {
            game.universeClient.generateSettings.universeSettings.zDim = it
        }
        table.add(zDimSelectBox)
    }
}