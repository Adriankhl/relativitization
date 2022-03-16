package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.ChangeFactoryPolicyCommand
import relativitization.universe.data.commands.GrantIndependenceCommand
import relativitization.universe.data.components.politicsData
import relativitization.universe.data.events.AskToMergeCarrierEvent

class PoliticsInfoPane(val game: RelativitizationGame) : UpperInfoPane<ScrollPane>(game) {
    override val infoName: String = "Politics"

    override val infoPriority: Int = 10

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    init {
        // Set background color
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)


        // Configure scroll pane
        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)


        updatePlayerData()
        updateTable()
    }

    override fun getScreenComponent(): ScrollPane {
        val primaryPlayerData: PlayerData = game.universeClient.getValidPrimaryPlayerData()
        if (primaryPlayerData != playerData) {
            updatePlayerData()
            updateTable()
        }
        return scrollPane
    }


    override fun onUniverseData3DChange() {
        updatePlayerData()
        updateTable()
    }

    override fun onPrimarySelectedPlayerIdChange() {
        updatePlayerData()
        updateTable()
    }

    override fun onCommandListChange() {
        updatePlayerData()
        updateTable()
    }

    private fun updatePlayerData() {
        playerData = game.universeClient.getValidPrimaryPlayerData()
    }


    private fun updateTable() {
        table.clear()

        table.add(
            createLabel(
                "Politics: player ${playerData.playerId}",
                gdxSettings.bigFontSize
            )
        ).pad(20f)

        table.row().space(10f)

        table.add(createMergePlayerTable())

        table.row().space(20f)

        table.add(createGrantIndependenceTable())

        table.row().space(20f)

        table.add(createFactoryPolicyTable())
    }

    private fun createMergePlayerTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Waiting to merge: ${playerData.playerInternalData.politicsData().hasAgreedMerge}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        val askToMergeButton = createTextButton(
            "Ast to merge",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val askToMergeEvent = AskToMergeCarrierEvent(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
            )

            val addEventCommand = AddEventCommand(
                event = askToMergeEvent,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D
            )

            game.universeClient.currentCommand = addEventCommand
        }
        nestedTable.add(askToMergeButton)

        return nestedTable
    }

    private fun createGrantIndependenceTable(): Table {
        val nestedTable = Table()

        val grantIndependenceButton = createTextButton(
            "Grant independence",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val grantIndependenceCommand = GrantIndependenceCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
            )

            game.universeClient.currentCommand = grantIndependenceCommand
        }
        nestedTable.add(grantIndependenceButton)

        return nestedTable
    }

    private fun createFactoryPolicyTable(): Table {
        val nestedTable = Table()

        val allowSubordinateBuildFactoryTickImageButton = createTickImageButton(
            playerData.playerInternalData.politicsData().isSubordinateBuildFactoryAllowed,
            gdxSettings.soundEffectsVolume,
        )

        val allowLeaderBuildLocalFactoryTickImageButton = createTickImageButton(
            playerData.playerInternalData.politicsData().isLeaderBuildLocalFactoryAllowed,
            gdxSettings.soundEffectsVolume,
        )

        val allowForeignInvestorTickImageButton = createTickImageButton(
            playerData.playerInternalData.politicsData().isForeignInvestorAllowed,
            gdxSettings.soundEffectsVolume,
        )

        val changeButton = createTextButton(
            "Change factory policy",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val changeFactoryPolicyCommand = ChangeFactoryPolicyCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                allowSubordinateBuildFactory = allowSubordinateBuildFactoryTickImageButton.isChecked,
                allowLeaderBuildLocalFactory = allowLeaderBuildLocalFactoryTickImageButton.isChecked,
                allowForeignInvestor = allowForeignInvestorTickImageButton.isChecked,
            )

            game.universeClient.currentCommand = changeFactoryPolicyCommand
        }

        nestedTable.add(changeButton).colspan(3)

        nestedTable.row().space(0f)

        nestedTable.add(
            createLabel(
                "Subordinate build: ",
                gdxSettings.smallFontSize,
            )
        )

        nestedTable.add(
            createLabel(
                "${playerData.playerInternalData.politicsData().isSubordinateBuildFactoryAllowed}",
                gdxSettings.smallFontSize,
            )
        )

        nestedTable.add(allowSubordinateBuildFactoryTickImageButton).size(
            50f * gdxSettings.imageScale
        )

        nestedTable.row()

        nestedTable.add(
            createLabel(
                "Leader local build: ",
                gdxSettings.smallFontSize,
            )
        )

        nestedTable.add(
            createLabel(
                "${playerData.playerInternalData.politicsData().isLeaderBuildLocalFactoryAllowed}",
                gdxSettings.smallFontSize,
            )
        )

        nestedTable.add(allowLeaderBuildLocalFactoryTickImageButton).size(
            50f * gdxSettings.imageScale
        )

        nestedTable.row()

        nestedTable.add(
            createLabel(
                "Foreigner investor: ",
                gdxSettings.smallFontSize,
            )
        )

        nestedTable.add(
            createLabel(
                "${playerData.playerInternalData.politicsData().isForeignInvestorAllowed}",
                gdxSettings.smallFontSize,
            )
        )

        nestedTable.add(allowForeignInvestorTickImageButton).size(
            50f * gdxSettings.imageScale
        )

        return nestedTable
    }
}