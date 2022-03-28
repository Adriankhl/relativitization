package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.Actor
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

abstract class UpperInfoPane<out T : Actor>(
    game: RelativitizationGame
) : ScreenComponent<T>(game.assets) {
    // The name of the upper info
    abstract val infoName: String
}

sealed class UpperInfoPaneList {
    abstract fun getUpperInfoPaneList(game: RelativitizationGame): List<UpperInfoPane<Actor>>
}

fun UpperInfoPaneList.name(): String = this::class.simpleName.toString()

object UpperInfoPaneCollection {
    val upperInfoPaneListMap: Map<String, UpperInfoPaneList> = UpperInfoPaneList::class
        .sealedSubclasses.map {
            it.objectInstance!!
        }.associateBy { it.name() }
}