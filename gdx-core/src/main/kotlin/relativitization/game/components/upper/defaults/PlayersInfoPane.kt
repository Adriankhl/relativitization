package relativitization.game.components.upper.defaults

import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.components.upper.UpperInfoPane
import relativitization.game.utils.PlayerImage
import relativitization.game.utils.PlayerSummary
import relativitization.game.utils.Summary
import relativitization.universe.data.PlayerData
import relativitization.universe.data.components.defaults.economy.ResourceType

class PlayersInfoPane(val game: RelativitizationGame) : UpperInfoPane<ScrollPane>(game) {
    override val infoName: String = "Players"

    override val infoPriority: Int = 3

    private val gdxSettings = game.gdxSettings

    private val table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    // Viewed history of player in ID
    private val viewedPlayerIdList: MutableList<Int> = mutableListOf()

    // ID of currently viewing player
    private var viewingIdIndex: Int = 0

    // the currently viewing player data
    private var playerData: PlayerData = PlayerData(-1)

    // cache the computed summary
    private var isPlayerSummaryShowing: Boolean = false
    private var playerSummary: PlayerSummary = PlayerSummary()
    private var playerSummaryOption: PlayerSummaryOption = PlayerSummaryOption.SELF_ONLY
    private var selectedPlayerSummaryResourceType: ResourceType = ResourceType.PLANT

    init {
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)

