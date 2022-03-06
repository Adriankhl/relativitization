package relativitization.game.screens

import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class DeadScreen(val game: RelativitizationGame) : TableScreen(game.assets) {

    val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()

        val deadButton = createTextButton(
            "You are dead, quit game",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.dispose()
        }

        root.add(deadButton)
    }
}