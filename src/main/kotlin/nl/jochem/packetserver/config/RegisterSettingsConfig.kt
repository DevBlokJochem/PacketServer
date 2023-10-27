package nl.jochem.packetserver.config

import com.google.gson.GsonBuilder
import java.io.File
import java.util.*

private const val filename: String = "packetserver/settings.json"

class RegisterSettingsConfig {

    init {
        if(!File("packetserver").exists()) { File("packetserver").mkdirs() }
        if (!File(filename).exists()) {
            File(filename).createNewFile()
            File(filename).writeText(GsonBuilder().setPrettyPrinting().create()!!.toJson(PacketServerSettings(
                serverIP = "localhost",
                serverPort = 25800,
                serverID = UUID.randomUUID(),
                logs = true
            )))
        }
    }

    fun getInstance() = GsonBuilder().setPrettyPrinting().create()!!.fromJson(File(filename).readText(), PacketServerSettings::class.java)!!

}

data class PacketServerSettings(
    val serverIP: String,
    val serverPort: Int,
    val serverID: UUID,
    val logs: Boolean
)