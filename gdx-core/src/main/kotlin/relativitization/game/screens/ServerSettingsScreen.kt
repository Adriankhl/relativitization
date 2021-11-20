package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import relativitization.universe.utils.RelativitizationLogManager

class ServerSettingsScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val gdxSettings = game.gdxSettings

    override fun show() {
        super.show()

        root.add(createServerSettingsScrollPane()).pad(20f).growX()

        root.row().space(20f)

        root.add(createButtonTable())
    }

    /**
     * Create table for apply button and cancel button
     */
    private fun createButtonTable(): Table {
        val nestedTable = Table()


        // Add apply setting button
        val applyFailLabel = createLabel("", gdxSettings.normalFontSize)
        val applyButton = createTextButton("Apply settings", gdxSettings.bigFontSize, gdxSettings.soundEffectsVolume) {
            logger.debug("Applying settings")
            runBlocking {
                val httpCode = game.universeClient.httpPostUniverseServerSettings()
                if (httpCode == HttpStatusCode.OK) {
                    // Also modify client admin password after successfully modified the password at the serve
                    val newUniverseClientSettings = game.universeClient.universeClientSettings.copy(
                        adminPassword = game.universeClient.universeServerSettings.adminPassword
                    )
                    game.universeClient.setUniverseClientSettings(newUniverseClientSettings)
                    game.screen = RegisterPlayerScreen(game)
                    dispose()
                } else {
                    applyFailLabel.setText("Apply fail, http code: $httpCode")
                }
            }
        }

        val cancelButton = createTextButton(
            "Cancel",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = MainMenuScreen(game)
        }

        nestedTable.add(applyButton).space(10f)
        nestedTable.add(cancelButton).space(10f)
        nestedTable.row().space(10f)
        nestedTable.add(applyFailLabel).colspan(2)

        return nestedTable
    }


    /**
     * Create scroll pane for all generate universe setting
     */
    private fun createServerSettingsScrollPane(): ScrollPane {
        val table = Table()

        table.add(createLabel("Server Settings :", gdxSettings.hugeFontSize)).colspan(2)

        table.row().space(20f)

        // don't change admin password stored in the client now, do this after successfully updated server password
        table.add(createLabel("Admin password (for admin access to server): ", gdxSettings.normalFontSize))
        val adminPasswordTextField = createTextField(
            game.universeClient.universeServerSettings.adminPassword,
            gdxSettings.normalFontSize
        ) { adminPassword, _ ->
            game.universeClient.universeServerSettings.adminPassword = adminPassword
        }
        table.add(adminPasswordTextField)

        table.row().space(10f)

        table.add(createLabel("Clear inactive player per turn: ", gdxSettings.normalFontSize))
        val clearInactivePerTurnCheckbox = createCheckBox(
            "",
            game.universeClient.universeServerSettings.clearInactivePerTurn,
            gdxSettings.normalFontSize
        ) { clearInactivePerTurn, _ ->
            game.universeClient.universeServerSettings.clearInactivePerTurn = clearInactivePerTurn
        }
        table.add(clearInactivePerTurnCheckbox)

        table.row().space(10f)

        table.add(createLabel("Human input wait time limit (in seconds): ", gdxSettings.normalFontSize))
        val waitTimeLimitSelectBox = createSelectBox(
            (10..3600).toList(),
            game.universeClient.universeServerSettings.waitTimeLimit,
            gdxSettings.normalFontSize,
        ) { limit, _ ->
            game.universeClient.universeServerSettings.waitTimeLimit = limit
        }
        table.add(waitTimeLimitSelectBox)

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