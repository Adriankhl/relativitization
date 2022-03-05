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

class PoliticsInfo(val game: RelativitizationGame) : UpperInfo<ScrollPane>(game) {
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

        val headerLabel = createLabel(
            "Politics: player ${playerData.playerId}",
            gdxSettings.bigFontSize
        )

        table.add(headerLabel).pad(20f)

        table.row().space(20f)

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
                "Waiting to merge: ${playerData.playerInternalData.politicsData().agreeMerge}",
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

        nestedTable.add(
            createLabel(
                "Factory policy: ",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Allow subordinate build: ${playerData.playerInternalData.politicsData().allowSubordinateBuildFactory}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Allow local build on subordinate: ${playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Allow foreign build: ${playerData.playerInternalData.politicsData().allowForeignInvestor}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        val allowSubordinateBuildFactoryCheckBox = createCheckBox(
            "Allow subordinate build",
            playerData.playerInternalData.politicsData().allowSubordinateBuildFactory,
            gdxSettings.smallFontSize,
        )

        val allowLeaderBuildLocalFactoryCheckBox = createCheckBox(
            "Allow subordinate build",
            playerData.playerInternalData.politicsData().allowLeaderBuildLocalFactory,
            gdxSettings.smallFontSize,
        )

        val allowForeignInvestorCheckBox = createCheckBox(
            "Allow subordinate build",
            playerData.playerInternalData.politicsData().allowForeignInvestor,
            gdxSettings.smallFontSize,
        )

        val changeButton = createTextButton(
            "Change",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val changeFactoryPolicyCommand = ChangeFactoryPolicyCommand(
                toId = playerData.playerId,
                fromId = game.universeClient.getCurrentPlayerData().playerId,
                fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                allowSubordinateBuildFactory = allowSubordinateBuildFactoryCheckBox.isChecked,
                allowLeaderBuildLocalFactory = allowLeaderBuildLocalFactoryCheckBox.isChecked,
                allowForeignInvestor = allowForeignInvestorCheckBox.isChecked,
            )

            game.universeClient.currentCommand = changeFactoryPolicyCommand
        }

        nestedTable.add(changeButton)

        nestedTable.row().space(10f)

        nestedTable.add(allowSubordinateBuildFactoryCheckBox)

        nestedTable.row().space(10f)

        nestedTable.add(allowLeaderBuildLocalFactoryCheckBox)

        nestedTable.row().space(10f)

        nestedTable.add(allowForeignInvestorCheckBox)

        return nestedTable
    }
}