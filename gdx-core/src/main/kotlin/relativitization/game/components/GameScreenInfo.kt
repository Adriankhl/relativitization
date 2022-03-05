package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import relativitization.game.RelativitizationGame
import relativitization.game.components.bottom.BottomCommandInfo
import relativitization.game.components.upper.*
import relativitization.game.utils.ScreenComponent
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.full.primaryConstructor

class GameScreenInfo(val game: RelativitizationGame) : ScreenComponent<SplitPane>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private val upperInfoMap: Map<String, UpperInfo<Actor>> = UpperInfo::class.sealedSubclasses.associate {
        val info = it.primaryConstructor!!.call(game)
        info.infoName to info
    }

    private val upperInfoContainer: Container<Actor> = Container(
        getCurrentUpperInfoComponent().getScreenComponent()
    )
    private val bottomCommandInfo: BottomCommandInfo = BottomCommandInfo(game)

    private val infoAndCommand = createSplitPane(
        upperInfoContainer,
        bottomCommandInfo.getScreenComponent(),
        true
    )


    init {
        // Add child screen component
        addChildScreenComponent(bottomCommandInfo)

        addAllComponentToClient(game, getCurrentUpperInfoComponent())

        upperInfoContainer.fill()

        infoAndCommand.splitAmount = gdxSettings.upperInfoAndBottomCommandSplitAmount
    }

    override fun getScreenComponent(): SplitPane {
        return infoAndCommand
    }

    override fun onGdxSettingsChange() {
        // Show upper info based on setting
        upperInfoContainer.actor = getCurrentUpperInfoComponent().getScreenComponent()

        // Show bottom command or not
        if (gdxSettings.showingBottomCommand) {
            infoAndCommand.splitAmount = gdxSettings.upperInfoAndBottomCommandSplitAmount
        } else {
            // Only update the stored split amount of the split screen is not too close to the edge
            if (infoAndCommand.splitAmount < infoAndCommand.maxSplitAmount * 0.9) {
                gdxSettings.upperInfoAndBottomCommandSplitAmount = infoAndCommand.splitAmount
            }
            infoAndCommand.splitAmount = infoAndCommand.maxSplitAmount
        }
    }

    private fun getCurrentUpperInfoComponent(): ScreenComponent<Actor> {
        return upperInfoMap.getOrElse(gdxSettings.showingUpperInfo) {
            upperInfoMap.values.first()
        }
    }

    fun reRegisterUpperInfoComponent() {
        upperInfoMap.values.forEach { removeAllComponentFromClient(game, it) }
        addAllComponentToClient(game, getCurrentUpperInfoComponent())
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}