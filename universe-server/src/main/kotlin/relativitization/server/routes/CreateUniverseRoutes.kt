package relativitization.server.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import relativitization.server.UniverseServer
import relativitization.server.UniverseServerState
import relativitization.universe.Universe
import relativitization.universe.communication.CreateUniverseMessage
import relativitization.universe.data.UniverseData
import relativitization.universe.generate.GenerateSetting
import relativitization.universe.generate.GenerateUniverse

fun Route.createUniverseRouting(serverState: UniverseServerState) {
    route("/create") {
        post() {
            val createUniverseMessage: CreateUniverseMessage = call.receive()
            if (createUniverseMessage.adminPassword == serverState.adminPassword) {
                val universeData: UniverseData = GenerateUniverse.generate(createUniverseMessage.generateSetting)
                if (universeData.isUniverseValid()) {
                    serverState.universe = Universe(universeData)
                    call.respondText("Created Universe", ContentType.Text.Plain, HttpStatusCode.OK)
                } else {
                    call.respondText(
                        "Created Universe Failed, wrong setting",
                        ContentType.Text.Plain,
                        HttpStatusCode.NotAcceptable
                    )
                } 
            } else {
                call.respondText(
                    "Created Universe Failed, wrong admin password",
                    io.ktor.http.ContentType.Text.Plain,
                    io.ktor.http.HttpStatusCode.Unauthorized
                )
            }
        }
    }
}

fun Application.registerCreateUniverseRoutes(serverState: UniverseServerState) {
    routing {
        createUniverseRouting(serverState)
    }
}