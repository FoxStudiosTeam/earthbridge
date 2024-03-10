package ru.foxstudios.earthbridge

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelOption
import io.netty.channel.FixedRecvByteBufAllocator
import io.netty.channel.socket.DatagramPacket
import reactor.core.publisher.Flux
import reactor.netty.udp.UdpServer
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets


fun main(args: Array<String>) {
    var text = ""
    val server = UdpServer.create().port(28961).host("127.0.0.1").wiretap(true).option(ChannelOption.SO_BROADCAST, true).option(
        ChannelOption.RCVBUF_ALLOCATOR, FixedRecvByteBufAllocator(Int.MAX_VALUE))
        .handle { inbound, outbound ->
            val inFlux: Flux<DatagramPacket> = inbound.receiveObject()
                .handle { incoming, sink ->
                    if (incoming is DatagramPacket) {
                        println("receive!!!")
                        val packet = incoming
                        val content = packet.content().toString(StandardCharsets.UTF_8)
                        text += content
                        println(content.toByteArray().size)
                        println(content)

                        val byteBuf : ByteBuf?
                        if(content.contains('}')){
                            byteBuf = Unpooled.copiedBuffer("ok", StandardCharsets.UTF_8)
                            println(text)
                            text = ""
                        }else{
                            byteBuf = Unpooled.copiedBuffer("*", StandardCharsets.UTF_8)
                        }
                        val response = DatagramPacket(byteBuf, packet.sender())
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

