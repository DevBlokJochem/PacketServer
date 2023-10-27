package nl.jochem.packetserver.manager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.jochem.packetserver.PacketManager
import nl.jochem.packetserver.packets.ServerClosePacket
import nl.jochem.packetserver.packets.ServerOpenPacket
import nl.jochem.packetserver.utils.createName
import nl.jochem.packetserver.utils.getPacketType
import java.io.OutputStream
import java.net.Socket
import java.util.*

class PacketClient(address: String, private val port: Int, serverID: UUID) : PacketControl() {
    private var connected: Boolean = true
    private var connection: Socket = Socket(address, port)
    private var reader: Scanner = Scanner(connection.getInputStream())
    internal var writer: OutputStream = connection.getOutputStream()

    init {
        read()
        send(ServerOpenPacket(serverID), writer)
        println("Connected to master server at $address on port $port [client]")
    }

    private fun read() {
        GlobalScope.launch(Dispatchers.IO) {
            while (connected) {
                if(reader.hasNextLine()) {
                    val text = reader.nextLine()

                    val packet = getPacketType(text)
                    if(packet != null) {
                        if(packet.packetID == ServerClosePacket::class.java.createName()) {
                            disable()
                        }
                        recieve(text, packet)
                    }else{
                        println("PacketClient.getPacketType(text) == null")
                        println("========================================")
                        println(text)
                        println("========================================")
                    }
                }
            }
        }
    }


    override fun disable() {
        connected = false
        reader.close()
        connection.close()
        println("Connection $port closed")
        PacketManager.shutdown = true
    }
}