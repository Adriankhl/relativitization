package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Image
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class MainMenuScreen(val game: RelativitizationGame) : TableScreen(game.assets) {

    private val background: Image = assets.getImage("background/universe-background")
    private val gdxSettings = game.gdxSettings


    override fun show() {
        // Add background before adding root table from super.show()
        stage.addActor(background)

        super.show()

        val newUniverseButton = createTextButton(
            "New Universe",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = NewUniverseScreen(game)
            dispose()
        }
        root.add(newUniverseButton).prefSize(500f, 100f)

        root.row().space(10f)

        val loadUniverseButton = createTextButton(
            "Load Universe",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = LoadUniverseScreen(game)
            dispose()
        }
        root.add(loadUniverseButton).prefSize(500f, 100f)

        root.row().space(10f)

        val joinUniverseButton = createTextButton(
            "Join Universe",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = JoinUniverseScreen(game)
            dispose()
        }
        root.add(joinUniverseButton).prefSize(500f, 100f)

        root.row().space(10f)

        val gdxSettingsButton = createTextButton(
            "Client Settings",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = ClientSettingsScreen(game, false)
            dispose()
        }
        root.add(gdxSettingsButton).prefSize(500f, 100f)

        root.row().space(10f)

        val helpButton = createTextButton(
            "Help",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = HelpScreen(game, false)
            dispose()
        }
        root.add(helpButton).prefSize(500f, 100f)
    }
}