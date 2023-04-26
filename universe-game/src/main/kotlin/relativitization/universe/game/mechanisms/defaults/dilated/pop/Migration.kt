package relativitization.universe.game.mechanisms.defaults.dilated.pop

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.game.data.components.defaults.popsystem.pop.getCommonPopData
import relativitization.universe.game.data.components.popSystemData
import kotlin.math.min
import kotlin.random.Random

/**
 * Population migrate from higher salary place to lower salary place
 */
object Migration : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        // determine the rate of migration
        val migrationRate = 0.01

        PopType.values().forEach { popType ->
            // carrier id to common pop data of a specific pop type
            val commonPopMap: Map<Int, MutableCommonPopData> = mutablePlayerData
                .playerInternalData.popSystemData().carrierDataMap.mapValues { (_, carrier) ->
                    carrier.allPopData.getCommonPopData(popType)
                }

            // Shuffle carrier id to avoid same immigration order every time
            val carrierIdList: MutableList<Int> = commonPopMap.keys.shuffled(
                random
            ).toMutableList()

            repeat(carrierIdList.size) {
                if (carrierIdList.size >= 2) {
                    val emigrateId: Int = carrierIdList.minByOrNull {
                        val commonPop: MutableCommonPopData = commonPopMap.getValue(it)
                        commonPop.salaryFactor * commonPop.employmentRate
                    } ?: -1

                    val emigrateCommonPop: MutableCommonPopData = commonPopMap.getValue(emigrateId)

                    carrierIdList.remove(emigrateId)

                    val immigrateId: Int = carrierIdList.elementAt(
                        random.nextInt(carrierIdList.size)
                    )

                    val immigrateCommonPop: MutableCommonPopData = commonPopMap.getValue(
                        immigrateId
                    )

                    localMigrate(
                        emigrateCommonPop,
                        immigrateCommonPop,
                        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.getValue(
                            immigrateId
                        ).carrierInternalData.idealPopulation,
                        migrationRate,
                    )
                }
            }
        }

        return listOf()
    }

    /**
     * Emigrate from one to another
     */
    fun localMigrate(
        emigrateCommonPop: MutableCommonPopData,
        immigrateCommonPop: MutableCommonPopData,
        immigrateCarrierIdealPop: Double,
        migrationRate: Double,
    ) {
        val immigrateAmount: Double =
            if (immigrateCommonPop.adultPopulation < immigrateCarrierIdealPop) {
                min(
                    (immigrateCarrierIdealPop - immigrateCommonPop.adultPopulation) * migrationRate,
                    emigrateCommonPop.adultPopulation * migrationRate
                )
            } else {
                0.0
            }

        val otherSaving: Double = if (emigrateCommonPop.adultPopulation > 0.0) {
            emigrateCommonPop.saving * immigrateAmount / emigrateCommonPop.adultPopulation
        } else {
            0.0
        }

        immigrateCommonPop.addAdultPopulation(
            otherPopulation = immigrateAmount,
            otherEducationLevel = emigrateCommonPop.educationLevel,
            otherSatisfaction = emigrateCommonPop.satisfaction,
            otherSaving = otherSaving,
        )
    }
}