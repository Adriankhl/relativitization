package relativitization.game.utils

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import relativitization.universe.utils.RelativitizationLogManager

class GdxScrollPane(
    actor: Actor,
    skin: Skin,
    val onSizeChanged: () -> Unit,
) : ScrollPane(actor, skin) {
    override fun sizeChanged() {
        super.sizeChanged()
        try {
            onSizeChanged()
        } catch (e: Throwable) {
            logger.error("onSizeChanged: $e")
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}