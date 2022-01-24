package relativitization.game.screens

import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class CreditScreen(
    val game: RelativitizationGame,
    private val inGame: Boolean
) : TableScreen(game.assets) {
    private val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()

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
    }
}