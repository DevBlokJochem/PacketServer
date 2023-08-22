package nl.jochem.packetserver.manager

import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.packets.ServerOpenPacket
import nl.jochem.packetserver.utils.createName
import nl.jochem.packetserver.utils.getPacketType
import nl.jochem.packetserver.utils.getServerOpenPacket
import nl.jochem.packetserver.utils.getSpecificPacket
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class ServerClient(client: Socket, packetServer: PacketServer) {
    private val client: Socket = client
    private val reader: Scanner = Scanner(client.getInputStream())
    internal val writer: OutputStream = client.getOutputStream()
    private var running: Boolean = false
    private val packetServer: PacketServer

    init {
        this.packetServer = packetServer
    }

    fun run() {
        running = true
        // Welcome message
        write("Connected to the server [server]")

        while (running) {
            try {
                val text = reader.nextLine()
                if(getPacketType(text) != null) {
                    if(getPacketType(text)!!.packetID == ServerOpenPacket::class.java.createName()) {
                        packetServer.createClient((getSpecificPacket(text, ServerOpenPacket::class.java) as ServerOpenPacket).serverID, this)
                    }else{
                        packetServer.recieve(text, getPacketType(text) as Packet)
                    }

                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                disable()
            } finally {

            }

        }
    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    fun disable() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }

}