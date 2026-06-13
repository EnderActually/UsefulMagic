package cn.coostack.usefulmagic.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

object ClientConfig {
    private val gson = Gson()
    private val configFile: File
        get() {
            val configDir = File(System.getProperty("user.home"), ".minecraft/config")
            configDir.mkdirs()
            return File(configDir, "usefulmagic.json")
        }

    fun loadManaBarVisible(): Boolean {
        return try {
            if (!configFile.exists()) {
                return true // Default to visible
            }
            val json = gson.fromJson(configFile.readText(), JsonObject::class.java)
            json.get("manaBarVisible")?.asBoolean ?: true
        } catch (e: Exception) {
            e.printStackTrace()
            true // Default to visible on error
        }
    }

    fun saveManaBarVisible(visible: Boolean) {
        try {
            val json = if (configFile.exists()) {
                gson.fromJson(configFile.readText(), JsonObject::class.java)
            } else {
                JsonObject()
            }
            json.addProperty("manaBarVisible", visible)
            configFile.writeText(gson.toJson(json))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
