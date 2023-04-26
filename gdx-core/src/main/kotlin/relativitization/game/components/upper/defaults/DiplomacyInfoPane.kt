package relativitization.game.components.upper.defaults

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.components.upper.UpperInfoPane
import relativitization.universe.core.data.PlayerData
import relativitization.universe.game.data.commands.AddEventCommand
import relativitization.universe.game.data.commands.DeclareIndependenceToDirectLeaderCommand
import relativitization.universe.game.data.commands.DeclareIndependenceToTopLeaderCommand
import relativitization.universe.game.data.commands.DeclareWarCommand
import relativitization.universe.game.data.commands.RemoveAllyCommand
import relativitization.universe.game.data.commands.SurrenderCommand
import relativitization.universe.game.data.components.defaults.diplomacy.getRelation
import relativitization.universe.game.data.components.defaults.diplomacy.hasSubordinateWar
import relativitization.universe.game.data.components.defaults.diplomacy.isAlly
import relativitization.universe.game.data.components.defaults.diplomacy.isEnemy
import relativitization.universe.game.data.components.defaults.diplomacy.war.WarData
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.data.events.CallAllyToSubordinateWarEvent
import relativitization.universe.game.data.events.CallAllyToWarEvent
import relativitization.universe.game.data.events.ProposeAllianceEvent
import relativitization.universe.game.data.events.ProposePeaceEvent

class DiplomacyInfoPane(val game: RelativitizationGame) : UpperInfoPane<ScrollPane>(game) {
    override val infoName: String = "Diplomacy"

    private val gdxSettings = game.gdxSettings

    private var table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var playerData: PlayerData = PlayerData(-1)

    // For choosing the diplomatic relation between player with this id and the primary player
    // Update by select box or select new player
    private var otherPlayerId: Int = playerData.playerId

    private var currentDiplomacyInfoRelationType: DiplomacyInfoRelationType =
        DiplomacyInfoRelationType.ALL

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

    override fun onSelectedPlayerIdListChange() {
        otherPlayerId = game.universeClient.newSelectedPlayerId
        updateTable()
    }

    override fun onCommandListChange() {
        updatePlayerData()
        updateTable()
    }


    private fun updatePlayerData() {
        playerData = game.universeClient.getValidPrimaryPlayerData()

        if (!game.universeClient.getUniverseData3D().playerDataMap.containsKey(otherPlayerId)) {
            otherPlayerId = playerData.playerId
        }
    }


    private fun updateTable() {
        table.clear()

        table.add(
            createLabel(
                "Diplomacy: player ${playerData.playerId}",
                gdxSettings.bigFontSize
            )
        ).pad(20f)

        table.row().space(10f)

        table.add(createSelectOtherPlayerTable())

        table.row().space(20f)

        table.add(createDiplomaticRelationTable())

        table.row().space(20f)

        table.add(createAllianceTable())

        table.row().space(20f)

        table.add(createSelfWarTable())

        table.row().space(20f)

        table.add(createOtherWarTable())

        table.row().space(20f)

        table.add(createWarCommandTable())
    }

    private fun createSelectOtherPlayerTable(): Table {
        val nestedTable = Table()

        val otherPlayerIdSelectBox = createSelectBox(
            computeOtherPlayerIdList(),
            otherPlayerId,
            gdxSettings.smallFontSize
        ) { i, _ ->
            otherPlayerId = i
            updateTable()
        }

        nestedTable.add(
            createLabel(
                "Type: ",
                gdxSettings.smallFontSize
            )
        )

        val diplomacyInfoRelationTypeSelectBox = createSelectBox(
            DiplomacyInfoRelationType.values().toList(),
            currentDiplomacyInfoRelationType,
            gdxSettings.smallFontSize
        ) { diplomacyInfoRelationType, _ ->
            currentDiplomacyInfoRelationType = diplomacyInfoRelationType
            otherPlayerId = computeOtherPlayerIdList().firstOrNull() ?: playerData.playerId
            updateTable()
        }
        nestedTable.add(diplomacyInfoRelationTypeSelectBox)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Other player id: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(otherPlayerIdSelectBox)

        return nestedTable
    }

