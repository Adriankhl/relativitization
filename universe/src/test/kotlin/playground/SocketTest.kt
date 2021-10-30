package playground

import java.net.Socket
import kotlin.test.Test

internal class SocketTest {
    @Test
    fun socket() {
        Socket("127.0.0.1", 29979)
    }
}