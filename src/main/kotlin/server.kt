import com.github.sarxos.webcam.Webcam
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.File
import java.util.*
import javax.imageio.ImageIO


fun main() {
    val webcams = Webcam.getWebcams()
    val d = Toolkit.getDefaultToolkit().getScreenSize()
    val rebot = Robot()

    embeddedServer(Netty, port = 2333, host = "0.0.0.0") {
        routing {
            get("/") {
                var info = ""
                for(i in webcams.indices) {
                    info += "${i} ${webcams[i].name}\n"
                }

                call.respondText(info)
            }

            get("/screenshot") {
                log.info("screenshot")
                val file = File("${UUID.randomUUID()}.png")
                val i = rebot.createScreenCapture(Rectangle(0, 0, d.width, d.height))
                ImageIO.write(i, "png", file)

                call.respondFile(file)
            }

            for(i in webcams.indices) {
                get("${i}") {
                    val webcam = webcams[i]
                    webcam.open()

                    val file = File("${UUID.randomUUID()}}.png")
                    ImageIO.write(webcam.image, "PNG", file);

                    webcam.close()

                    call.respondFile(file)
                }
            }
        }
    }.start(wait = true)
}