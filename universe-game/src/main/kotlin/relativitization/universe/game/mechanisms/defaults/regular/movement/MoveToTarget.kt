package relativitization.universe.game.mechanisms.defaults.regular.movement

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.game.data.commands.ChangeVelocityCommand
import relativitization.universe.core.data.commands.Command
import relativitization.universe.game.data.components.defaults.physics.MutableTargetDouble3DData
import relativitization.universe.game.data.components.modifierData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.maths.physics.Movement
import relativitization.universe.core.maths.physics.TargetVelocityData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.game.data.components.defaults.physics.maxMovementDeltaRestMass
import relativitization.universe.game.data.components.totalRestMass
import kotlin.math.min
import kotlin.random.Random

object MoveToTarget : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        val targetDouble3DData: MutableTargetDouble3DData = mutablePlayerData.playerInternalData
            .physicsData().targetDouble3DData

        if (targetDouble3DData.maxSpeed > universeData3DAtPlayer.universeSettings.speedOfLight) {
            logger.error("maxSpeed greater than the speed of light")
        }

        val sameDouble3D: Boolean =
            mutablePlayerData.double4D.toMutableDouble3D() == targetDouble3DData.target
        val zeroVelocity: Boolean =
            mutablePlayerData.velocity.mag() <= 0.0

        // Change hasTarget to false after reaching the target
        if (sameDouble3D && zeroVelocity) {
            targetDouble3DData.hasTarget = false
        }

        if (targetDouble3DData.hasTarget) {
            mutablePlayerData.playerInternalData.modifierData().physicsModifierData
                .disableFuelIncreaseByTime(1)

            val targetVelocityData: TargetVelocityData = Movement.targetDouble3DByPhotonRocket(
                initialRestMass = mutablePlayerData.playerInternalData.physicsData()
                    .totalRestMass(),
                maxDeltaRestMass = mutablePlayerData.playerInternalData.physicsData().fuelRestMassData
                    .maxMovementDeltaRestMass(),
                initialVelocity = mutablePlayerData.velocity.toVelocity(),
                maxSpeed = min(
                    targetDouble3DData.maxSpeed,
                    universeData3DAtPlayer.universeSettings.speedOfLight
                ),
                initialDouble3D = mutablePlayerData.double4D.toDouble3D(),
                targetDouble3D = targetDouble3DData.target.toDouble3D(),
                speedOfLight = universeData3DAtPlayer.universeSettings.speedOfLight
            )

            val changeVelocityCommand = ChangeVelocityCommand(
                toId = mutablePlayerData.playerId,
                targetVelocity = targetVelocityData.newVelocity
            )

            changeVelocityCommand.checkAndExecute(
                playerData = mutablePlayerData,
                fromId = mutablePlayerData.playerId,
                fromInt4D = mutablePlayerData.int4D.toInt4D(),
                universeSettings = universeData3DAtPlayer.universeSettings
            )
        }

        return listOf()
    }
}