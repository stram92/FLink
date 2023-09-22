package com.saladstudios.FLink.utility.json

import android.content.Context
import com.saladstudios.FLink.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

// Funktion zum Lesen einer JSON-Datei aus dem "res/raw" Ordner
fun readJsonFileLocal(context: Context): JSONArray? {
    val fileName = "financesdata.json"
    val file = File(context.filesDir, fileName)

    if (file.exists()) {
        val jsonContent = file.readText(Charsets.UTF_8)
        val jsonArray = JSONArray(jsonContent)
        return jsonArray
    }

    return JSONArray()
}

fun addJsonEntryLocal(
    context: Context,
    payer: String,
    description: String,
    amount: String,
    entryDate: String,
    payedFor: String,
    payedForAmount: String
) {
    val jsonArray = readJsonFileLocal(context)
    val newEntry = JSONObject()
    newEntry.put("payer", payer)
    newEntry.put("description", description)
    newEntry.put("amount", amount)
    newEntry.put("entryDate", entryDate)
    newEntry.put("payedFor", payedFor)
    newEntry.put("payedForAmount", payedForAmount)

    jsonArray?.put(newEntry)


    val fileName = "financesdata.json"
    val file = File(context.filesDir, fileName)
    file.writeText(jsonArray.toString(),Charsets.UTF_8)
}

fun wipeJsonEntriesLocal (context: Context) {
    val fileName = "financesdata.json"
    val file = File(context.filesDir, fileName)
    if (file.exists()) {
        file.delete()
    }
}