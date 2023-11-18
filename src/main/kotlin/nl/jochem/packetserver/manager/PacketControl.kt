package nl.jochem.packetserver.manager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.jochem.packetserver.config.RegisterSettingsConfig
import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.packethelpers.SubscriptionPacket
import nl.jochem.packetserver.packets.ServerOpenPacket
import nl.jochem.packetserver.utils.createName
import nl.jochem.packetserver.utils.getPacket
import java.io.OutputStream
import java.util.*
import java.util.function.Consumer

abstract class PacketControl {

    private val listeners: PriorityQueue<SubscriptionPacket<*>> = PriorityQueue()
    internal val logged = RegisterSettingsConfig().getInstance().logs
    internal val loggedPackets: ArrayList<Packet> = ArrayList()

    var online = false

    abstract fun send(packet: Packet, writer: OutputStream? = null)

    fun <T : Packet> subscribe(type: Class<T>, callback: Consumer<T>, priority: Int): SubscriptionPacket<T> {
        val subscription: SubscriptionPacket<T> = SubscriptionPacket(type, callback, priority)
        listeners.add(subscription)
        return subscription
    }

    fun recieve(input: String, packet: Packet) {
        GlobalScope.launch(Dispatchers.IO) {
            if(logged) println("Receive packet: ${packet.packetID} (${packet.sender})")
            listeners.filter { sub -> packet.packetID == sub.packetType.createName() }.forEach {sub ->
                val packet = getPacket(input, sub.packetType)
                if(packet != null) {
                    sub.handle(packet as Packet)
                }
            }
        }
    }

    abstract fun disable()
    fun online(writer: OutputStream? = null) {
        online = true
        loggedPackets.removeIf { packet -> packet.packetID == ServerOpenPacket.ID }
        send(ServerOpenPacket(RegisterSettingsConfig().getInstance().serverID))
        loggedPackets.forEach {
            send(it, writer)
        }
    }
}