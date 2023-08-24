package nl.jochem.packetserver.manager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.jochem.packetserver.PacketManager
import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.packets.ServerOpenPacket
import nl.jochem.packetserver.utils.getPacketType
import java.io.OutputStream
import java.net.Socket
import java.util.*

class PacketClient(private val port: Int, serverID: UUID) : PacketControl() {

    private val address = "localhost"
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
                try {
                    val text = reader.nextLine()

                    if(getPacketType(text) != null) {
                        launch(Dispatchers.Main) {
                            recieve(text, getPacketType(text) as Packet)
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    launch(Dispatchers.Main) {
                        PacketManager.shutdown()
                    }
                } finally {

                }
            }
        }
    }


    override fun disable() {
        connected = false
        reader.close()
        writer.close()
        connection.close()
        println("Connection $port closed")
        PacketManager.shutdown = true
    }
}