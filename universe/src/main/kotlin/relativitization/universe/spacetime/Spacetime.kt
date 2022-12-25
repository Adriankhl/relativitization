package relativitization.universe.spacetime

import relativitization.universe.data.UniverseSettings
import relativitization.universe.maths.physics.Int3D
import relativitization.universe.maths.physics.Velocity
import relativitization.universe.utils.RelativitizationLogManager

sealed class Spacetime {
    abstract fun computeDelay(
        i1: Int3D,
        i2: Int3D,
        universeSettings: UniverseSettings
    ): Int

    abstract fun computeDilation(
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

    fun computeDelay(
        i1: Int3D,
        i2: Int3D,
        universeSettings: UniverseSettings
    ): Int {
        val spacetime: Spacetime = spacetimeMap.getOrElse(
            universeSettings.spacetimeCollectionName
        ) {
            logger.error("No space name matched, use minkowski spacetime")
            MinkowskiSpacetime
        }

        return spacetime.computeDelay(i1, i2, universeSettings)
    }

    fun computeDilation(
        int3D: Int3D,
        velocity: Velocity,
        universeSettings: UniverseSettings
    ): Double {
        val spacetime: Spacetime = spacetimeMap.getOrElse(
            universeSettings.spacetimeCollectionName
        ) {
            logger.error("No space name matched, use minkowski spacetime")
            MinkowskiSpacetime
        }

        return spacetime.computeDilation(int3D, velocity, universeSettings)
    }
}