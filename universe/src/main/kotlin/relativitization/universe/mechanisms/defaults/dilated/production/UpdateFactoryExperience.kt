package relativitization.universe.mechanisms.defaults.dilated.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.popSystemData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.random.Random

object UpdateFactoryExperience : Mechanism() {
    // Parameters
    // Maximum experience of a factory
    private const val maxExperience: Double = 10.0

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values
            .forEach { carrier ->
                carrier.allPopData.labourerPopData.fuelFactoryMap.values
                    .forEach { fuelFactory ->
                        fuelFactory.experience = computeNewExperience(
                            fuelFactory.experience,
                            fuelFactory.employeeFraction()
                        )
                    }

                carrier.allPopData.labourerPopData.resourceFactoryMap.values
                    .forEach { resourceFactory ->
                        resourceFactory.experience = computeNewExperience(
                            resourceFactory.experience,
                            resourceFactory.employeeFraction()
                        )
                    }
            }
        return listOf()
    }

    /**
     * Compute the new experience of the factory
     *
     * @param experience current experience
     * @param employeeFraction between 0 and 1, how full is the factory
     */
    private fun computeNewExperience(
        experience: Double,
        employeeFraction: Double,
    ): Double {
        // The ideal experience level given the employee ratio
        val targetExperience: Double = employeeFraction * maxExperience

        return experience + (targetExperience - experience) * 0.1
    }
}