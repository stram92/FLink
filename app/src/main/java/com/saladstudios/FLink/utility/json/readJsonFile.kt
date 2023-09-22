package com.saladstudios.FLink.utility.json

import android.content.Context
import org.json.JSONArray

// Funktion zum Lesen einer JSON-Datei aus dem "res/raw" Ordner
fun readJsonFile(context: Context, resourceId: Int): JSONArray? {
    val json: String?
    try {
        val inputStream = context.resources.openRawResource(resourceId)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        json = String(buffer, Charsets.UTF_8)
        return JSONArray(json)
    } catch (ex: Exception) {
        ex.printStackTrace()
        return null
    }
}