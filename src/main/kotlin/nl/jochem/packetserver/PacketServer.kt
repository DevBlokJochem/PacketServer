package nl.jochem.packetserver

import nl.jochem.packetserver.packethelpers.SubscriptionPacket
import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.utils.createName
import nl.jochem.packetserver.utils.getPacket
import java.net.ServerSocket
import java.util.function.Consumer
import kotlin.concurrent.thread

class PacketServer(private val port: Int) {

    private val server: ServerSocket = ServerSocket(port)
    private val listeners: ArrayList<SubscriptionPacket<*>> = ArrayList()

    init {
        println("Packet server is running on port ${server.localPort}")

        thread {
            while (true) {
                val client = server.accept()
                if(client.inetAddress.hostAddress != "127.0.0.1") client.close()
                println("Client connected: ${client.inetAddress.hostAddress}")

                // Run client in it's own thread.
                thread { ClientHandler(client).run(this) }
            }
        }
    }

    fun <T : Packet> subscribe(type: Class<T>, callback: Consumer<T>): SubscriptionPacket<T> {
        val subscription: SubscriptionPacket<T> = SubscriptionPacket(type, callback)
        listeners.add(subscription)
        return subscription
    }

    fun recieve(input: String, packet: Packet) {
        listeners.filter { sub -> packet.packetID == sub.packetType.createName() }.forEach {sub ->
            val packet = getPacket(input, sub.packetType)
            if(packet != null) {
                sub.handle(packet as Packet)
            }
        }
    }
}