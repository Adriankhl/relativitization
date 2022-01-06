package relativitization.universe.mechanisms.defaults.dilated.pop

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.math.min
import relativitization.universe.maths.random.Rand

/**
 * Population migrate from higher salary place to lower salary place
 */
object Migration : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        // determine the rate of migration
        val migrationRate: Double = 0.01

        PopType.values().forEach { popType ->
            // carrier id to common pop data of a specific pop type
            val commonPopMap: Map<Int, MutableCommonPopData> = mutablePlayerData
                .playerInternalData.popSystemData().carrierDataMap.mapValues { (_, carrier) ->
                    carrier.allPopData.getCommonPopData(popType)
                }

            // Shuffle carrier id to avoid same immigration order every time
            val carrierIdList: MutableList<Int> = commonPopMap.keys.shuffled(
                Rand.rand()
            ).toMutableList()

            repeat(carrierIdList.size) {
                if (carrierIdList.size >= 2 ) {
                    val emigrateId: Int = carrierIdList.minByOrNull {
                        val commonPop: MutableCommonPopData = commonPopMap.getValue(it)
                        commonPop.salaryPerEmployee * (1.0 - commonPop.unemploymentRate)
                    } ?: -1

                    val emigrateCommonPop: MutableCommonPopData = commonPopMap.getValue(emigrateId)

                    carrierIdList.remove(emigrateId)

                    val immigrateId: Int = carrierIdList.elementAt(
                        Rand.rand().nextInt(carrierIdList.size)
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
        val immigrateAmount: Double = min(
            immigrateCarrierIdealPop * migrationRate,
            emigrateCommonPop.adultPopulation * migrationRate
        )

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