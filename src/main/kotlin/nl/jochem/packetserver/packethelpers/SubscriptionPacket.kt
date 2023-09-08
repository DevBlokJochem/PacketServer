package nl.jochem.packetserver.packethelpers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.function.Consumer

class SubscriptionPacket<T : Packet>(packetType: Class<T>, callback: Consumer<T>) {

    val packetType: Class<T>
    private val callback: Consumer<T>

    init {
        this.packetType = packetType
        this.callback = callback
    }

    fun handle(packet: Packet) {
        callback.accept(packetType.cast(packet))
    }

}