package relativitization.game.components.upper.defaults

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import relativitization.game.RelativitizationGame
import relativitization.game.components.upper.UpperInfoPane
import relativitization.universe.core.ai.AI
import relativitization.universe.core.ai.AICollection
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.commands.CommandErrorMessage
import relativitization.universe.core.utils.I18NString
import relativitization.universe.game.ai.DefaultAI
import relativitization.universe.game.data.commands.CannotSendCommand
import relativitization.universe.game.data.commands.DummyCommand
import kotlin.random.Random

class AIInfoPane(val game: RelativitizationGame) : UpperInfoPane<ScrollPane>(game) {
    override val infoName: String = "AI"

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

        table.add(
            createLabel(
                "AI: Player ${game.universeClient.universeClientSettings.playerId}",
                gdxSettings.bigFontSize
            )
        ).pad(20f)

        table.row().space(10f)

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
                "Auto compute: ",
                gdxSettings.smallFontSize,
            )
        )

        val autoAISelectBox = createSelectBox(
            AI::class.sealedSubclasses.map { it.objectInstance!!.name() },
            game.universeClient.autoAIName,
            gdxSettings.smallFontSize
        ) { s, _ ->
            game.universeClient.autoAIName = s
        }
        nestedTable.add(autoAISelectBox)

        nestedTable.row().space(20f)

        nestedTable.add(
            createLabel(
                "Manual compute: ",
                gdxSettings.smallFontSize,
            )
        )

        val manualAISelectBox = createSelectBox(
            AI::class.sealedSubclasses.map { it.objectInstance!!.name() },
            aiName,
            gdxSettings.smallFontSize
        ) { s, _ ->
            aiName = s
        }
        nestedTable.add(manualAISelectBox).pad(10f)

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
                Random(System.currentTimeMillis()),
                aiName,
            )

            aiCommandList.clear()

            aiCommandList.addAll(commandList)

            updateTable()
        }
        nestedTable.add(computeButton).pad(5f)

        nestedTable.row()

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
            aiCommandList.removeAll(toRemoveList.toSet())
            updateTable()

            // Some commands are not executed successfully
            if (successList.any { !it.success }) {
                val successNum: Int = successList.filter { it.success }.size
                val failedNum: Int = successList.filter { !it.success }.size
                game.universeClient.currentCommand = CannotSendCommand(
                    reason = I18NString(
                        singleMessage = "Success: $successNum, " +
                                "failed (include execute failure on other player): $failedNum. "
                    )
                )
            } else {
                game.universeClient.currentCommand = game.universeClient.planDataAtPlayer
                    .commandList.lastOrNull() ?: DummyCommand()
            }
        }
        nestedTable.add(useAllButton).pad(5f)

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

        val commandDescriptionLabel = createLabel(
            command.description(game.universeClient.universeClientSettings.playerId),
            gdxSettings.smallFontSize
        )
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