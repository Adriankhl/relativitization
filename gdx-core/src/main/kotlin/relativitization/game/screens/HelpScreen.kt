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
        root.add(
            createLabel(
                "Help",
                gdxSettings.hugeFontSize
            )
        )

        root.row().space(20f)

        root.add(
            createLabel(
                "Quick start / documentation: ",
                gdxSettings.normalFontSize
            )
        )

        root.row().space(10f)

        val docTextButton = createTextButton(
            "github.com/Adriankhl/relativitization-game-doc",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            Gdx.net.openURI("https://github.com/Adriankhl/relativitization-game-doc")
        }
        root.add(docTextButton)
    }
}