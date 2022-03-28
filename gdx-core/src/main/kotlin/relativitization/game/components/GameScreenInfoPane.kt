package relativitization.game.components

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import relativitization.game.RelativitizationGame
import relativitization.game.components.bottom.BottomCommandInfoPane
import relativitization.game.components.upper.DefaultUpperInfoPaneList
import relativitization.game.components.upper.UpperInfoPane
import relativitization.game.components.upper.UpperInfoPaneCollection
import relativitization.game.utils.ScreenComponent
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.reflect.full.primaryConstructor

class GameScreenInfoPane(
    val game: RelativitizationGame,
) : ScreenComponent<SplitPane>(game.assets) {
    private val gdxSettings = game.gdxSettings

    private val upperInfoPaneMap: Map<String, UpperInfoPane<Actor>> = UpperInfoPaneCollection
        .upperInfoPaneListMap.getValue(
            gdxSettings.upperInfoPaneListName,
        ).getUpperInfoPaneList(game).associateBy {
            it.infoName
        }

    private val upperInfoPaneContainer: Container<Actor> = Container(
        getCurrentUpperInfoComponent().getScreenComponent()
    )
    private val bottomCommandInfoPane: BottomCommandInfoPane = BottomCommandInfoPane(game)

    val fullInfoPane = createSplitPane(
        upperInfoPaneContainer,
        bottomCommandInfoPane.getScreenComponent(),
        true
    )

    init {
        // Add child screen component
        addChildScreenComponent(bottomCommandInfoPane)

        addAllComponentToClient(game, getCurrentUpperInfoComponent())

        upperInfoPaneContainer.fill()

        fullInfoPane.splitAmount = gdxSettings.infoPaneSplitAmount
    }

    override fun getScreenComponent(): SplitPane {
        return fullInfoPane
    }

    override fun onGdxSettingsChange() {
        // Show upper info based on setting
        upperInfoPaneContainer.actor = getCurrentUpperInfoComponent().getScreenComponent()

        // Show bottom command or not
        if (gdxSettings.isBottomCommandInfoPaneShow) {
            fullInfoPane.splitAmount = gdxSettings.infoPaneSplitAmount
        } else {
            // Only update the stored split amount of the split screen is not too close to the edge
            if (fullInfoPane.splitAmount < fullInfoPane.maxSplitAmount * 0.9) {
                gdxSettings.infoPaneSplitAmount = fullInfoPane.splitAmount
            }
            fullInfoPane.splitAmount = fullInfoPane.maxSplitAmount
        }
    }

    private fun getCurrentUpperInfoComponent(): ScreenComponent<Actor> {
        return upperInfoPaneMap.getOrElse(gdxSettings.upperInfoPaneChoice) {
            upperInfoPaneMap.values.first()
        }
    }

    fun reRegisterUpperInfoComponent() {
        upperInfoPaneMap.values.forEach { removeAllComponentFromClient(game, it) }
        addAllComponentToClient(game, getCurrentUpperInfoComponent())
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}