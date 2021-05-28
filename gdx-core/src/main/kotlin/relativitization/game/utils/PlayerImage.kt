package relativitization.game.utils

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import relativitization.universe.data.PlayerData
import relativitization.universe.data.PlayerType
import relativitization.universe.data.popsystems.CarrierType

object PlayerImage {
    fun getPlayerImages(
        playerData: PlayerData,
        assets: Assets,
        xPos: Float,
        yPos: Float,
        width: Float,
        height: Float,
        soundVolume: Float,
        function: (Image) -> Unit = {}
    ): List<Image> {

        val imageList: MutableList<Image> = mutableListOf()

        val playerId = playerData.id

        val hasStellarSystem: Boolean = playerData.playerInternalData.popSystemicData.carrier.map {
            it.carrierType == CarrierType.STELLAR
        }.contains(true)

        if (hasStellarSystem) {
            val stellarImage: Image = ActorFunction.createImage(assets, "system/sun", soundVolume)
            stellarImage.setPosition(xPos, yPos)
            stellarImage.setSize(width, height)
            imageList.add(stellarImage)
        }

        if (playerData.playerType != PlayerType.NONE) {
            val playerShipImage: Image = ActorFunction.createImage(
                assets,
                playerId,
                "system/ship1",
                xPos,
                yPos,
                width,
                height,
                soundVolume,
            )
            imageList.add(playerShipImage)
        }

        // Add an transparent square on top for selecting player
        val transparentSquare: Image = ActorFunction.createImage(
            assets,
            "basic/white-pixel",
            xPos,
            yPos,
            width,
            height,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            soundVolume,
            function
        )
        imageList.add(transparentSquare)

        return imageList
    }

    fun getPlayerImageStack(
        playerData: PlayerData,
        assets: Assets,
        width: Float,
        height: Float,
        soundVolume: Float,
        function: (Image) -> Unit = {}
    ): Stack {
        val imageList = getPlayerImages(playerData, assets, 0f, 0f, width, height, soundVolume, function)
        val stack = Stack()
        for (image in imageList) {
            stack.add(image)
        }

        return stack
    }
}