package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.*
import relativitization.universe.data.components.defaults.diplomacy.DiplomaticRelationData
import relativitization.universe.data.components.defaults.diplomacy.DiplomaticRelationState
import relativitization.universe.data.components.defaults.diplomacy.WarStateData
import relativitization.universe.data.components.diplomacyData

class DiplomacyInfoPane(val game: RelativitizationGame) : UpperInfoPane<ScrollPane>(game) {
    override val infoName: String = "Diplomacy"

    override val infoPriority: Int = 11

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

        table.add(createWarStateTable())

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
            DiplomacyInfoRelationType.ALL -> game.universeClient.getUniverseData3D().playerDataMap.keys.toList()
            DiplomacyInfoRelationType.WAR -> playerData.playerInternalData.diplomacyData().warData.warStateMap.keys
                .toList()
            DiplomacyInfoRelationType.WAR_AND_ENEMY -> (playerData.playerInternalData.diplomacyData().warData.warStateMap
                .keys + playerData.playerInternalData.diplomacyData().relationMap.filterValues {
                it.diplomaticRelationState == DiplomaticRelationState.ENEMY
            }.keys).toList()
            DiplomacyInfoRelationType.OTHER_RELATION -> playerData.playerInternalData.diplomacyData().relationMap
                .filterValues { it.diplomaticRelationState != DiplomaticRelationState.ENEMY }.keys.toList()
        }.sorted()
    }

    private fun createDiplomaticRelationTable(): Table {
        val nestedTable = Table()

        // Only show this information if the other player is not self
        if (playerData.playerId != otherPlayerId) {
            nestedTable.add(
                createLabel(
                    "Relation with: $otherPlayerId",
                    gdxSettings.normalFontSize
                )
            )

            nestedTable.row().space(10f)

            val diplomaticRelationData: DiplomaticRelationData =
                playerData.playerInternalData.diplomacyData()
                    .getDiplomaticRelationData(otherPlayerId)

            nestedTable.add(
                createLabel(
                    "Relation: ${diplomaticRelationData.relation}",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Diplomatic state: ${diplomaticRelationData.diplomaticRelationState}",
                    gdxSettings.smallFontSize
                )
            )
        }

        return nestedTable
    }

    private fun createWarStateTable(): Table {
        val nestedTable = Table()

        if (playerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                otherPlayerId
            )
        ) {
            val warState: WarStateData =
                playerData.playerInternalData.diplomacyData().warData.getWarStateData(
                    otherPlayerId
                )

            nestedTable.add(
                createLabel(
                    "In war",
                    gdxSettings.normalFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "War start time: ${warState.startTime}",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "War type: ${
                        if (warState.isOffensive) {
                            "Offensive"
                        } else {
                            "Defensive"
                        }
                    }",
                    gdxSettings.smallFontSize
                )
            )

            nestedTable.row().space(10f)

            nestedTable.add(
                createLabel(
                    "Proposed peace: ${warState.proposePeace}",
                    gdxSettings.smallFontSize
                )
            )

            if (playerData.playerId == game.universeClient.getUniverseData3D().id) {
                nestedTable.row().space(10f)

                val proposePeaceButton = createTextButton(
                    "Propose peace",
                    gdxSettings.smallFontSize,
                    gdxSettings.soundEffectsVolume,
                    extraColor = commandButtonColor,
                ) {
                    val proposePeaceCommand = ProposePeaceCommand(
                        toId = game.universeClient.getCurrentPlayerData().playerId,
                        fromId = game.universeClient.getCurrentPlayerData().playerId,
                        fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                        targetPlayerId = otherPlayerId
                    )

                    game.universeClient.currentCommand = proposePeaceCommand
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
                        fromId = game.universeClient.getCurrentPlayerData().playerId,
                        fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
                        targetPlayerId = otherPlayerId
                    )

                    game.universeClient.currentCommand = surrenderCommand
                }
                nestedTable.add(surrenderButton)
            }
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
                    fromId = game.universeClient.getCurrentPlayerData().playerId,
                    fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
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
                        fromId = game.universeClient.getCurrentPlayerData().playerId,
                        fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
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
                    fromId = game.universeClient.getCurrentPlayerData().playerId,
                    fromInt4D = game.universeClient.getCurrentPlayerData().int4D,
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
    WAR("War"),
    WAR_AND_ENEMY("War and enemy"),
    OTHER_RELATION("Other relation"),
    ;

    override fun toString(): String {
        return value
    }
}