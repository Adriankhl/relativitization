package relativitization.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen

class ServerSettingsScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    val gdxSetting = game.gdxSetting

    override fun show() {
        super.show()

        root.add(createServerSettingsScrollPane()).colspan(2)

        root.row().space(20f)

        // Add apply setting button
        val applyFailLabel = createLabel("", gdxSetting.normalFontSize)
        val applyButton = createTextButton("Apply settings", gdxSetting.bigFontSize) {
            runBlocking {
                val httpCode = game.universeClient.httpPostUniverseServerSettings()
                if (httpCode == HttpStatusCode.OK) {
                    // Also modify client admin password after successfully modified the password at the serve
                    val newUniverseClientSetting = game.universeClient.universeClientSettings.copy(
                        adminPassword = game.universeClient.universeServerSettings.adminPassword
                    )
                    game.universeClient.setUniverseClientSettings(newUniverseClientSetting)
                    game.screen = RegisterPlayerScreen(game)
                    dispose()
                } else {
                    applyFailLabel.setText("Apply fail, http code: $httpCode")
                }
            }
        }

        root.add(applyButton).colspan(2)
        root.row().space(10f)
        root.add(applyFailLabel).colspan(2)
    }


    /**
     * Create scroll pane for all generate universe setting
     */
    private fun createServerSettingsScrollPane(): ScrollPane {
        val table = Table()

        table.add(createLabel("Server Settings :", gdxSetting.hugeFontSIze)).colspan(2)

        table.row().space(20f)

        // don't change admin password stored in the client now, do this after successfully updated server password
        table.add(createLabel("Admin password (for admin access to server): ", gdxSetting.normalFontSize))
        val adminPasswordTextField = createTextField(
            game.universeClient.universeServerSettings.adminPassword,
            gdxSetting.normalFontSize
        ) { adminPassword, _ ->
            game.universeClient.universeServerSettings.adminPassword = adminPassword
        }
        table.add(adminPasswordTextField)

        table.row().space(10f)

        table.add(createLabel("Clear inactive player per turn: ", gdxSetting.normalFontSize))
        val clearInactivePerTurnCheckbox = createCheckBox(
            "",
            game.universeClient.universeServerSettings.clearInactivePerTurn,
            gdxSetting.normalFontSize
        ) { clearInactivePerTurn, _ ->
            game.universeClient.universeServerSettings.clearInactivePerTurn = clearInactivePerTurn
        }
        table.add(clearInactivePerTurnCheckbox)

        table.row().space(10f)

        table.add(createLabel("Human input wait time limit (in seconds): ", gdxSetting.normalFontSize))
        val waitTimeLimitSelectBox = createSelectBox(
            (10..3600).toList(),
            game.universeClient.universeServerSettings.waitTimeLimit,
            gdxSetting.normalFontSize,
        ) { limit, _ ->
            game.universeClient.universeServerSettings.waitTimeLimit = limit
        }
        table.add(waitTimeLimitSelectBox)

        table.row().space(10f)

        val scrollPane: ScrollPane = createScrollPane(table)

        scrollPane.fadeScrollBars = false
        scrollPane.setFlickScroll(false)

        return scrollPane
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}