package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import relativitization.universe.utils.RelativitizationLogManager

class JoinUniverseScreen(val game: RelativitizationGame) : TableScreen(game.assets)  {
    private val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()

        root.add(createUniverseClientSettingsScrollPane()).pad(20f).growX()
    }

    fun createUniverseClientSettingsScrollPane(): ScrollPane {
        val table = Table()

        table.add(createLabel(
            "Join universe settings:",
            gdxSettings.hugeFontSize
        )).colspan(2).space(20f)

        table.row().space(10f)

        table.add(createLabel("Server address: ", gdxSettings.normalFontSize))
        val serverAddressTextField = createTextField(
            game.universeClient.universeClientSettings.serverAddress,
            gdxSettings.normalFontSize
        ) { address, _ ->
            game.universeClient.universeClientSettings.serverAddress = address
        }
        table.add(serverAddressTextField)

        table.row().space(10f)


        table.add(createLabel("Server port: ", gdxSettings.normalFontSize))
        val serverPortTextField = createTextField(
            game.universeClient.universeClientSettings.serverPort.toString(),
            gdxSettings.normalFontSize
        ) { port, _ ->
            try {
                game.universeClient.universeClientSettings.serverPort = port.toInt()
            } catch (e: NumberFormatException) {
                logger.error("Invalid server port")
            }
        }
        table.add(serverPortTextField)

        table.row().space(10f)

        // Add empty space for Android keyboard input
        val emptyLabel = createLabel("", gdxSettings.smallFontSize)
        emptyLabel.height = Gdx.graphics.height.toFloat()
        table.add(emptyLabel).minHeight(Gdx.graphics.height.toFloat())

        val scrollPane: ScrollPane = createScrollPane(table)

        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        return scrollPane
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}