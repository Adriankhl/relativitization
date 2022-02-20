package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.MapPlayerColorMode
import relativitization.game.RelativitizationGame

class MapModeInfo(val game: RelativitizationGame) : UpperInfo<ScrollPane>(game) {
    override val infoName: String = "Map mode"

    override val infoPriority: Int = 14

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

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

    override fun onGdxSettingsChange() {
        updateTable()
    }

    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel(
            "Map mode",
            gdxSettings.bigFontSize
        )

        table.add(headerLabel).pad(20f)

        table.row().space(20f)

        table.add(createMapColorModeTable())
    }

    private fun createMapColorModeTable(): Table {
        val nestedTable = Table()

        nestedTable.add(createLabel("Map color mode: ", gdxSettings.smallFontSize))

        val mapColorModeSelectBox = createSelectBox(
            MapPlayerColorMode.values().toList(),
            gdxSettings.mapPlayerColorMode,
            gdxSettings.smallFontSize
        ) { mapPlayerColorMode, _ ->
            gdxSettings.mapPlayerColorMode = mapPlayerColorMode
            game.changeGdxSettings()
        }

        nestedTable.add(mapColorModeSelectBox)

        return nestedTable
    }
}