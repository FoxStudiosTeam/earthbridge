import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import reactor.netty.Connection
import reactor.netty.udp.UdpServer
import java.time.Duration


fun main(args: Array<String>) {
    val server: Connection = UdpServer.create().port(25577).host("127.0.0.1").bindNow(Duration.ofSeconds(10))
    runBlocking {
        launch {
            println("starting2")
        }
        println("starting1")
    }
    server.onDispose().block()
}

