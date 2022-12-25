package relativitization.game.utils

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin

/**
 * A scroll pane which call a function when size change
 *
 * @property onSizeChanged the function to call, ideally this should not be nullable,
 *  but jvm behaves weirdly and introduce null pointer exception,
 *  so let it be nullable here and safe call the invoke
 */
class GdxScrollPane(
    actor: Actor,
    skin: Skin,
    private val onSizeChanged: (() -> Unit)?,
) : ScrollPane(actor, skin) {
    override fun sizeChanged() {
        super.sizeChanged()
        onSizeChanged?.invoke()
    }
}