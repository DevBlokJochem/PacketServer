package nl.jochem.packetserver

import nl.jochem.packetserver.packethelpers.Packet
import nl.jochem.packetserver.utils.getPacketType
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class ClientHandler(client: Socket) {
    private val client: Socket = client
    private val reader: Scanner = Scanner(client.getInputStream())
    private val writer: OutputStream = client.getOutputStream()
    private var running: Boolean = false

    fun run(packetServer: PacketServer) {
        running = true
        // Welcome message
        write("Connected to the server [server]")

        while (running) {
            try {
                val text = reader.nextLine()
                if (text == "exit"){
                    write("Exit [server]")
                    shutdown()
                    continue
                }else {
                    if(getPacketType(text) != null) {
                        packetServer.recieve(text, getPacketType(text) as Packet)
                    }
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
                shutdown()
            } finally {

            }

        }
    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    private fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }

}