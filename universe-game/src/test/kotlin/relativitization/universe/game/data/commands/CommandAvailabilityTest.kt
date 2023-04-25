package relativitization.universe.game.data.commands

import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.utils.I18NString
import kotlin.reflect.KClass
import kotlin.test.Test

internal class CommandAvailabilityTest {
    @Test
    fun kClassTest() {
        val k1 = DummyCommand::class
        assert(k1.isInstance(DummyCommand()))
        assert(!k1.isInstance(CannotSendCommand(reason = I18NString(""))))

        val l1: List<KClass<out Command>> = listOf(
            DummyCommand::class
        )

        assert(
            l1.any {
                it.isInstance(DummyCommand())
            }
        )

        assert(
            !l1.any {
                it.isInstance(CannotSendCommand(reason = I18NString("")))
            }
        )
    }
}