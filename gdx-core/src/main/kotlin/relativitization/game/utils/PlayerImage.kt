package relativitization.game.utils

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.PlayerData
import relativitization.universe.data.PlayerType
import relativitization.universe.data.popsystems.CarrierType
import kotlin.math.abs

object PlayerImage {
    private val logger = LogManager.getLogger()

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
        val velocity = playerData.playerInternalData.physicsData.velocity
        val maxComponent = velocity.maxComponent()

        val degree: Float = if (maxComponent.second < 0.000001f) {
            0.0f
        } else if (maxComponent.first == 'x') {
            if (maxComponent.second > 0) {
                val primaryDirection: Float = 90f
                val adjusted: Float = (- velocity.vy / velocity.vx * 15 - velocity.vz / velocity.vx * 5).toFloat()
                primaryDirection + adjusted
            } else  {
                val primaryDirection: Float = 270f
                val adjusted: Float = (velocity.vy / abs(velocity.vx) * 15 + velocity.vz / abs(velocity.vx) * 5).toFloat()
                primaryDirection + adjusted
            }
        } else if (maxComponent.first == 'y') {
            if (maxComponent.second > 0) {
                val primaryDirection: Float = 0f
                val adjusted: Float = (velocity.vx / velocity.vy * 15 + velocity.vz / velocity.vy * 5).toFloat()
                primaryDirection + adjusted
            } else  {
                val primaryDirection: Float = 180f
                val adjusted: Float = (- velocity.vx / abs(velocity.vy) * 15 - velocity.vz / abs(velocity.vy) * 5).toFloat()
                primaryDirection + adjusted
            }
        } else if (maxComponent.first == 'z') {
            if (maxComponent.second > 0) {
                val primaryDirection: Float = 45f
                val adjusted: Float = (velocity.vx / velocity.vz * 15 - velocity.vy / velocity.vz * 5).toFloat()
                primaryDirection + adjusted
            } else  {
                val primaryDirection: Float = 225f
                val adjusted: Float = (- velocity.vx / abs(velocity.vz) * 15 + velocity.vy / abs(velocity.vz) * 5).toFloat()
                primaryDirection + adjusted
            }
        } else {
            logger.error("Wrong velocity maxComponent character")
            0.0f
        }


        return 360f - degree
    }
}