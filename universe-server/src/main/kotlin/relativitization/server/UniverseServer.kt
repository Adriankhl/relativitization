package relativitization.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.serialization.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import relativitization.server.routes.registerCreateUniverseRoutes
import relativitization.server.routes.registerRunUniverseRoutes
import relativitization.server.routes.registerUniverseStatusRoutes
import relativitization.universe.UniverseServerSettings
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager
import java.net.Socket


class UniverseServer(universeServerSettings: UniverseServerSettings) {
    private val universeServerInternal: UniverseServerInternal = UniverseServerInternal(universeServerSettings)

    private var universeServerInternalJob: Job = Job()

    private val ktorServer = embeddedServer(
        CIO,
        configure = {
            connectionIdleTimeoutSeconds = 30
        },
        environment = applicationEngineEnvironment {

            module {
                install(ContentNegotiation) {
                    json(DataSerializer.format)
                }
                install(StatusPages) {
                    exception<Throwable> {
                        logger.warn("Wrong request causing exception in server")
                        call.respondText("Something wrong in the request", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                    }
                }
                registerUniverseStatusRoutes(universeServerInternal)
                registerCreateUniverseRoutes(universeServerInternal)
                registerRunUniverseRoutes(universeServerInternal)
            }

            connector {
                port = findAvailablePort()
                host = "127.0.0.1"
            }

        }
    )

    suspend fun start() = coroutineScope {
        launch {
            ktorServer.start(true)
        }
        universeServerInternalJob = launch {
            universeServerInternal.start()
        }
    }

    suspend fun stop() {
        universeServerInternal.stop(universeServerInternalJob)
        ktorServer.stop(1000, 1000)
    }

    private fun findAvailablePort(testPort: Int = 29979): Int {
        val available: Boolean = try {
            Socket("127.0.0.1", testPort)
            false
        } catch (e: Throwable) {
            true
        }

        return if (available) {
            testPort
        } else {
            findAvailablePort(testPort + 1)
        }
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }

}