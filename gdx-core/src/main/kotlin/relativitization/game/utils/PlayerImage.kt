package relativitization.game.utils

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import relativitization.universe.data.PlayerData
import relativitization.universe.data.PlayerType
import relativitization.universe.data.components.default.popsystem.CarrierType
import relativitization.universe.utils.RelativitizationLogManager

object PlayerImage {
    private val logger = RelativitizationLogManager.getLogger()

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

        val playerId = playerData.playerId

        val hasStellarSystem: Boolean = playerData.playerInternalData.popSystemData().carrierDataMap.any {
            it.value.carrierType == relativitization.universe.data.components.default.popsystem.CarrierType.STELLAR
        }

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
            playerShipImage.setOrigin(Align.center)
            playerShipImage.rotation = rotationByVelocity(playerData)
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

    /**
     * Compute the image direction of player ship by the velocity
     * The velocity is 3 dimensional so it is projected to a circle
     *
     * Main velocity component
     * vx: x axis
     * vy: y axis
     * vz: 45 degree axis
     *
     * Other component adjust the angle
     */
    private fun rotationByVelocity(
        playerData: PlayerData
    ): Float {
        val velocity = playerData.velocity
        val maxComponent: Pair<Char, Double> = velocity.maxComponent()
        val magMaxComponent: Double = maxComponent.second

        val degree: Float = if (maxComponent.second < 0.000001f) {
            0.0f
        } else if (maxComponent.first == 'x') {
            if (velocity.vx > 0) {
                val primaryDirection: Float = 90f
                val adjusted: Float = (- velocity.vy / magMaxComponent * 15 - velocity.vz / magMaxComponent * 5).toFloat()
                primaryDirection + adjusted
            } else  {
                val primaryDirection: Float = 270f
                val adjusted: Float = (velocity.vy / magMaxComponent * 15 + velocity.vz / magMaxComponent * 5).toFloat()
                primaryDirection + adjusted
            }
        } else if (maxComponent.first == 'y') {
            if (velocity.vy > 0) {
                val primaryDirection: Float = 0f
                val adjusted: Float = (velocity.vx / magMaxComponent * 15 + velocity.vz / magMaxComponent * 5).toFloat()
                primaryDirection + adjusted
            } else  {
                val primaryDirection: Float = 180f
                val adjusted: Float = (- velocity.vx / magMaxComponent * 15 - velocity.vz / magMaxComponent * 5).toFloat()
                primaryDirection + adjusted
            }
        } else if (maxComponent.first == 'z') {
            if (velocity.vz > 0) {
                val primaryDirection: Float = 45f
                val adjusted: Float = (velocity.vx / magMaxComponent * 15 - velocity.vy / magMaxComponent * 5).toFloat()
                primaryDirection + adjusted
            } else  {
                val primaryDirection: Float = 225f
                val adjusted: Float = (- velocity.vx / magMaxComponent * 15 + velocity.vy / magMaxComponent * 5).toFloat()
                primaryDirection + adjusted
            }
        } else {
            logger.error("Wrong velocity maxComponent character")
            0.0f
        }


        return 360f - degree
    }
}