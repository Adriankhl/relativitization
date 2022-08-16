package relativitization.game.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align
import relativitization.game.MapPlayerColorMode
import relativitization.universe.data.PlayerData
import relativitization.universe.data.PlayerType
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.maths.physics.Double3D
import relativitization.universe.maths.physics.Velocity
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sign
import kotlin.random.Random

object PlayerImage {
    private val logger = RelativitizationLogManager.getLogger()

    /**
     * Get a list of player image to add to group / stack
     *
     * @param playerData the image of this player
     * @param universeData3DAtPlayer the universe data
     * @param primaryPlayerData a primary selected player, which may affect the color
     * @param assets game assets
     * @param xPos the x coordinate of the image group
     * @param yPos the y coordinate of the image group
     * @param width the width of the image group
     * @param height the height of the image group
     * @param soundVolume sound volume when clicked
     * @param mapPlayerColorMode how the player image should be colored
     * @param showCombat whether combat information should be shown
     * @param function the function to be called when clicked
     */
    fun getPlayerImages(
        playerData: PlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        primaryPlayerData: PlayerData,
        assets: Assets,
        xPos: Float,
        yPos: Float,
        width: Float,
        height: Float,
        soundVolume: Float,
        mapPlayerColorMode: MapPlayerColorMode,
        showCombat: Boolean,
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
                    primaryPlayerData.playerInternalData.diplomacyData().relationData
                        .isEnemy(playerData.playerId) -> {
                        Color(1f, 0f, 0f, 1f)
                    }
                    playerData.playerInternalData.diplomacyData().relationData
                        .isEnemy(primaryPlayerData.playerId) -> {
                        Color(0.5f, 0f, 0f, 1f)
                    }
                    primaryPlayerData.isLeader(playerData.playerId) -> {
                        Color(1.0f, 1.0f, 0f, 1f)
                    }
                    primaryPlayerData.isSubOrdinate(playerData.playerId) -> {
                        Color(0f, 0.5f, 0f, 1f)
                    }
                    primaryPlayerData.playerInternalData.diplomacyData().relationData
                        .isAlly(playerData.playerId) -> {
                        Color(0f, 0f, 1f, 1f)
                    }
                    primaryPlayerData.topLeaderId() == playerData.topLeaderId() -> {
                        Color(0.5f, 0.5f, 0f, 1f)
                    }
                    else -> {
                        Color(0f, 0f, 0f, 1f)
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
            val stellarImage: Image = ActorFunction.createImage(
                assets = assets,
                name = "system/sun",
                soundVolume = soundVolume
            )
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
            playerShipImage.rotation = rotationByNextPosition(playerData)
            imageList.add(playerShipImage)

        }

        if (showCombat) {
            // Get player in the same cube, excluding self
            val neighborList: List<PlayerData> = universeData3DAtPlayer.getIdMap(
                playerData.int4D.toInt3D()
            ).values.flatten().filter {
                it != playerData.playerId
            }.map {
                universeData3DAtPlayer.get(it)
            }

            // player view the neighbor as enemy, or neighbor that views the player as enemy
            val neighborEnemyList: List<PlayerData> = neighborList.filter { neighbor ->
                playerData.playerInternalData.diplomacyData().relationData.enemyIdSet
                    .contains(neighbor.playerId) ||
                        neighbor.playerInternalData.diplomacyData().relationData.enemyIdSet
                            .contains(playerData.playerId)
            }

            // Determine if it is in combat or not
            val inCombat: Boolean = neighborEnemyList.isNotEmpty()

            // Add a red sword if in Combat
            if (inCombat) {
                val sword: Image = ActorFunction.createImage(
                    assets = assets,
                    name = "combat/sword1",
                    xPos = xPos + width * 0.75f,
                    yPos = yPos,
                    width = width * 0.25f,
                    height = height * 0.5f,
                    r = 1.0f,
                    g = 0.0f,
                    b = 0.0f,
                    a = 1.0f,
                    soundVolume = soundVolume,
                    function = function
                )
                imageList.add(sword)
            }
        }


        // Add a transparent square on top for selecting player
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

    fun getPlayerImageWidgetGroup(
        playerData: PlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        primaryPlayerData: PlayerData,
        assets: Assets,
        width: Float,
        height: Float,
        soundVolume: Float,
        mapPlayerColorMode: MapPlayerColorMode,
        function: (Image) -> Unit = {}
    ): WidgetGroup {
        val imageList = getPlayerImages(
            playerData = playerData,
            universeData3DAtPlayer = universeData3DAtPlayer,
            primaryPlayerData = primaryPlayerData,
            assets = assets,
            xPos = 0f,
            yPos = 0f,
            width = width,
            height = height,
            soundVolume = soundVolume,
            mapPlayerColorMode = mapPlayerColorMode,
            showCombat = false,
            function = function
        )
        val widgetGroup = WidgetGroup()
        for (image in imageList) {
            widgetGroup.addActor(image)
        }

        return widgetGroup
    }

    /**
     * Compute the image direction of player ship by velocity
     * The velocity is 3 dimensional, so it is projected to a circle
     *
     * Main velocity component
     * vx: x-axis
     * vy: y-axis
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

        val degree: Float = when {
            maxComponent.second < 0.000001f -> {
                0.0f
            }
            maxComponent.first == 'x' -> {
                if (velocity.vx > 0) {
                    val primaryDirection = 90f
                    val adjusted: Float =
                        (-velocity.vy / magMaxComponent * 15 - velocity.vz / magMaxComponent * 5).toFloat()
                    primaryDirection + adjusted
                } else {
                    val primaryDirection = 270f
                    val adjusted: Float =
                        (velocity.vy / magMaxComponent * 15 + velocity.vz / magMaxComponent * 5).toFloat()
                    primaryDirection + adjusted
                }
            }
            maxComponent.first == 'y' -> {
                if (velocity.vy > 0) {
                    val primaryDirection = 0f
                    val adjusted: Float =
                        (velocity.vx / magMaxComponent * 15 + velocity.vz / magMaxComponent * 5).toFloat()
                    primaryDirection + adjusted
                } else {
                    val primaryDirection = 180f
                    val adjusted: Float =
                        (-velocity.vx / magMaxComponent * 15 - velocity.vz / magMaxComponent * 5).toFloat()
                    primaryDirection + adjusted
                }
            }
            maxComponent.first == 'z' -> {
                if (velocity.vz > 0) {
                    val primaryDirection = 45f
                    val adjusted: Float =
                        (velocity.vx / magMaxComponent * 15 - velocity.vy / magMaxComponent * 5).toFloat()
                    primaryDirection + adjusted
                } else {
                    val primaryDirection = 225f
                    val adjusted: Float =
                        (-velocity.vx / magMaxComponent * 15 + velocity.vy / magMaxComponent * 5).toFloat()
                    primaryDirection + adjusted
                }
            }
            else -> {
                logger.error("Wrong velocity maxComponent character")
                0.0f
            }
        }


        return 360f - degree
    }

    /**
     * Compute the primary image direction of player ship by the next cube the ship will enter
     * Then slightly rotate the direction by velocity
     */
    private fun rotationByNextPosition(
        playerData: PlayerData
    ): Float {
        val velocity: Velocity = playerData.velocity
        val velocityMag: Double = velocity.mag()
        val double3D: Double3D = playerData.double4D.toDouble3D()
        val maxComponent: Pair<Char, Double> = velocity.maxComponent()

        val xPositive: Boolean = velocity.vx >= 0.0
        val vxMag: Double = abs(velocity.vx)
        val xDistance: Double = if (xPositive) {
            ceil(double3D.x) - double3D.x
        } else {
            double3D.x - floor(double3D.x)
        }
        val xTime: Double = if (vxMag > 0.0) {
            xDistance / vxMag
        } else {
            Double.MAX_VALUE
        }

        val yPositive: Boolean = velocity.vy >= 0.0
        val vyMag: Double = abs(velocity.vy)
        val yDistance: Double = if (yPositive) {
            ceil(double3D.y) - double3D.y
        } else {
            double3D.y - floor(double3D.y)
        }
        val yTime: Double = if (vyMag > 0.0) {
            yDistance / vyMag
        } else {
            Double.MAX_VALUE
        }

        val zPositive: Boolean = velocity.vz >= 0.0
        val vzMag: Double = abs(velocity.vz)
        val zDistance: Double = if (zPositive) {
            ceil(double3D.z) - double3D.z
        } else {
            double3D.z - floor(double3D.z)
        }
        val zTime: Double = if (vzMag > 0.0) {
            zDistance / vzMag
        } else {
            Double.MAX_VALUE
        }

        val degree: Float = if (velocityMag < 1E-4) {
            315f
        } else {
            when {
                ((xTime <= yTime) && (xTime <= zTime)) -> {
                    if (xPositive) {
                        val primaryDirection = 90f
                        val adjustment: Float =
                            (-velocity.vy / velocityMag * 15 - velocity.vz / velocityMag * 5).toFloat()
                        val realAdjustment: Float = if (maxComponent.first == 'x') {
                            adjustment * 0.5f
                        } else {
                            adjustment * 0.5f + 10f * adjustment.sign
                        }
                        primaryDirection + realAdjustment
                    } else {
                        val primaryDirection = 270f
                        val adjustment: Float =
                            (velocity.vy / velocityMag * 15 + velocity.vz / velocityMag * 5).toFloat()
                        val realAdjustment: Float = if (maxComponent.first == 'x') {
                            adjustment * 0.5f
                        } else {
                            adjustment * 0.5f + 10f * adjustment.sign
                        }
                        primaryDirection + realAdjustment
                    }
                }
                ((yTime <= xTime) && (yTime <= zTime)) -> {
                    if (yPositive) {
                        val primaryDirection = 0f
                        val adjustment: Float =
                            (velocity.vx / velocityMag * 15 + velocity.vz / velocityMag * 5).toFloat()
                        val realAdjustment: Float = if (maxComponent.first == 'y') {
                            adjustment * 0.5f
                        } else {
                            adjustment * 0.5f + 10f * adjustment.sign
                        }
                        primaryDirection + realAdjustment
                    } else {
                        val primaryDirection = 180f
                        val adjustment: Float =
                            (-velocity.vx / velocityMag * 15 - velocity.vz / velocityMag * 5).toFloat()
                        val realAdjustment: Float = if (maxComponent.first == 'y') {
                            adjustment * 0.5f
                        } else {
                            adjustment * 0.5f + 10f * adjustment.sign
                        }
                        primaryDirection + realAdjustment
                    }
                }
                else -> {
                    if (zPositive) {
                        val primaryDirection = 45f
                        val adjustment: Float =
                            (velocity.vx / velocityMag * 15 - velocity.vy / velocityMag * 5).toFloat()
                        val realAdjustment: Float = if (maxComponent.first == 'z') {
                            adjustment * 0.5f
                        } else {
                            adjustment * 0.5f + 10f * adjustment.sign
                        }
                        primaryDirection + realAdjustment
                    } else {
                        val primaryDirection = 225f
                        val adjustment: Float =
                            (-velocity.vx / velocityMag * 15 + velocity.vy / velocityMag * 5).toFloat()
                        val realAdjustment: Float = if (maxComponent.first == 'z') {
                            adjustment * 0.5f
                        } else {
                            adjustment * 0.5f + 10f * adjustment.sign
                        }
                        primaryDirection + realAdjustment
                    }
                }
            }
        }

        return 360f - degree
    }
}