    private fun computeOtherPlayerIdList(): List<Int> {
        return when (currentDiplomacyInfoRelationType) {
            DiplomacyInfoRelationType.ALL -> {
                game.universeClient.getUniverseData3D().playerDataMap.keys.toList()
            }
            DiplomacyInfoRelationType.SELF_WAR_TARGET -> {
                playerData.playerInternalData.diplomacyData().relationData.selfWarDataMap.keys
                    .toList()
            }
            DiplomacyInfoRelationType.SELF_DEFENSIVE_WAR_TARGET -> {
                playerData.playerInternalData.diplomacyData()
                    .relationData.selfWarDataMap.filterValues {
                        it.warCoreData.isDefensive
                    }.keys.toList()
            }
            DiplomacyInfoRelationType.SELF_OFFENSIVE_WAR_TARGET -> {
                playerData.playerInternalData.diplomacyData()
                    .relationData.selfWarDataMap.filterValues {
                        it.warCoreData.isOffensive
                    }.keys.toList()
            }
            DiplomacyInfoRelationType.SUBORDINATE -> {
                playerData.playerInternalData.subordinateIdSet.toList()
            }
            DiplomacyInfoRelationType.SUBORDINATE_IN_DEFENSIVE_WAR -> {
                playerData.playerInternalData.subordinateIdSet.filter {
                    game.universeClient.getUniverseData3D().playerDataMap.containsKey(it)
                }.filter { playerId ->
                    game.universeClient.getUniverseData3D().get(playerId).playerInternalData
                        .diplomacyData().relationData.selfWarDataMap.values.any {
                            it.warCoreData.isOffensive
                        }
                }
            }
            DiplomacyInfoRelationType.SUBORDINATE_WAR_TARGET -> {
                playerData.playerInternalData.diplomacyData().relationData.subordinateWarDataMap
                    .keys.toList()
            }
            DiplomacyInfoRelationType.ALLY -> {
                playerData.playerInternalData.diplomacyData().relationData.allyMap.keys.toList()
            }
            DiplomacyInfoRelationType.ALLY_WAR_TARGET -> {
                playerData.playerInternalData.diplomacyData().relationData.allyWarDataMap.values
                    .flatMap { it.keys } + playerData.playerInternalData.diplomacyData()
                    .relationData.allySubordinateWarDataMap.values.flatMap { allySubordinateMap ->
                        allySubordinateMap.values.flatMap {
                            it.keys
                        }
                    }
            }
            DiplomacyInfoRelationType.ENEMY -> {
                playerData.playerInternalData.diplomacyData().relationData.allyMap.keys.toList()
            }
        }
    }

    private fun createDiplomaticRelationTable(): Table {
        val nestedTable = Table()

        // Only show this information if the other player is not self
        if (playerData.playerId != otherPlayerId) {
            val relation: Double = playerData.playerInternalData.diplomacyData().relationData
                .getRelation(otherPlayerId)

            nestedTable.add(
                createLabel(
                    "Relation with: $otherPlayerId ($relation)",
                    gdxSettings.normalFontSize
                )
            )

            nestedTable.row().space(10f)

            val isAlly: Boolean = playerData.playerInternalData.diplomacyData().relationData
                .isAlly(otherPlayerId)
            val isEnemy: Boolean = playerData.playerInternalData.diplomacyData().relationData
                .isEnemy(otherPlayerId)

            val relationTable = Table()

            if (isAlly) {
                relationTable.add(
                    createLabel(
                        "Ally",
                        gdxSettings.normalFontSize
                    )
                ).space(10f)
            }

            if (isEnemy) {
                relationTable.add(
                    createLabel(
                        "Enemy",
                        gdxSettings.normalFontSize
                    )
                ).space(10f)
            }

            nestedTable.add(relationTable)
        }

        return nestedTable
    }

