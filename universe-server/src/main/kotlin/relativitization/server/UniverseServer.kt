package relativitization.server

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import relativitization.server.routes.registerCreateUniverseRoutes
import relativitization.server.routes.registerRunUniverseRoutes
import relativitization.server.routes.registerUniverseStatusRoutes
import relativitization.universe.game.UniverseServerSettings
import relativitization.universe.game.data.serializer.DataSerializer
import relativitization.universe.game.utils.RelativitizationLogManager


class UniverseServer(
    universeServerSettings: UniverseServerSettings,
    serverAddress: String = "127.0.0.1",
    serverPort: Int = 29979,
) {

    private val universeServerInternal: UniverseServerInternal = UniverseServerInternal(
        universeServerSettings,
        serverAddress,
        serverPort,
    )

    private var universeServerInternalJob: Job = Job()

    private val ktorServer = embeddedServer(
        CIO,
        host = serverAddress,
        port = serverPort,
        configure = {
            connectionIdleTimeoutSeconds = 30
        },
        module = {
            install(ContentNegotiation) {
                json(DataSerializer.format)
            }
            install(StatusPages) {
                exception<Throwable> { call, cause ->
                    logger.warn("Wrong request causing exception in server, cause: $cause")
                    call.respondText(
                        text = "Something wrong in the request, cause $cause",
                        contentType = ContentType.Text.Plain,
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
            registerUniverseStatusRoutes(universeServerInternal)
            registerCreateUniverseRoutes(universeServerInternal)
            registerRunUniverseRoutes(universeServerInternal)
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
        logger.debug("Stopping server")
        universeServerInternal.stop(universeServerInternalJob)
        ktorServer.stop(1000, 1000)
        logger.debug("Server stopped")
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }

}