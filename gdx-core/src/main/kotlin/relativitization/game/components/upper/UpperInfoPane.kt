package relativitization.game.components.upper

import com.badlogic.gdx.scenes.scene2d.Actor
import relativitization.game.RelativitizationGame
import relativitization.game.utils.ScreenComponent

abstract class UpperInfoPane<out T : Actor>(
    game: RelativitizationGame
) : ScreenComponent<T>(game.assets) {
    // The name of the upper info
    abstract val infoName: String

    // The priority of this info to show on the left, the lower, the more towards the left
    abstract val infoPriority: Int
}

sealed class UpperInfoPaneList {
    abstract val upperInfoPaneList: List<UpperInfoPane<Actor>>
}

fun UpperInfoPaneList.name(): String = this::class.simpleName.toString()

object UpperInfoPaneCollection {
    val upperInfoPaneListMap: Map<String, UpperInfoPaneList> = UpperInfoPaneList::class
        .sealedSubclasses.map {
            it.objectInstance!!
        }.associateBy { it.name() }
}