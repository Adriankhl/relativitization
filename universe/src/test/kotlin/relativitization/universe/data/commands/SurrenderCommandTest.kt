package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.name
import relativitization.universe.generate.testing.TestingFixedMinimal
import kotlin.test.Test


internal class SurrenderCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            )
        )

        val view1At3 = universe.getUniverse3DViewAtPlayer(3)
        val view1At5 = universe.getUniverse3DViewAtPlayer(5)

        assert(
            view1At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )
        assert(
            view1At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    5 to listOf(
                        DeclareWarCommand(
                            toId = 3,
                            fromId = 5,
                            fromInt4D = view1At5.getCurrentPlayerData().int4D,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view2At3 = universe.getUniverse3DViewAtPlayer(3)
        val view2At5 = universe.getUniverse3DViewAtPlayer(5)


        assert(
            view2At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )
        assert(
            view2At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.keys == setOf(3)
        )

        runBlocking {
            for (i in (1..2)) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }


        val view3At3 = universe.getUniverse3DViewAtPlayer(3)
        val view3At5 = universe.getUniverse3DViewAtPlayer(5)


        assert(
            view3At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.keys == setOf(5)
        )
        assert(
            view3At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.keys == setOf(3)
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    3 to listOf(
                        SurrenderCommand(
                            toId = 3,
                            fromId = 3,
                            fromInt4D = view3At3.getCurrentPlayerData().int4D,
                            targetPlayerId = 5
                        )
                    ),
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        runBlocking {
            for (i in (1..3)) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }


        val view4At3 = universe.getUniverse3DViewAtPlayer(3)
        val view4At5 = universe.getUniverse3DViewAtPlayer(5)

        assert(
            view4At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )
        assert(
            view4At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )

        assert(view4At3.getCurrentPlayerData().playerInternalData.directLeaderId == 5)
        assert(view4At5.getCurrentPlayerData().isSubOrdinate(3))
    }

    @Test
    fun fixedMinimalDoubleSurrenderTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            )
        )

        val view1At3 = universe.getUniverse3DViewAtPlayer(3)
        val view1At5 = universe.getUniverse3DViewAtPlayer(5)

        assert(
            view1At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )
        assert(
            view1At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    5 to listOf(
                        DeclareWarCommand(
                            toId = 3,
                            fromId = 5,
                            fromInt4D = view1At5.getCurrentPlayerData().int4D,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view2At3 = universe.getUniverse3DViewAtPlayer(3)
        val view2At5 = universe.getUniverse3DViewAtPlayer(5)


        assert(
            view2At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )
        assert(
            view2At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.keys == setOf(3)
        )

        runBlocking {
            for (i in (1..2)) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }


        val view3At3 = universe.getUniverse3DViewAtPlayer(3)
        val view3At5 = universe.getUniverse3DViewAtPlayer(5)


        assert(
            view3At3.getCurrentPlayerData().playerInternalData.diplomacyData()
                .relationData.selfWarDataMap.keys == setOf(5)
        )
        assert(
            view3At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.keys == setOf(3)
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    3 to listOf(
                        SurrenderCommand(
                            toId = 3,
                            fromId = 3,
                            fromInt4D = view3At3.getCurrentPlayerData().int4D,
                            targetPlayerId = 5
                        )
                    ),
                    5 to listOf(
                        SurrenderCommand(
                            toId = 5,
                            fromId = 5,
                            fromInt4D = view3At5.getCurrentPlayerData().int4D,
                            targetPlayerId = 3
                        )
                    ),
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        runBlocking {
            for (i in (1..3)) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }


        val view4At3 = universe.getUniverse3DViewAtPlayer(3)
        val view4At5 = universe.getUniverse3DViewAtPlayer(5)

        assert(
            view4At3.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )
        assert(
            view4At5.getCurrentPlayerData().playerInternalData.diplomacyData().relationData
                .selfWarDataMap.isEmpty()
        )

        assert(view4At3.getCurrentPlayerData().isTopLeader())
        assert(view4At5.getCurrentPlayerData().isTopLeader())
    }
}