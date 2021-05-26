package relativitization.game.screens

import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class LoadUniverseScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        var loadUniverseName: String = ""

        root.add(createLabel("Saved universe: ", gdxSetting.hugeFontSize))

        root.row().space(20f)

        runBlocking {
            val loadUniverseNameList = createList(
                game.universeClient.httpGetSavedUniverse(),
                gdxSetting.normalFontSize
            ) { name, _ ->
                loadUniverseName = name
            }

            root.add(loadUniverseNameList)

        }

        root.row().space(10f)

        val loadFailLabel = createLabel("", gdxSetting.normalFontSize)
        val loadButton = createTextButton(
            "Load",
            gdxSetting.normalFontSize,
            gdxSetting.soundEffectsVolume
        ) {
            runBlocking {
                val httpCode = game.universeClient.httpPostLoadUniverse(loadUniverseName)
                if (httpCode == HttpStatusCode.OK) {
                    game.screen = ServerSettingsScreen(game)
                    dispose()
                } else {
                    loadFailLabel.setText("Load universe fail, http code: ${httpCode}")
                }
            }
        }
        root.add(loadButton)
        root.row().space(10f)
        root.add(loadFailLabel)
    }
}