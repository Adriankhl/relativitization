package playground

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Ignore
import kotlin.test.Test

internal class KtorBugTest {
    @Ignore
    fun minimalTest() {
        val server = embeddedServer(
            CIO,
            environment = applicationEngineEnvironment {
                connector {
                    port = 12345
                    host = "127.0.0.1"
                }
            }
        )
        val client = HttpClient(io.ktor.client.engine.cio.CIO)

        runBlocking {
            launch {
                server.start(true)
            }

            println("Launched Server")
            val job = launch {
                val response: HttpResponse = client.get("http://127.0.0.1:12345")
                println(response)
            }
            println("Launched response")
            delay(1000)
            println("Cancel job")
            job.cancel()
            server.stop(1000, 1000)
        }
    }

    @Ignore
    fun minimalTest2() {
        println("Start")
        val server = embeddedServer(
            CIO,
            environment = applicationEngineEnvironment {
                connector {
                    port = 12345
                    host = "127.0.0.1"
                }
            }
        )
        val client = HttpClient(io.ktor.client.engine.cio.CIO)

        runBlocking {
            launch {
                server.start(true)
            }

            println("Launched Server")
            val job = launch {
                try {
                    val response: HttpResponse = client.get("http://127.0.0.1:12345")
                    println(response)
                } catch (cause: Throwable) {
                    println(cause)
                }
            }
            println("Launched response")
            delay(1000)
            println("Cancel job")
            job.cancel()
            server.stop(1000, 1000)
        }
    }

    @Ignore
    fun minimalTest3() {
        val server = embeddedServer(
            CIO,
            environment = applicationEngineEnvironment {
                connector {
                    port = 12345
                    host = "127.0.0.1"
                }
            }
        )

        runBlocking {
            launch {
                server.start(true)
            }

            println("Launched Server")
            // This one will still hang the process, while `throw RuntimeException("Error!")` crash the process
            //val response: HttpResponse = client.get("http://127.0.0.1:12345")
            println("Stop server")
            server.stop(1000, 1000)
        }
    }
}
