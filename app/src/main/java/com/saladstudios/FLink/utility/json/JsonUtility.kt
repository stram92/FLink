package com.saladstudios.FLink.utility.json

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat

private const val fileName = "financesdata.json"

fun readJsonFileLocal(context: Context): JSONArray? {
    val file = File(context.filesDir, fileName)

    if (file.exists()) {
        val jsonContent = file.readText(Charsets.UTF_8)
        val jsonArray = JSONArray(jsonContent)

        val jsonList = mutableListOf<JSONObject>()

        for (i in 0 until jsonArray.length()) {
            jsonList.add(jsonArray.getJSONObject(i))
        }

        val dateFormat = SimpleDateFormat("dd.MM.yyyy")

        jsonList.sortBy { dateFormat.parse(it.getString("entryDate")) }

        return JSONArray(jsonList)
    }

    return JSONArray()
}

fun addJsonEntryLocal(context: Context,
    payer: String, description: String, amount: String, entryDate: String, payedFor: String, payedForAmount: String
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

    val file = File(context.filesDir, fileName)
    file.writeText(jsonArray.toString(),Charsets.UTF_8)
}

fun removeJsonEntryLocal (context: Context, indexToRemove: Int) {
    val file = File(context.filesDir, fileName)

    if (file.exists()) {
        val jsonArray = readJsonFileLocal(context)

        if (indexToRemove >= 0 && indexToRemove < jsonArray!!.length()) {
            jsonArray.remove(indexToRemove)
        }

        file.writeText(jsonArray.toString(),Charsets.UTF_8)
    }
}

fun wipeJsonEntriesLocal (context: Context) {
    val file = File(context.filesDir, fileName)
    if (file.exists()) {
        file.delete()
    }
}