    private fun createAllianceTable(): Table {
        val nestedTable = Table()

        val currentPlayerData: PlayerData = game.universeClient.getCurrentPlayerData()

        if (currentPlayerData.playerId != playerData.playerId) {
            nestedTable.add(
                createLabel(
                    "Alliance commands: ",
                    gdxSettings.normalFontSize
                )
            )

            nestedTable.row().space(10f)

            val isAlly: Boolean = currentPlayerData.playerInternalData.diplomacyData().relationData
                .isAlly(playerData.playerId)

            if (isAlly) {
                val breakAllianceButton = createTextButton(
                    "Break alliance",
                    gdxSettings.smallFontSize,
                    gdxSettings.soundEffectsVolume,
                    extraColor = commandButtonColor,
                ) {
                    val removeAllyCommand = RemoveAllyCommand(
                        toId = currentPlayerData.playerId,
                        targetPlayerId = playerData.playerId,
                    )

                    game.universeClient.currentCommand = removeAllyCommand
                }
                nestedTable.add(breakAllianceButton)
            } else {
                val proposeAllianceButton = createTextButton(
                    "Propose alliance",
                    gdxSettings.smallFontSize,
                    gdxSettings.soundEffectsVolume,
                    extraColor = commandButtonColor,
                ) {
                    val proposeAllianceEvent = ProposeAllianceEvent(
                        toId = playerData.playerId,
                    )

                    val addEventCommand = AddEventCommand(
                        proposeAllianceEvent,
                    )

                    game.universeClient.currentCommand = addEventCommand
                }
                nestedTable.add(proposeAllianceButton)
            }
        }

        return nestedTable
    }

    /**
     * Table to display information for all wars
     */
    private fun createGeneralWarDataTable(warData: WarData): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "War start time: ${warData.warCoreData.startTime}",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.row().space(10f)

        val warTypeText: String = if (warData.warCoreData.isOffensive) {
            if (warData.warCoreData.isDefensive) {
                "War type: offensive + defensive"
            } else {
                "War type: defensive"
            }
        } else {
            if (warData.warCoreData.isDefensive) {
                "War type: defensive"
            } else {
                ""
            }
        }

        nestedTable.add(
            createLabel(
                warTypeText,
                gdxSettings.smallFontSize
            )
        )

