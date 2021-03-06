package relativitization.game.screens

import com.badlogic.gdx.Gdx
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class HelpScreen(
    val game: RelativitizationGame,
    private val inGame: Boolean
) : TableScreen(game.assets) {
    private val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()

        root.add(
            createLabel(
                "Guide (Click to open + copy): ",
                gdxSettings.normalFontSize
            )
        ).colspan(2)

        root.row().space(10f)

        val docTextButton = createTextButton(
            "https://github.com/Adriankhl/relativitization-game-doc",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            Gdx.net.openURI("https://github.com/Adriankhl/relativitization-game-doc")
            Gdx.app.clipboard.contents = "https://github.com/Adriankhl/relativitization-game-doc"
        }
        root.add(docTextButton).colspan(2)

        root.row().space(30f)

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
        root.add(returnButton)

        val aboutButton = createTextButton(
            "About",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = AboutScreen(game, inGame)
        }
        root.add(aboutButton)
    }
}