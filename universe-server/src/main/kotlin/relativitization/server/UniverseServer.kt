package relativitization.server

import relativitization.universe.data.serializer.DataSerializer
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.serialization.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import relativitization.server.routes.registerCreateUniverseRoutes
import relativitization.server.routes.registerUniverseStatusRoutes


class UniverseServer(adminPassword: String) {
    private val universeServerInternal: UniverseServerInternal = UniverseServerInternal(adminPassword)

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
            }

            connector {
                port = 29979
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


    companion object {
        private val logger = LogManager.getLogger()
    }

}