        return nestedTable
    }

    private fun createCallAllyToWarTable(
        allyIdList: List<Int>,
        warTargetId: Int,
    ): Table {
        val nestedTable = Table()

        val allySelectBox = createSelectBox(
            allyIdList,
            allyIdList.first(),
            gdxSettings.smallFontSize
        )

        val callAllyToSelfWarButton = createTextButton(
            "Call ally to self war",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val event = CallAllyToWarEvent(
                toId = allySelectBox.selected,
                warTargetId = warTargetId
            )

            val addEventCommand = AddEventCommand(
                event = event,
            )

            game.universeClient.currentCommand = addEventCommand
        }

        nestedTable.add(callAllyToSelfWarButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Select ally: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(allySelectBox)

        return nestedTable
    }

    private fun createCallAllyToSubordinateWarTable(
        allyIdList: List<Int>,
        subordinateId: Int,
        warTargetId: Int,
    ): Table {
        val nestedTable = Table()

        val allySelectBox = createSelectBox(
            allyIdList,
            allyIdList.first(),
            gdxSettings.smallFontSize
        )

        val callAllyToSubordinateWarButton = createTextButton(
            "Call ally to subordinate war",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            extraColor = commandButtonColor,
        ) {
            val event = CallAllyToSubordinateWarEvent(
                toId = allySelectBox.selected,
                subordinateId = subordinateId,
                warTargetId = warTargetId
            )

            val addEventCommand = AddEventCommand(
                event = event,
            )

            game.universeClient.currentCommand = addEventCommand
        }

        nestedTable.add(callAllyToSubordinateWarButton).colspan(2)

        nestedTable.row().space(10f)

        nestedTable.add(
            createLabel(
                "Select ally: ",
                gdxSettings.smallFontSize
            )
        )

        nestedTable.add(allySelectBox)

        return nestedTable
    }

    private fun createSelfWarTable(): Table {
        val nestedTable = Table()

        val isInSelfWar: Boolean = playerData.playerInternalData.diplomacyData().relationData
            .selfWarDataMap.containsKey(otherPlayerId)

        if (isInSelfWar) {
            val warData: WarData = playerData.playerInternalData.diplomacyData().relationData
                .selfWarDataMap.getValue(otherPlayerId)

            nestedTable.add(
                createLabel(
                    "In war (self)",
                    gdxSettings.normalFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(createGeneralWarDataTable(warData))

            // Create this table if the selected player is the game player
            if (playerData.playerId == game.universeClient.getUniverseData3D().id) {
                nestedTable.row().space(10f)

                val allyIdList: List<Int> = playerData.playerInternalData.diplomacyData()
                    .relationData.allyMap.keys.toList()

                // Add call ally to war table if ally is not empty
                if (allyIdList.isNotEmpty()) {
                    nestedTable.add(
                        createCallAllyToWarTable(
                            allyIdList = allyIdList,
                            warTargetId = otherPlayerId,
                        )
                    )

                    nestedTable.row().space(10f)
                }

                val proposePeaceButton = createTextButton(
                    "Propose peace",
                    gdxSettings.smallFontSize,
                    gdxSettings.soundEffectsVolume,
                    extraColor = commandButtonColor,
                ) {
                    val proposePeaceEvent = ProposePeaceEvent(
                        toId = otherPlayerId,
                    )

                    val addEventCommand = AddEventCommand(
                        proposePeaceEvent,
                    )

                    game.universeClient.currentCommand = addEventCommand
                }
                nestedTable.add(proposePeaceButton)

                nestedTable.row().space(10f)

                val surrenderButton = createTextButton(
                    "Surrender",
                    gdxSettings.smallFontSize,
                    gdxSettings.soundEffectsVolume,
                    extraColor = commandButtonColor,
                ) {
                    val surrenderCommand = SurrenderCommand(
                        toId = game.universeClient.getCurrentPlayerData().playerId,
                        targetPlayerId = otherPlayerId
                    )

                    game.universeClient.currentCommand = surrenderCommand
                }
                nestedTable.add(surrenderButton)
            }

            // Create this table if the selected player is a subordinate of the game player
            // and the game player has joined the war
            val isCurrentPlayerInThisWar: Boolean = game.universeClient.getUniverseData3D()
                .getCurrentPlayerData().playerInternalData.diplomacyData()
                .relationData.hasSubordinateWar(playerData.playerId, otherPlayerId)
            if (isCurrentPlayerInThisWar) {
                nestedTable.row().space(10f)

                val allyIdList: List<Int> = game.universeClient.getUniverseData3D()
                    .getCurrentPlayerData().playerInternalData.diplomacyData()
                    .relationData.allyMap.keys.toList()

                // Add call ally to war table if ally is not empty
                if (allyIdList.isNotEmpty()) {
                    nestedTable.add(
                        createCallAllyToSubordinateWarTable(
                            allyIdList = allyIdList,
                            subordinateId = playerData.playerId,
                            warTargetId = otherPlayerId,
                        )
                    )
                }
            }
        }

        return nestedTable
    }

    /**
     * Display information about the war between primarily selected player's
     * subordinate/ally/ally's subordinate and other selected player if the primarily selected
     * player is involved in the war
     */
    private fun createOtherWarTable(): Table {
        val nestedTable = Table()

        val isInSubordinateWar: Boolean = playerData.playerInternalData.diplomacyData()
            .relationData.subordinateWarDataMap.values.any {
                it.containsKey(otherPlayerId)
            }

        val isInAllyWar: Boolean = playerData.playerInternalData.diplomacyData()
            .relationData.allyWarDataMap.values.any {
                it.containsKey(otherPlayerId)
            }

        val isInAllySubordinateWar: Boolean = playerData.playerInternalData.diplomacyData()
            .relationData.allySubordinateWarDataMap.values.any { allySubordinateMap ->
                allySubordinateMap.values.any {
                    it.containsKey(otherPlayerId)
                }
            }

        if (isInSubordinateWar || isInAllyWar || isInAllySubordinateWar) {
            nestedTable.add(
                createLabel(
                    "In war (other)",
                    gdxSettings.normalFontSize
                )
            ).colspan(2)
        }

        if (isInSubordinateWar) {
            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Subordinate: ",
                    gdxSettings.smallFontSize
                )
            )

            val inWarSubordinateList: List<Int> = playerData.playerInternalData.diplomacyData()
                .relationData.subordinateWarDataMap.filterValues {
                    it.containsKey(otherPlayerId)
                }.keys.toList()

            nestedTable.add(
                createSelectBox(
                    inWarSubordinateList,
                    inWarSubordinateList.first(),
                    gdxSettings.smallFontSize
                )
            )
        }

        if (isInAllyWar) {
            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Ally: ",
                    gdxSettings.smallFontSize
                )
            )

            val inWarAllyList: List<Int> = playerData.playerInternalData.diplomacyData()
                .relationData.allyWarDataMap.filterValues {
                    it.containsKey(otherPlayerId)
                }.keys.toList()

            nestedTable.add(
                createSelectBox(
                    inWarAllyList,
                    inWarAllyList.first(),
                    gdxSettings.smallFontSize
                )
            )
        }

        if (isInAllySubordinateWar) {
            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Ally's subordinate: ",
                    gdxSettings.smallFontSize
                )
            )

            val inWarAllySubordinateList: List<Pair<Int, Int>> = playerData.playerInternalData
                .diplomacyData().relationData.allySubordinateWarDataMap
                .flatMap { (allyId, allySubordinateMap) ->
                    allySubordinateMap.filterValues {
                        it.containsKey(otherPlayerId)
                    }.keys.map {
                        Pair(allyId, it)
                    }
                }

            nestedTable.add(
                createSelectBox(
                    inWarAllySubordinateList,
                    inWarAllySubordinateList.first(),
                    gdxSettings.smallFontSize,
                )
            )
        }

        return nestedTable
    }

    private fun createWarCommandTable(): Table {
        val nestedTable = Table()

        val currentPlayerData: PlayerData = game.universeClient.getCurrentPlayerData()

        if (currentPlayerData.playerId != playerData.playerId) {
            nestedTable.add(
                createLabel(
                    "War commands: ",
                    gdxSettings.normalFontSize
                )
            )
        }

        if (!currentPlayerData.isLeaderOrSelf(playerData.playerId) &&
            !currentPlayerData.isSubOrdinate(playerData.playerId)
        ) {
            nestedTable.row().space(10f)

            val declareWarButton = createTextButton(
                "Declare war",
                gdxSettings.smallFontSize,
                gdxSettings.soundEffectsVolume,
                extraColor = commandButtonColor,
            ) {
                val declareWarCommand = DeclareWarCommand(
                    toId = playerData.playerId,
                )

                game.universeClient.currentCommand = declareWarCommand
            }
            nestedTable.add(declareWarButton)
        }

        if (!currentPlayerData.isTopLeader() &&
            (currentPlayerData.playerInternalData.directLeaderId == playerData.playerId)
        ) {
            nestedTable.row().space(10f)

            val declareIndependenceToDirectLeaderButton = createTextButton(
                "Declare independence (direct leader)",
                gdxSettings.smallFontSize,
                gdxSettings.soundEffectsVolume,
                extraColor = commandButtonColor,
            ) {
                val declareIndependenceToDirectLeaderCommand =
                    DeclareIndependenceToDirectLeaderCommand(
                        toId = playerData.playerId,
                    )

                game.universeClient.currentCommand = declareIndependenceToDirectLeaderCommand
            }
            nestedTable.add(declareIndependenceToDirectLeaderButton)
        }

        if (!currentPlayerData.isTopLeader() &&
            (currentPlayerData.topLeaderId() == playerData.playerId)
        ) {
            nestedTable.row().space(10f)

            val declareIndependenceToTopLeaderButton = createTextButton(
                "Declare independence (top leader)",
                gdxSettings.smallFontSize,
                gdxSettings.soundEffectsVolume,
                extraColor = commandButtonColor,
            ) {
                val declareIndependenceToTopLeaderCommand = DeclareIndependenceToTopLeaderCommand(
                    toId = playerData.playerId,
                )

                game.universeClient.currentCommand = declareIndependenceToTopLeaderCommand
            }
            nestedTable.add(declareIndependenceToTopLeaderButton)
        }

        return nestedTable
    }
}

enum class DiplomacyInfoRelationType(val value: String) {
    ALL("All"),
    SELF_WAR_TARGET("Self war target"),
    SELF_DEFENSIVE_WAR_TARGET("Self defensive war target"),
    SELF_OFFENSIVE_WAR_TARGET("Self offensive war target"),
    SUBORDINATE("Subordinate"),
    SUBORDINATE_IN_DEFENSIVE_WAR("Subordinate in defensive war"),
    SUBORDINATE_WAR_TARGET("Subordinate war target"),
    ALLY("Ally"),
    ALLY_WAR_TARGET("Ally war target"),
    ENEMY("Enemy"),
    ;

    override fun toString(): String {
        return value
    }
}