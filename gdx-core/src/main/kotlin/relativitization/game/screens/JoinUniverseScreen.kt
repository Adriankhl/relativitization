package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import relativitization.universe.core.utils.RelativitizationLogManager

class JoinUniverseScreen(val game: RelativitizationGame) : TableScreen(game.assets)  {
    private val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()

        root.add(createUniverseClientSettingsScrollPane()).pad(20f).growX()

        root.row().space(20f)

        root.add(createButtonTable())
    }

    private fun createUniverseClientSettingsScrollPane(): ScrollPane {
        val table = Table()

        table.add(createLabel(
            "Join universe settings:",
            gdxSettings.hugeFontSize
        )).colspan(2).space(20f)

        table.row().space(10f)

        table.add(
            createLabel(
                "Server address: ",
                gdxSettings.normalFontSize
            )
        )

        val serverAddressTextField = createTextField(
            game.universeClient.universeClientSettings.serverAddress,
            gdxSettings.normalFontSize
        ) { address, _ ->
            game.universeClient.universeClientSettings.serverAddress = address
        }
        table.add(serverAddressTextField)

        table.row().space(10f)

        table.add(
            createLabel(
                "Server port: ",
                gdxSettings.normalFontSize
            )
        )

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

        table.add(
            createLabel(
                "Admin password (if you are admin): ",
                gdxSettings.normalFontSize
            )
        )

        val adminPasswordTextField = createTextField(
            game.universeClient.universeClientSettings.adminPassword,
            gdxSettings.normalFontSize
        ) { password, _ ->
            game.universeClient.universeClientSettings.adminPassword = password
        }
        table.add(adminPasswordTextField)

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

    private fun createButtonTable(): Table {
        val table = Table()

        val joinFailLabel = createLabel("", gdxSettings.normalFontSize)
        val joinUniverseButton = createTextButton(
            "Join",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            runBlocking {
                val universeStatus = game.universeClient.httpGetUniverseServerStatus()
                if (universeStatus.hasUniverse) {
                    game.screen = ServerSettingsScreen(game)
                    dispose()
                } else {
                    joinFailLabel.setText("Cannot join, no universe available.")
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

        table.add(joinUniverseButton).space(10f)
        table.add(cancelButton).space(10f)

        table.row().space(10f)

        table.add(joinFailLabel).colspan(2)

        return table
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}