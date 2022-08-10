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
        ).colspan(2)

        nestedTable.row().space(30f)

        val introductionText: String = """
            You directly control one or more local pop systems, 
            e.g., solar system and spaceship, in a relativistic universe.
            
            "Relativistic universe":
            1. Information cannot travel faster than the speed of light,
                 you see the past state of other players.
            2. A nation consists of a hierarchy of players, 
                 i.e., leaders and subordinates.
            3. You send "command" to control your local pop systems,
                 manage your nation, and interact with other players.
            4. The faster you move, the slower your clock ticks. 
        """.trimIndent()

        nestedTable.add(
            createLabel(
                introductionText,
                gdxSettings.normalFontSize
            )
        ).colspan(2).growX()

        nestedTable.row().space(10f)

        val aiGuideText: String = """
            You can use the AI to make all decisions for you every turn:
            1. Click "AI" in the control bar.
            2. On the right side, (click) "Compute" a list of commands.
            3. Then, click "Use all" to accept all proposed commands.
        """.trimIndent()

        nestedTable.add(
            createLabel(
                aiGuideText,
                gdxSettings.normalFontSize
            )
        ).colspan(2).growX()

        nestedTable.row().space(10f)

        nestedTable.add(
            createImageButton(
                name = "basic/white-upload",
                rUp = 1.0f,
                gUp = 1.0f,
                bUp = 1.0f,
                aUp = 1.0f,
                rDown = 1.0f,
                gDown = 1.0f,
                bDown = 1.0f,
                aDown = 0.7f,
                rChecked = 1.0f,
                gChecked = 1.0f,
                bChecked = 1.0f,
                aChecked = 1.0f,
                soundVolume = gdxSettings.soundEffectsVolume
            )
        ).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        nestedTable.add(
            createLabel(
                "Click this button to send out all commands. ",
                gdxSettings.normalFontSize,
            )
        ).growX()

        nestedTable.row().space(10f)

        nestedTable.add(
            createImageButton(
                name = "basic/white-rightmost-triangle",
                rUp = 1.0f,
                gUp = 1.0f,
                bUp = 1.0f,
                aUp = 1.0f,
                rDown = 1.0f,
                gDown = 1.0f,
                bDown = 1.0f,
                aDown = 0.7f,
                rChecked = 1.0f,
                gChecked = 1.0f,
                bChecked = 1.0f,
                aChecked = 1.0f,
                soundVolume = gdxSettings.soundEffectsVolume
            )
        ).size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        nestedTable.add(
            createLabel(
                "When this button turns white, click to enter next turn.",
                gdxSettings.normalFontSize,
            )
        ).growX()

        return nestedTable
    }
}