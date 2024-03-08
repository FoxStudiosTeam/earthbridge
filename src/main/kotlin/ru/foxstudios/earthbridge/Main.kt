package ru.foxstudios.earthbridge

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelOption
import io.netty.channel.socket.DatagramPacket
import kotlinx.coroutines.runBlocking
import reactor.core.publisher.Flux
import reactor.netty.udp.UdpServer
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets


fun main(args: Array<String>) {
    runBlocking {
        println("da")
    }
    var data = arrayOf<String>()
    val server = UdpServer.create().port(25577).host("127.0.0.1").wiretap(true).option(ChannelOption.SO_RCVBUF, Int.MAX_VALUE).option(ChannelOption.SO_BROADCAST, true)
        .handle { inbound, outbound ->
            val inFlux: Flux<DatagramPacket> = inbound.receiveObject()
                .handle { incoming, sink ->
                    if (incoming is DatagramPacket) {
                        val content = incoming.content()
                        println(content.toString(StandardCharsets.UTF_8))
                        val response = DatagramPacket(Unpooled.copiedBuffer("ok", StandardCharsets.UTF_8), incoming.sender())
                        sink.next(response)
                    }
                }
            return@handle outbound.sendObject(inFlux)
        }
    server.bindNow().onDispose().block()
}

fun giveInfo(buffContent: String) {
    val writer = BufferedWriter(OutputStreamWriter(System.out))
    writer.write(buffContent.toByteArray().size)
    writer.flush()
    writer.write(buffContent)
    writer.flush()
}

