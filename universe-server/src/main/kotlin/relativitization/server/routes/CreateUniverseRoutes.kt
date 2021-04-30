package relativitization.server.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import relativitization.server.UniverseServerStatus
import relativitization.universe.Universe

fun Route.createUniverseRouting(universe: Universe, serverStatus: UniverseServerStatus) {
    route("/create") {
        get {

        }
    }
}

fun Application.registerCreateUniverseRoutes(universe: Universe, serverStatus: UniverseServerStatus) {
    routing {
        createUniverseRouting(universe, serverStatus)
    }
}