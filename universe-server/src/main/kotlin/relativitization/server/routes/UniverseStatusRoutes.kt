package relativitization.server.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import relativitization.server.UniverseServerInternal

fun Route.universeStatueRouting(universeServerInternal: UniverseServerInternal) {
    route("/status/hello") {
        get {
            call.respondText("Hello, world!")
        }
    }
}

fun Application.registerUniverseStatusRoutes(universeServerInternal: UniverseServerInternal) {
    routing {
        universeStatueRouting(universeServerInternal)
    }
}
