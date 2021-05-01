package relativitization.server.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import relativitization.server.UniverseServerInternal
import relativitization.universe.Universe
import relativitization.universe.communication.CreateUniverseMessage
import relativitization.universe.data.UniverseData
import relativitization.universe.generate.GenerateUniverse

fun Route.createUniverseRouting(universeServerInternal: UniverseServerInternal) {
    route("/create") {
        post() {
            val createUniverseMessage: CreateUniverseMessage = call.receive()
            if (createUniverseMessage.adminPassword == universeServerInternal.adminPassword) {
                val universeData: UniverseData = GenerateUniverse.generate(createUniverseMessage.generateSetting)
                if (universeData.isUniverseValid()) {
                    universeServerInternal.universe = Universe(universeData)
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

fun Application.registerCreateUniverseRoutes(universeServerInternal: UniverseServerInternal) {
    routing {
        createUniverseRouting(universeServerInternal)
    }
}