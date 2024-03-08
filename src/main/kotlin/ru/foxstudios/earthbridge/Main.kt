package ru.foxstudios.earthbridge

import com.rabbitmq.client.impl.nio.ByteBufferOutputStream
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelOption
import io.netty.channel.socket.DatagramPacket
import kotlinx.coroutines.runBlocking
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.netty.udp.UdpServer
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets


fun main(args: Array<String>) {
    runBlocking {
        println("da")
    }
    val server = UdpServer.create().port(25577).host("127.0.0.1").wiretap(true).option(ChannelOption.SO_BROADCAST, true)
        .handle { inbound, outbound ->
            val inFlux: Flux<DatagramPacket> = inbound.receiveObject()
                .handle { incoming, sink ->
                    if (incoming is DatagramPacket) {
                        val packet = incoming
                        val content = packet.content()
                        val buffContent = content.toString(StandardCharsets.UTF_8)
                        val writer = BufferedWriter(OutputStreamWriter(System.out))
                        writer.write(buffContent.toByteArray().size)
                        writer.flush()
                        writer.write(buffContent)
                        writer.flush()
                        val byteBuf = Unpooled.copiedBuffer("ok", StandardCharsets.UTF_8)
                        val response = DatagramPacket(byteBuf, packet.sender())
                        sink.next(response)
                    }
                }
            return@handle outbound.sendObject(inFlux)
        }
    server.bindNow().onDispose().block()
}

