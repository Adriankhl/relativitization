package relativitization.game.utils

import com.badlogic.gdx.scenes.scene2d.ui.Image
import relativitization.universe.data.PlayerData
import relativitization.universe.data.PlayerType
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

        val imageList: MutableList<Image> = mutableListOf()
        
        val playerId = playerData.id

        val hasStellarSystem: Boolean = playerData.playerInternalData.popSystemicData.carrier.map {
            it.carrierType == CarrierType.STELLAR
        }.contains(true)

        if (hasStellarSystem)  {
            val stellarImage: Image = assets.getImage("system/sun")
            stellarImage.setPosition(xPos, yPos)
            stellarImage.setSize(width, height)
            imageList.add(stellarImage)
        }

        if (playerData.playerType != PlayerType.NONE) {
            val playerShipImage: Image = assets.getImage(playerId, "system/ship1", xPos, yPos, width, height)
            imageList.add(playerShipImage)
        }

        return imageList
    }
}