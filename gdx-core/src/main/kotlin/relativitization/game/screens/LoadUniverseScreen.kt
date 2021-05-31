package relativitization.game.screens

import io.ktor.http.*
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

        val loadFailLabel = createLabel("", gdxSettings.normalFontSize)
        val loadButton = createTextButton(
            "Load",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume
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