        // Configure scroll pane
        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        viewedPlayerIdList.clear()
        updatePlayerDataAndIdList()
        updateTable()
    }

    override fun getScreenComponent(): ScrollPane {
        val primaryPlayerData: PlayerData = game.universeClient.getValidPrimaryPlayerData()
        if (primaryPlayerData != playerData) {
            updatePlayerDataAndIdList()
            updateTable()
        }
        return scrollPane
    }

    override fun onUniverseData3DChange() {
        // Clear data list when change data
        viewedPlayerIdList.clear()
        updatePlayerDataAndIdList()
        updateTable()
    }

    override fun onPrimarySelectedPlayerIdChange() {
        updatePlayerDataAndIdList()
        updateTable()
    }

    override fun onCommandListChange() {
        updatePlayerDataAndIdList()
        updateTable()
    }

    private fun updatePlayerDataAndIdList() {
        playerData = game.universeClient.getValidPrimaryPlayerData()

        if (viewedPlayerIdList.isEmpty()) {
            viewingIdIndex = 0
            viewedPlayerIdList.add(playerData.playerId)
        } else {
            // Correctly constrain the index
            if (viewingIdIndex < 0) {
                viewingIdIndex = 0
            }
            if (viewingIdIndex >= viewedPlayerIdList.size) {
                viewingIdIndex = viewedPlayerIdList.size - 1
            }

            // If this id is not the current id
            if (viewedPlayerIdList[viewingIdIndex] != playerData.playerId) {
                viewedPlayerIdList.dropLast(viewedPlayerIdList.size - viewingIdIndex - 1)
                viewingIdIndex++
                viewedPlayerIdList.add(playerData.playerId)
            }
        }
    }

    private fun updateTable() {
        table.clear()

        table.add(
            createLabel(
                "Selected: player ${playerData.playerId}",
                gdxSettings.bigFontSize
            )
        ).pad(20f)

        table.row().space(10f)

        table.add(createPlayerImageAndButtonTable())

        table.row().space(10f)

        table.add(createAllPlayerIdTable())

        table.row().space(10f)

        table.add(createDirectLeaderIdTable())

        table.row().space(10f)

        table.add(createDirectSubordinateIdTable())

        table.row().space(10f)

        table.add(createLeaderIdTable())

        table.row().space(10f)

        table.add(createSubordinateIdTable())

        table.row().space(30f)

        table.add(createPlayerSummaryControlTable())

        table.row().space(10f)
    }


    private fun previousPlayerId() {
        if (viewedPlayerIdList.isNotEmpty()) {
            viewingIdIndex = if (viewingIdIndex >= 1) {
                viewingIdIndex - 1
            } else {
                0
            }

            game.universeClient.primarySelectedPlayerId = viewedPlayerIdList[viewingIdIndex]
        }
    }

    private fun nextPlayerId() {
        if (viewedPlayerIdList.isNotEmpty()) {
            viewingIdIndex = if (viewingIdIndex <= viewedPlayerIdList.size - 2) {
                viewingIdIndex + 1
            } else {
                viewedPlayerIdList.size - 1
            }

            game.universeClient.primarySelectedPlayerId = viewedPlayerIdList[viewingIdIndex]
        }
    }


    private fun createPlayerImageAndButtonTable(): Table {
        val nestedTable = Table()

        val previousPlayerIdButton = createImageButton(
            name = "basic/white-left-arrow",
            rUp = 1.0f,
            gUp = 1.0f,
            bUp = 1.0f,
            aUp = 1.0f,
            rDown = 1.0f,
            gDown = 1.0f,
            bDown = 1.0f,
            aDown = 0.7f,
            rChecked = 1.0f,
            gChecked = 1.0f,
            bChecked = 1.0f,
            aChecked = 1.0f,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            previousPlayerId()
        }

        val nextPlayerIdButton = createImageButton(
            name = "basic/white-right-arrow",
            rUp = 1.0f,
            gUp = 1.0f,
            bUp = 1.0f,
            aUp = 1.0f,
            rDown = 1.0f,
            gDown = 1.0f,
            bDown = 1.0f,
            aDown = 0.7f,
            rChecked = 1.0f,
            gChecked = 1.0f,
            bChecked = 1.0f,
            aChecked = 1.0f,
            soundVolume = gdxSettings.soundEffectsVolume
        ) {
            nextPlayerId()
        }


        val playerImageWidgetGroup = PlayerImage.getPlayerImageWidgetGroup(
            playerData = playerData,
            universeData3DAtPlayer = game.universeClient.getUniverseData3D(),
            primaryPlayerData = game.universeClient.getPrimarySelectedPlayerData(),
            assets = assets,
            width = 128f,
            height = 128f,
            soundVolume = gdxSettings.soundEffectsVolume,
            mapPlayerColorMode = gdxSettings.mapPlayerColorMode
        ) {
            game.universeClient.mapCenterPlayerId = playerData.playerId
            game.universeClient.primarySelectedInt3D = playerData.int4D.toInt3D()
        }

        nestedTable.add(previousPlayerIdButton).size(
            40f * gdxSettings.imageScale,
            40f * gdxSettings.imageScale
        ).pad(20f)

        nestedTable.add(playerImageWidgetGroup).size(
            128f * gdxSettings.imageScale,
            128f * gdxSettings.imageScale
        )

        nestedTable.add(nextPlayerIdButton).size(
            40f * gdxSettings.imageScale,
            40f * gdxSettings.imageScale
        ).pad(20f)

        return nestedTable
    }

    private fun createAllPlayerIdTable(): Table {
        val nestedTable = Table()

        val allPlayerIdLabel = createLabel("Available Player Id: ", gdxSettings.smallFontSize)

        val allPlayerIdSelectBox = createSelectBox(
            itemList = game.universeClient.getUniverseData3D().playerDataMap.keys.toList().sorted(),
            default = playerData.playerId,
            fontSize = gdxSettings.smallFontSize,
        ) { selectedId, _ ->
            game.universeClient.replacePrimarySelectedPlayerId(selectedId)
        }

        nestedTable.add(allPlayerIdLabel)
        nestedTable.add(allPlayerIdSelectBox)

        return nestedTable
    }

    private fun createDirectLeaderIdTable(): Table {
        val nestedTable = Table()

        val directLeaderIdLabel = createLabel("Direct leader Id: ", gdxSettings.smallFontSize)

        val directLeaderButton = createTextButton(
            text = playerData.playerInternalData.directLeaderId.toString(),
            fontSize = gdxSettings.smallFontSize,
            soundVolume = gdxSettings.soundEffectsVolume,
        ) {
            game.universeClient.replacePrimarySelectedPlayerId(playerData.playerInternalData.directLeaderId)
        }

        nestedTable.add(directLeaderIdLabel)
        nestedTable.add(directLeaderButton)

        return nestedTable
    }

    private fun createDirectSubordinateIdTable(): Table {
        val nestedTable = Table()

        val directSubordinateIdLabel =
            createLabel("Direct subordinate Id: ", gdxSettings.smallFontSize)

        val directSubordinateIdSelectBox = createSelectBox(
            itemList = playerData.playerInternalData.directSubordinateIdSet.toList(),
            default = playerData.playerId,
            fontSize = gdxSettings.smallFontSize,
        ) { selectedId, _ ->
            game.universeClient.replacePrimarySelectedPlayerId(selectedId)
        }

        nestedTable.add(directSubordinateIdLabel)
        nestedTable.add(directSubordinateIdSelectBox)

        return nestedTable
    }

    private fun createLeaderIdTable(): Table {
        val nestedTable = Table()

        val leaderIdLabel = createLabel("Leader Id: ", gdxSettings.smallFontSize)

        val leaderIdSelectBox = createSelectBox(
            itemList = playerData.playerInternalData.leaderIdList,
            default = playerData.playerId,
            fontSize = gdxSettings.smallFontSize,
        ) { selectedId, _ ->
            game.universeClient.replacePrimarySelectedPlayerId(selectedId)
        }

        nestedTable.add(leaderIdLabel)
        nestedTable.add(leaderIdSelectBox)

        return nestedTable
    }

    private fun createSubordinateIdTable(): Table {
        val nestedTable = Table()

        val subordinateIdLabel = createLabel("Subordinate Id: ", gdxSettings.smallFontSize)

        val subordinateIdSelectBox = createSelectBox(
            itemList = playerData.playerInternalData.subordinateIdSet.toList(),
            default = playerData.playerId,
            fontSize = gdxSettings.smallFontSize,
        ) { selectedId, _ ->
            game.universeClient.replacePrimarySelectedPlayerId(selectedId)
        }

        nestedTable.add(subordinateIdLabel)
        nestedTable.add(subordinateIdSelectBox)

        return nestedTable
    }

    private fun createPlayerSummaryControlTable(): Table {
        val nestedTable = Table()

        val playerSummaryContainer: Container<Table> = if (isPlayerSummaryShowing) {
            Container(createPlayerSummaryTable())
        } else {
            Container()
        }

        val computeSummaryButton = createTextButton(
            "Compute summary",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            updatePlayerSummary()
            playerSummaryContainer.actor = createPlayerSummaryTable()
            isPlayerSummaryShowing = true
        }

        val hideSummaryButton = createTextButton(
            "Hide",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            playerSummaryContainer.actor = Table()
            isPlayerSummaryShowing = false
        }

        nestedTable.add(computeSummaryButton)

        nestedTable.add(hideSummaryButton)
            .size(50f * gdxSettings.imageScale, 50f * gdxSettings.imageScale)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Options: ",
                gdxSettings.smallFontSize
            )
        )

        val summaryOptionSelectBox = createSelectBox(
            PlayerSummaryOption.values().toList(),
            playerSummaryOption,
            gdxSettings.smallFontSize
        ) { newPlayerSummaryOption, _ ->
            playerSummaryOption = newPlayerSummaryOption
        }

        nestedTable.add(summaryOptionSelectBox)

        nestedTable.row().space(10f)

        nestedTable.add(playerSummaryContainer).colspan(2)

        return nestedTable
    }

    private fun updatePlayerSummary() {
        val selectedId: Int = if (game.universeClient.primarySelectedPlayerId < 0) {
            game.universeClient.getUniverseData3D().id
        } else {
            game.universeClient.primarySelectedPlayerId
        }

        val otherPlayerIdList: List<Int> = when (playerSummaryOption) {
            PlayerSummaryOption.SELF_ONLY -> listOf()
            PlayerSummaryOption.SELF_AND_SUBORDINATES -> game.universeClient.getUniverseData3D()
                .get(
                    selectedId
                ).playerInternalData.subordinateIdSet.toList()
            PlayerSummaryOption.SELECTED -> game.universeClient.selectedPlayerIdList
        }
        playerSummary = Summary.computeFromUniverseData3DAtPlayer(
            selectedId,
            otherPlayerIdList,
            game.universeClient.getUniverseData3D(),
        )
    }

    private fun createPlayerSummaryTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "Summary player id: ${playerSummary.playerId}",
                gdxSettings.normalFontSize
            )
        )

        nestedTable.row().space(20f)

        nestedTable.add(
            createLabel(
                "Number of carrier: ${playerSummary.numCarrier}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Population: ${playerSummary.totalPopulation}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Satisfaction: ${playerSummary.averageSatisfaction}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Attack: ${playerSummary.totalAttack}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Shield: ${playerSummary.totalShield}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Fuel demand: ${playerSummary.totalFuelDemand}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Fuel supply: ${playerSummary.totalFuelSupply}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        val resourceSupplyLabel = createLabel(
            "$selectedPlayerSummaryResourceType supply: " +
                    "${
                        playerSummary.totalResourceSupplyMap.getValue(
                            selectedPlayerSummaryResourceType
                        )
                    }",
            gdxSettings.smallFontSize
        )

        val resourceDemandLabel = createLabel(
            "$selectedPlayerSummaryResourceType demand: " +
                    "${
                        playerSummary.totalResourceDemandMap.getValue(
                            selectedPlayerSummaryResourceType
                        )
                    }",
            gdxSettings.smallFontSize
        )

        val resourceTypeSelectBox = createSelectBox(
            ResourceType.values().toList(),
            selectedPlayerSummaryResourceType,
            gdxSettings.smallFontSize
        ) { resourceType, _ ->
            selectedPlayerSummaryResourceType = resourceType
            resourceSupplyLabel.setText(
                "$selectedPlayerSummaryResourceType supply: " +
                        "${
                            playerSummary.totalResourceSupplyMap.getValue(
                                selectedPlayerSummaryResourceType
                            )
                        }",
            )
            resourceDemandLabel.setText(
                "$selectedPlayerSummaryResourceType demand: " +
                        "${
                            playerSummary.totalResourceDemandMap.getValue(
                                selectedPlayerSummaryResourceType
                            )
                        }",
            )
        }

        nestedTable.add(resourceTypeSelectBox)

        nestedTable.row().space(10f)

        nestedTable.add(resourceDemandLabel)

        nestedTable.row().space(10f)

        nestedTable.add(resourceSupplyLabel)

        return nestedTable
    }
}

enum class PlayerSummaryOption(private val value: String) {
    SELF_ONLY("Self only"),
    SELF_AND_SUBORDINATES("Self and subordinates"),
    SELECTED("Selected"),
    ;

    override fun toString(): String {
        return value
    }
}