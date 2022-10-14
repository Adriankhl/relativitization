package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class LoadUniverseScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()

        var loadUniverseName: String = ""

        root.add(createLabel("Saved universe: ", gdxSettings.hugeFontSize))

        root.row().space(20f)

        runBlocking {
            val savedUniverseList: List<String> = game.universeClient.httpGetSavedUniverse()

            // Default load universe name to first element
            if (savedUniverseList.isNotEmpty()) {
                loadUniverseName = savedUniverseList[0]
            }

            val loadUniverseNameList = createList(
                savedUniverseList,
                gdxSettings.normalFontSize
            ) { name, _ ->
                loadUniverseName = name
            }

            root.add(loadUniverseNameList)
        }

        root.row().space(10f)

        val loadStatusLabel = createLabel("", gdxSettings.bigFontSize)

        val nextButton = createTextButton(
            "Next",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = ServerSettingsScreen(game)
            dispose()
        }

        disableActor(nextButton)

        val loadButton = createTextButton(
            "Load",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            disableActor(it)
            disableActor(nextButton)

            loadStatusLabel.setText("Loading...")

            runBlocking {
                game.universeClient.runOnceFunctionCoroutineList.add {
                    runBlocking {
                        val httpCode = game.universeClient.httpPostLoadUniverse(loadUniverseName)
                        if (httpCode == HttpStatusCode.OK) {
                            loadStatusLabel.setText("Loading done")
                            enableActor(nextButton)
                        } else {
                            loadStatusLabel.setText("Failed ($httpCode)")
                        }
                    }

                    enableActor(it)
                    Gdx.graphics.requestRendering()
                }
            }
        }


        val cancelButton = createTextButton(
            "Cancel",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = MainMenuScreen(game)
            dispose()
        }

        val buttonTable = Table()

        buttonTable.add(loadButton).space(10f)

        buttonTable.add(nextButton).space(10f)

        buttonTable.add(cancelButton).space(10f)

        root.add(buttonTable)
        root.row().space(10f)
        root.add(loadStatusLabel)
    }

}
