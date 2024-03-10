package ru.foxstudios.earthbridge

import com.rabbitmq.client.ConnectionFactory
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelOption
import io.netty.channel.FixedRecvByteBufAllocator
import io.netty.channel.socket.DatagramPacket
import io.netty.handler.logging.LogLevel
import reactor.core.publisher.Flux
import reactor.netty.transport.logging.AdvancedByteBufFormat
import reactor.netty.udp.UdpServer
import java.nio.charset.StandardCharsets


fun main(args: Array<String>) {
    var text = ""
    val factory = ConnectionFactory()
    factory.host = "localhost"
    factory.port = 30009
    val connection = factory.newConnection()
    val server = UdpServer.create().port(28961).host(System.getenv("EARTH_BRIDGE_IP"))
        .wiretap("logger-name", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL).option(ChannelOption.SO_BROADCAST, true)
        .option(ChannelOption.RCVBUF_ALLOCATOR, FixedRecvByteBufAllocator(Int.MAX_VALUE))
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
                            val channel = connection.createChannel()
                            try{
                                channel.queueDeclare("earth-queue", false, false, false, null);
                            }catch (e:Exception){
                                println(e.message)
                            }
                            channel.basicPublish("","earth-queue",null,text.toByteArray())

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

