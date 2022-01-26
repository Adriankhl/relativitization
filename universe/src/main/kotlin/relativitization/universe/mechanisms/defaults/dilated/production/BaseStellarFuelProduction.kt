package relativitization.universe.mechanisms.defaults.dilated.production

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.popsystem.CarrierType
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.global.UniverseGlobalData
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
            val fuelList: List<Double> =
                mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.map {
                    baseFuelProduction(
                        it.carrierType,
                        it.carrierInternalData.coreRestMass
                    )
                }

            fuelList.forEach {
                mutablePlayerData.playerInternalData.physicsData().addFuel(it)
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