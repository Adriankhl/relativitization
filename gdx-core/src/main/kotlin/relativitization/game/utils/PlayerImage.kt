package relativitization.game.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import relativitization.game.MapPlayerColorMode
import relativitization.universe.data.PlayerData
import relativitization.universe.data.PlayerType
import relativitization.universe.data.components.defaults.diplomacy.DiplomaticRelationState
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random

object PlayerImage {
    private val logger = RelativitizationLogManager.getLogger()

    fun getPlayerImages(
        playerData: PlayerData,
        primaryPlayerData: PlayerData,
        assets: Assets,
        xPos: Float,
        yPos: Float,
        width: Float,
        height: Float,
        soundVolume: Float,
        mapPlayerColorMode: MapPlayerColorMode,
        function: (Image) -> Unit = {}
    ): List<Image> {

        val imageList: MutableList<Image> = mutableListOf()

        // determine the color of the player image by map mode and player data
        val playerColor: Color = when (mapPlayerColorMode) {
            MapPlayerColorMode.ONE_COLOR_PER_PLAYER -> {
                val random = Random(playerData.playerId)
                val r = random.nextFloat()
                val g = random.nextFloat()
                val b = random.nextFloat()
                Color(r, g, b, 1f)
            }
            MapPlayerColorMode.TOP_LEADER -> {
                val random = Random(playerData.topLeaderId())
                val r = random.nextFloat()
                val g = random.nextFloat()
                val b = random.nextFloat()
                Color(r, g, b, 1f)
            }
            MapPlayerColorMode.WAR_STATE -> {
                when {
                    playerData.playerId == primaryPlayerData.playerId -> {
                        Color(0f, 1f, 0f, 1f)
                    }
                    primaryPlayerData.isLeader(playerData.playerId) -> {
                        Color(0f, 0.5f, 0f, 1f)
                    }
                    primaryPlayerData.isSubOrdinate(playerData.playerId) -> {
                        Color(0f, 0.8f, 0f, 1f)
                    }
                    primaryPlayerData.playerInternalData.diplomacyData().getRelationState(
                        playerData.playerId
                    ) == DiplomaticRelationState.ENEMY -> {
                        Color(1f, 0f, 0f, 1f)
                    }
                    primaryPlayerData.playerInternalData.diplomacyData().isEnemyOf(playerData) -> {
                        Color(0.5f, 0f, 0f, 1f)
                    }
                    else -> {
                        Color(0f, 0f ,0f, 1f)
                    }
                }
            }
        }

        val hasStellarSystem: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.any {
                it.value.carrierType == CarrierType.STELLAR
            }

        val hasSpaceship: Boolean =
            playerData.playerInternalData.popSystemData().carrierDataMap.any {
                it.value.carrierType == CarrierType.SPACESHIP
            }

        if (hasStellarSystem) {
            val stellarImage: Image = ActorFunction.createImage(assets, "system/sun", soundVolume)
            stellarImage.setPosition(xPos, yPos)
            stellarImage.setSize(width, height)
            imageList.add(stellarImage)
        }

        if (playerData.playerType != PlayerType.NONE) {
            val playerShipImage: Image = if (hasSpaceship) {
                if (playerData.isTopLeader()) {
                    ActorFunction.createImage(
                        assets = assets,
                        name = "system/ship2",
                        xPos = xPos,
                        yPos = yPos,
                        width = width,
                        height = height,
                        r = playerColor.r,
                        g = playerColor.g,
                        b = playerColor.b,
                        a = playerColor.a,
                        soundVolume = soundVolume,
                    )
                } else {
                    ActorFunction.createImage(
                        assets = assets,
                        name = "system/ship1",
                        xPos = xPos,
                        yPos = yPos,
                        width = width,
                        height = height,
                        r = playerColor.r,
                        g = playerColor.g,
                        b = playerColor.b,
                        a = playerColor.a,
                        soundVolume = soundVolume,
                    )
                }
            } else {
                if (playerData.isTopLeader()) {
                    ActorFunction.createImage(
                        assets = assets,
                        name = "system/no-ship2",
                        xPos = xPos,
                        yPos = yPos,
                        width = width,
                        height = height,
                        r = playerColor.r,
                        g = playerColor.g,
                        b = playerColor.b,
                        a = playerColor.a,
                        soundVolume = soundVolume,
                    )
                } else {
                    ActorFunction.createImage(
                        assets = assets,
                        name = "system/no-ship1",
                        xPos = xPos,
                        yPos = yPos,
                        width = width,
                        height = height,
                        r = playerColor.r,
                        g = playerColor.g,
                        b = playerColor.b,
                        a = playerColor.a,
                        soundVolume = soundVolume,
                    )
                }
            }
            playerShipImage.setOrigin(Align.center)
            playerShipImage.rotation = rotationByVelocity(playerData)
            imageList.add(playerShipImage)

        }

        // Add an transparent square on top for selecting player
        val transparentSquare: Image = ActorFunction.createImage(
            assets = assets,
            name = "basic/white-pixel",
            xPos = xPos,
            yPos = yPos,
            width = width,
            height = height,
            r = 0.0f,
            g = 0.0f,
            b = 0.0f,
            a = 0.0f,
            soundVolume = soundVolume,
            function = function
        )
        imageList.add(transparentSquare)

        return imageList
    }

    fun getPlayerImageStack(
        playerData: PlayerData,
        primaryPlayerData: PlayerData,
        assets: Assets,
        width: Float,
        height: Float,
        soundVolume: Float,
        mapPlayerColorMode: MapPlayerColorMode,
        function: (Image) -> Unit = {}
    ): Stack {
        val imageList = getPlayerImages(
            playerData = playerData,
            primaryPlayerData = primaryPlayerData,
            assets = assets,
            xPos = 0f,
            yPos = 0f,
            width = width,
            height = height,
            soundVolume = soundVolume,
            mapPlayerColorMode = mapPlayerColorMode,
            function = function
        )
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
                val adjusted: Float =
                    (-velocity.vy / magMaxComponent * 15 - velocity.vz / magMaxComponent * 5).toFloat()
                primaryDirection + adjusted
            } else {
                val primaryDirection: Float = 270f
                val adjusted: Float =
                    (velocity.vy / magMaxComponent * 15 + velocity.vz / magMaxComponent * 5).toFloat()
                primaryDirection + adjusted
            }
        } else if (maxComponent.first == 'y') {
            if (velocity.vy > 0) {
                val primaryDirection: Float = 0f
                val adjusted: Float =
                    (velocity.vx / magMaxComponent * 15 + velocity.vz / magMaxComponent * 5).toFloat()
                primaryDirection + adjusted
            } else {
                val primaryDirection: Float = 180f
                val adjusted: Float =
                    (-velocity.vx / magMaxComponent * 15 - velocity.vz / magMaxComponent * 5).toFloat()
                primaryDirection + adjusted
            }
        } else if (maxComponent.first == 'z') {
            if (velocity.vz > 0) {
                val primaryDirection: Float = 45f
                val adjusted: Float =
                    (velocity.vx / magMaxComponent * 15 - velocity.vy / magMaxComponent * 5).toFloat()
                primaryDirection + adjusted
            } else {
                val primaryDirection: Float = 225f
                val adjusted: Float =
                    (-velocity.vx / magMaxComponent * 15 + velocity.vy / magMaxComponent * 5).toFloat()
                primaryDirection + adjusted
            }
        } else {
            logger.error("Wrong velocity maxComponent character")
            0.0f
        }


        return 360f - degree
    }
}
