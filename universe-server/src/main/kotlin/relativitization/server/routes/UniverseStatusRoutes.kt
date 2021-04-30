package relativitization.server.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import relativitization.server.UniverseServerStatus
import relativitization.universe.Universe

fun Route.universeStatueRouting(universe: Universe, serverStatus: UniverseServerStatus) {
    route("/status/hello") {
        get {
            call.respondText("Hello, world!")
        }
    }
}

fun Application.registerUniverseStatusRoutes(universe: Universe, serverStatus: UniverseServerStatus) {
    routing {
        universeStatueRouting(universe, serverStatus)
    }
}
