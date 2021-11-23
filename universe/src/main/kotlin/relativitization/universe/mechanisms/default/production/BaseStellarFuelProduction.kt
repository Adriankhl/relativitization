package relativitization.universe.mechanisms.default.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.default.popsystem.CarrierType
import relativitization.universe.data.components.default.popsystem.MutableCarrierData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.mechanisms.Mechanism

/**
 * Produce basic fuel from stellar system regardless of pop
 */
object BaseStellarFuelProduction : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        // Produce if only fuel increase is not disable
        if (mutablePlayerData.playerInternalData.modifierData().physicsModifierData.disableRestMassIncreaseTimeLimit <= 0) {
            val gamma: Double = Relativistic.gamma(
                universeData3DAtPlayer.getCurrentPlayerData().velocity,
                universeSettings.speedOfLight
            )

            val fuelList: List<Double> =
                mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map {
                    baseFuelProduction(
                        mutableCarrierData = it,
                        gamma
                    )
                }

            fuelList.forEach {
                mutablePlayerData.playerInternalData.physicsData().addFuel(it)
            }
        }

        return listOf()
    }

    fun baseFuelProduction(
        mutableCarrierData: MutableCarrierData,
        gamma: Double,
    ): Double {
        return if (mutableCarrierData.carrierType == CarrierType.STELLAR) {
            // Estimate the relation between star mass and energy received by planet
            mutableCarrierData.coreRestMass / 1E24 / gamma
        } else {
            0.0
        }
    }
}