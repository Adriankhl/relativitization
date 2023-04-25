package relativitization.universe.game.spacetime

import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.maths.physics.Int3D
import relativitization.universe.game.maths.physics.Velocity
import relativitization.universe.game.utils.RelativitizationLogManager

sealed class Spacetime {
    /**
     * Compute time delay between two points
     *
     * @param i1 point 1 in integer coordinate
     * @param i2 point 2 in integer coordinate
     * @param universeSettings the settings of the universe
     */
    abstract fun computeTimeDelay(
        i1: Int3D,
        i2: Int3D,
        universeSettings: UniverseSettings
    ): Int

    /**
     * Compute dilated time relative to unit time of a player
     *
     * @param int3D the location of the player
     * @param velocity the velocity of the player
     * @param universeSettings the settings of the universe
     */
    abstract fun computeDilatedTime(
        int3D: Int3D,
        velocity: Velocity,
        universeSettings: UniverseSettings
    ): Double

    open fun name(): String = this::class.simpleName.toString()
}

object SpacetimeCollection {
    private val logger = RelativitizationLogManager.getLogger()

    val spacetimeMap: Map<String, Spacetime> = Spacetime::class.sealedSubclasses.map {
        it.objectInstance!!
    }.associateBy {
        it.name()
    }

    fun computeTimeDelay(
        i1: Int3D,
        i2: Int3D,
        universeSettings: UniverseSettings
    ): Int {
        val spacetime: Spacetime = spacetimeMap.getOrElse(
            universeSettings.spacetimeCollectionName
        ) {
            logger.error("No spacetime name matched, use Minkowski spacetime")
            MinkowskiSpacetime
        }

        return spacetime.computeTimeDelay(i1, i2, universeSettings)
    }

    fun computeDilatedTime(
        int3D: Int3D,
        velocity: Velocity,
        universeSettings: UniverseSettings
    ): Double {
        val spacetime: Spacetime = spacetimeMap.getOrElse(
            universeSettings.spacetimeCollectionName
        ) {
            logger.error("No spacetime name matched, use Minkowski spacetime")
            MinkowskiSpacetime
        }

        return spacetime.computeDilatedTime(int3D, velocity, universeSettings)
    }
}