package relativitization.game.utils

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class GdxScrollPane(
    actor: Actor,
    skin: Skin,
    val onSizeChanged: () -> Unit,
) : ScrollPane(actor, skin) {
    override fun sizeChanged() {
        super.sizeChanged()
        onSizeChanged()
    }
}