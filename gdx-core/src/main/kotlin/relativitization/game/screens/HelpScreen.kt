package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class HelpScreen(
    val game: RelativitizationGame,
    private val inGame: Boolean
) : TableScreen(game.assets) {
    private val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()

        root.add(createDocScrollPane())

        root.row().space(50f)

        root.add(createButtonTable())
    }

    private fun createButtonTable(): Table {
        val table = Table()

        val returnButton = createTextButton(
            "Return",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            if (inGame) {
                game.screen = GameScreen(game)
                dispose()
            } else {
                game.screen = MainMenuScreen(game)
                dispose()
            }
        }
        table.add(returnButton).space(20f)

        val aboutButton = createTextButton(
            "About",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = AboutScreen(game, inGame)
        }
        table.add(aboutButton).space(20f)

        return table
    }

    private fun createDocScrollPane(): ScrollPane {
        val table = Table()

        table.add(createFullGuideTable())

        table.row().space(50f)

        table.add(createQuickGuideTable())

        val scrollPane: ScrollPane = createScrollPane(table)

        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        return scrollPane
    }

    private fun createFullGuideTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Full Guide (Click to open + copy): ",
                gdxSettings.bigFontSize
            )
        )

        nestedTable.row().space(10f)

        val docTextButton = createTextButton(
            "https://github.com/Adriankhl/relativitization-game-doc",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            Gdx.net.openURI("https://github.com/Adriankhl/relativitization-game-doc")
            Gdx.app.clipboard.contents = "https://github.com/Adriankhl/relativitization-game-doc"
        }
        nestedTable.add(docTextButton)

        return nestedTable
    }

    private fun createQuickGuideTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Quick Guide",
                gdxSettings.bigFontSize,
            )
        )

        nestedTable.row().space(30f)

        val aiGuideText: String = """
            1. Click "AI" in the control bar.
            2. On the right side, click "Compute".
            3. Then, click "Use all".
            This allows the AI to make all the decisions for you in this turn.
        """.trimIndent()

        nestedTable.add(
            createLabel(
                aiGuideText,
                gdxSettings.normalFontSize
            )
        )

        return nestedTable
    }
}