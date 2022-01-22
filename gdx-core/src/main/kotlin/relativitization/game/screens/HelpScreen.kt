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
                "Quick start guide / documentation: ",
                gdxSettings.normalFontSize
            )
        )

        root.row().space(10f)

        val docTextButton = createTextButton(
            "github.com/Adriankhl/relativitization-game-doc",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            Gdx.net.openURI("https://github.com/Adriankhl/relativitization-game-doc")
        }
        root.add(docTextButton)

        root.row().space(30f)

        val returnButton = createTextButton(
            "Return",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            if (inGame) {
                game.screen = GameScreen(game)
            } else {
                game.screen = MainMenuScreen(game)
            }
        }
        root.add(returnButton)
    }
}