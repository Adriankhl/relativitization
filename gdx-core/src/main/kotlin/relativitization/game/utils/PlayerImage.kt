package relativitization.game.utils

import com.badlogic.gdx.scenes.scene2d.ui.Image
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.popsystems.CarrierType

object PlayerImage {
    fun getPlayerImages(
        playerData: PlayerData,
        assets: Assets,
        xPos: Float,
        yPos: Float,
        width: Float,
        height: Float,
    ): List<Image> {
        val playerId = playerData.id

        val playerShipImage: Image = assets.getImage(playerId, "system/ship1", xPos, yPos, width, height)

        val hasStellarSystem: Boolean = playerData.playerInternalData.popSystemicData.carrier.map {
            it.carrierType == CarrierType.STELLAR
        }.contains(true)

        return if (hasStellarSystem) {
            val stellarImage: Image = assets.getImage("system/sun")
            stellarImage.setPosition(xPos, yPos)
            stellarImage.setSize(width, height)
            listOf(stellarImage, playerShipImage)
        } else {
            listOf(playerShipImage)
        }
    }
}