package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Image
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class MainMenuScreen(val game: RelativitizationGame) : TableScreen(game.assets) {

    private val background: Image = assets.getImage("background/universe-background")
    private val gdxSetting = game.gdxSetting


    override fun show() {
        // Add background before adding root table from super.show()
        stage.addActor(background)

        super.show()

        val newUniverseButton = createTextButton("New Universe", gdxSetting.bigFontSize, gdxSetting.soundEffectsVolume) {
            game.screen = NewUniverseScreen(game)
            dispose()
        }
        root.add(newUniverseButton).prefSize(500f, 100f).space(20f)
        root.row()
        val loadUniverseButton = createTextButton("Load Universe", gdxSetting.bigFontSize, gdxSetting.soundEffectsVolume) {
            game.screen = LoadUniverseScreen(game)
            dispose()
        }
        root.add(loadUniverseButton).prefSize(500f, 100f).space(20f)
        root.row()
        val joinUniverseButton = createTextButton("Join Universe", gdxSetting.bigFontSize, gdxSetting.soundEffectsVolume)
        root.add(joinUniverseButton).prefSize(500f, 100f).space(20f)
        root.row()
        val gdxSettingsButton = createTextButton("Gdx Settings", gdxSetting.bigFontSize, gdxSetting.soundEffectsVolume) {
            game.screen = GdxSettingsScreen(game, false)
            dispose()
        }
        root.add(gdxSettingsButton).prefSize(500f, 100f).space(20f)
    }
}