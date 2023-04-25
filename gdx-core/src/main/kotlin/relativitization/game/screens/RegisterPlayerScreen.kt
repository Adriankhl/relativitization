package relativitization.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Array
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import relativitization.game.RelativitizationGame
import relativitization.game.utils.TableScreen
import relativitization.universe.game.UniverseClientSettings
import relativitization.universe.game.data.serializer.DataSerializer

class RegisterPlayerScreen(val game: RelativitizationGame) : TableScreen(game.assets) {
    private val gdxSettings = game.gdxSettings

    private var idList: List<Int> = runBlocking {
        game.universeClient.httpGetAvailableHumanIdList().sorted()
    }

    init {
        if (idList.isNotEmpty()) {
            game.universeClient.universeClientSettings.playerId = idList.first()
        }
    }

    private var playerId: Int = game.universeClient.universeClientSettings.playerId
    private var password: String = game.universeClient.universeClientSettings.password

    override fun show() {
        super.show()

        root.add(createRegisterPlayerScrollPane()).pad(20f).growX()

        root.row().space(10f)

        root.add(createButtonTable())
    }

    /**
     * Create table for start button and cancel button
     */
    private fun createButtonTable(): Table {
        val nestedTable = Table()

        val startStatusLabel = createLabel("", gdxSettings.normalFontSize)

        val startButton: TextButton = createTextButton(
            "Start",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            runBlocking {
                if (game.universeClient.getCurrentServerStatus().isUniverseRunning) {
                    game.screen = LoadingGameScreen(game)
                    dispose()
                } else {
                    val httpCode = game.universeClient.httpPostRunUniverse()
                    if (httpCode == HttpStatusCode.OK) {
                        game.screen = LoadingGameScreen(game)
                        dispose()
                    } else {
                        startStatusLabel.setText("Universe not running")
                    }
                }
            }
        }

        disableActor(startButton)

        val registerStatusLabel = createLabel("", gdxSettings.normalFontSize)

        val registerPlayerButton: TextButton = createTextButton(
            "Register",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            if (playerId >= 0) {
                val oldClientSettings: UniverseClientSettings = DataSerializer.copy(
                    game.universeClient.universeClientSettings
                )

                // Deregister player first
                runBlocking {
                    game.universeClient.httpPostDeregisterPlayer()

                    game.universeClient.universeClientSettings.playerId = playerId
                    game.universeClient.universeClientSettings.password = password

                    val httpCode = game.universeClient.httpPostRegisterPlayer()

                    if (httpCode == HttpStatusCode.OK) {
                        // Update primary selected id
                        game.universeClient.primarySelectedPlayerId =
                            game.universeClient.universeClientSettings.playerId
                        registerStatusLabel.setText("Registered player id: " +
                                "${game.universeClient.universeClientSettings.playerId}"
                        )
                        enableActor(startButton)
                    } else {
                        // Reset settings if it fails
                        game.universeClient.setUniverseClientSettings(oldClientSettings)
                        registerStatusLabel.setText("Register player fail, http code: $httpCode")
                    }
                }
            } else {
                registerStatusLabel.setText("Player id smaller than 0, please pick a valid id")
            }
        }


        val cancelButton = createTextButton(
            "Cancel",
            gdxSettings.bigFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            game.screen = MainMenuScreen(game)
        }

        nestedTable.add(registerPlayerButton).space(10f)
        nestedTable.add(startButton).space(10f)
        nestedTable.add(cancelButton).space(10f)
        nestedTable.row().space(10f)
        nestedTable.add(registerStatusLabel).colspan(3)
        nestedTable.add(startStatusLabel).colspan(3)

        return nestedTable
    }

    private fun createRegisterPlayerScrollPane(): ScrollPane {
        val table = Table()

        table.add(
            createLabel(
                "Register player settings:",
                gdxSettings.hugeFontSize
            )
        ).colspan(2)

        table.row().space(20f)

        table.add(
            createLabel(
                "Type of available players: ",
                gdxSettings.normalFontSize
            )
        )
        val getPlayerTypeSelectBox = createSelectBox(
            listOf("Human only", "All"),
            "Human only",
            gdxSettings.normalFontSize
        )
        table.add(getPlayerTypeSelectBox)

        table.row().space(10f)

        // Define before update button but show after update button
        val playerIdSelectBox = createSelectBox(
            idList,
            playerId,
            gdxSettings.normalFontSize
        ) { newPlayerId, _ ->
            playerId = newPlayerId
        }

        val updateButton = createTextButton(
            "Update",
            gdxSettings.normalFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            when (getPlayerTypeSelectBox.selected) {
                "Human only" -> {
                    runBlocking {
                        idList = game.universeClient.httpGetAvailableHumanIdList()
                    }
                }
                "All" -> {
                    runBlocking {
                        idList = game.universeClient.httpGetAvailableIdList()
                    }
                }
                else -> {
                    runBlocking {
                        idList = game.universeClient.httpGetAvailableIdList()
                    }
                }
            }

            // Prevent null pointer exception at playerIdSelectBox
            if (idList.isEmpty()) {
                idList = listOf(-1)
            }

            playerIdSelectBox.items = Array(idList.sorted().toTypedArray())
        }
        table.add(updateButton).colspan(2)

        table.row().space(10f)

        table.add(createLabel("Pick your player id: ", gdxSettings.normalFontSize))
        table.add(playerIdSelectBox)

        table.row().space(10f)

        table.add(
            createLabel(
                "Password (for holding your player id): ",
                gdxSettings.normalFontSize
            )
        )
        val passwordTextField = createTextField(
            game.universeClient.universeClientSettings.password,
            gdxSettings.normalFontSize
        ) { newPassword, _ ->
            password = newPassword
        }
        table.add(passwordTextField)

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
}