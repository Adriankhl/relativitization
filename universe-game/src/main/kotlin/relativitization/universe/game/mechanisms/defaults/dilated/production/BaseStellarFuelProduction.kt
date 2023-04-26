package relativitization.universe.game.mechanisms.defaults.dilated.production

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.components.defaults.popsystem.CarrierType
import relativitization.universe.game.data.components.modifierData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.popSystemData
import kotlin.random.Random

/**
 * Produce basic fuel from stellar system regardless of pop
 */
object BaseStellarFuelProduction : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        // Produce if only fuel increase is not disable
        if (mutablePlayerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0) {
            val fuelList: List<Double> =
                mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map {
                    baseFuelProduction(
                        it.carrierType,
                        it.carrierInternalData.coreRestMass
                    )
                }

            fuelList.forEach {
                mutablePlayerData.playerInternalData.physicsData().addExternalFuel(it)
            }
        }

        return listOf()
    }

    fun baseFuelProduction(
        carrierType: CarrierType,
        coreRestMass: Double,
    ): Double {
        return if (carrierType == CarrierType.STELLAR) {
            // Estimate the relation between star mass and energy received by planet
            coreRestMass / 1E24
        } else {
            0.0
        }
    }
}