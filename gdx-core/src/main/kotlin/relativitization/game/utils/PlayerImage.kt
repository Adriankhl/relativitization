package relativitization.game.utils

import com.badlogic.gdx.scenes.scene2d.ui.Image
import relativitization.universe.data.UniverseData3DAtPlayer

object PlayerImage {
    fun getPlayerImages(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        assets: Assets,
        xPos: Float,
        yPos: Float,
        width: Float,
        height: Float,
    ): List<Image> {
        val playerShip: Image = assets.getImage(universeData3DAtPlayer.id, "system/ship1", xPos, yPos, width, height)

        return if (universeData3DAtPlayer.get(universeData3DAtPlayer.id).playerInternalData.popSystemicData.carrier.)
    }
}