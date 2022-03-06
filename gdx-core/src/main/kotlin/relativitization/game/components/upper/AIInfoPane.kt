package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.universe.ai.AI
import relativitization.universe.ai.AICollection
import relativitization.universe.ai.DefaultAI
import relativitization.universe.ai.name
import relativitization.universe.data.commands.*
import relativitization.universe.utils.I18NString

class AIInfoPane(val game: RelativitizationGame) : UpperInfoPane<ScrollPane>(game) {
    override val infoName: String = "AI"

    override val infoPriority: Int = 2

    private val gdxSettings = game.gdxSettings

    private val table: Table = Table()

    private val scrollPane: ScrollPane = createScrollPane(table)

    private var aiName: String = DefaultAI.name()

    private val aiCommandList: MutableList<Command> = mutableListOf()

    init {
        // Set background color
        table.background = assets.getBackgroundColor(0.2f, 0.2f, 0.2f, 1.0f)

        // Configure scroll pane
        scrollPane.fadeScrollBars = false
        scrollPane.setClamp(true)
        scrollPane.setOverscroll(false, false)

        updateTable()
    }

    override fun getScreenComponent(): ScrollPane {
        return scrollPane
    }

    private fun updateTable() {
        table.clear()

        val headerLabel = createLabel(
            "AI: Player ${game.universeClient.universeClientSettings.playerId}",
            gdxSettings.bigFontSize
        )

        table.add(headerLabel).pad(20f)

        table.row().space(20f)

        table.add(createAISelectionTable())

        table.row().space(10f)

        table.add(createAIComputeTable())

        table.row().space(20f)

        table.add(createAllAICommandTable()).growX()
    }

    private fun createAISelectionTable(): Table {
        val nestedTable = Table()

        nestedTable.add(
            createLabel(
                "AI: ",
                gdxSettings.smallFontSize,
            )
        )

        val aiSelectBox = createSelectBox(
            AI::class.sealedSubclasses.map { it.objectInstance!!.name() },
            aiName,
            gdxSettings.smallFontSize
        ) { s, _ ->
            aiName = s
        }
        nestedTable.add(aiSelectBox)

        return nestedTable
    }

    private fun createAIComputeTable(): Table {
        val nestedTable = Table()

        val computeButton = createTextButton(
            "Compute",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            val commandList: List<Command> = AICollection.compute(
                game.universeClient.getUniverseData3D(),
                aiName
            )

            aiCommandList.clear()

            aiCommandList.addAll(commandList)

            updateTable()
        }
        nestedTable.add(computeButton)

        nestedTable.row().space(20f)

        val useAllButton = createTextButton(
            "Use all",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
            commandButtonColor,
        ) {
            game.universeClient.clearCommandList()

            val successList: List<CommandErrorMessage> = game.universeClient.planDataAtPlayer
                .addAllCommand(aiCommandList)

            // Clear added commands, left failed ones
            val toRemoveList: List<Command> = aiCommandList.filterIndexed { index, _ ->
                successList[index].success
            }
            aiCommandList.removeAll(toRemoveList)
            updateTable()

            // Some commands are not executed successfully
            if (successList.any { !it.success }) {
                val successNum: Int = successList.filter { it.success }.size
                val failedNum: Int = successList.filter { !it.success }.size
                game.universeClient.currentCommand = CannotSendCommand(
                    reason = I18NString(singleMessage = "Success: $successNum, " +
                            "failed (include execute failure on other player): $failedNum. ")
                )
            } else {
                game.universeClient.currentCommand = game.universeClient.planDataAtPlayer
                    .commandList.lastOrNull() ?: DummyCommand()
            }
        }
        nestedTable.add(useAllButton)

        return nestedTable
    }

    private fun createAllAICommandTable(): Table {
        val nestedTable = Table()

        aiCommandList.forEach {
            nestedTable.add(createAICommandTable(it)).growX()

            nestedTable.row().space(20f)
        }

        return nestedTable
    }

    private fun createAICommandTable(command: Command): Table {
        val nestedTable = Table()

        nestedTable.background = assets.getBackgroundColor(0.25f, 0.25f, 0.25f, 1.0f)

        val commandNameLabel = createLabel(command.name(), gdxSettings.normalFontSize)
        nestedTable.add(commandNameLabel).growX().colspan(2)

        nestedTable.row().space(10f)

        val commandDescriptionLabel = createLabel(command.description(), gdxSettings.smallFontSize)
        commandDescriptionLabel.wrap = true
        nestedTable.add(commandDescriptionLabel).growX().colspan(2)

        nestedTable.row().space(10f)

        val addButton = createTextButton(
            "Add",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume,
        ) {
            aiCommandList.remove(command)
            game.universeClient.currentCommand = command
            if (game.universeClient.currentCommand !is CannotSendCommand) {
                game.universeClient.confirmCurrentCommand()
            }
            updateTable()
        }

        nestedTable.add(addButton)

        val cancelButton = createTextButton(
            "Remove",
            gdxSettings.smallFontSize,
            gdxSettings.soundEffectsVolume
        ) {
            aiCommandList.remove(command)
            updateTable()
        }

        nestedTable.add(cancelButton)

        return nestedTable
    }
}