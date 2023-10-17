package com.saladstudios.FLink.utility.json

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat

private const val fileName = "financesdata.json"

fun readJsonFileLocal(context: Context): JSONArray? {
    val file = File(context.filesDir, fileName)

    if (file.exists()) {
        val jsonContent = file.readText(Charsets.UTF_8)

        Log.d("FLinkTest",jsonContent)


        val jsonArray = JSONArray(jsonContent)

        return sortJsonArray(jsonArray)
    }

    return JSONArray()
}

fun getJsonArray(jsonString: String): JSONArray? {
    val jsonArray = JSONArray(jsonString)

    return sortJsonArray(jsonArray)
}

fun mergeJsonArrays(first: JSONArray, second: JSONArray): JSONArray {
    var mergedArray = JSONArray()

    for (i in first.length()-1 downTo 0) {
        val item = first.get(i)

        mergedArray.put(item)
    }

    for (i in second.length()-1 downTo 0) {
        val item = second.get(i)

        if (!mergedArray.toString().contains(item.toString())) {
            mergedArray.put(item)
        }
    }

    return sortJsonArray(mergedArray)
}

fun removeJsonEntries (jsonArray: JSONArray, jsonArrayToRemove: JSONArray): JSONArray {
    val jsonArrayToRemoveList = ArrayList<Any>()

    for (i in jsonArrayToRemove.length()-1 downTo 0) {
        jsonArrayToRemoveList.add(jsonArrayToRemove[i])
    }

    for (i in jsonArray.length()-1 downTo 0) {
        val item = jsonArray[i]

        if (jsonArrayToRemoveList.contains(item)) {
            jsonArray.remove(i)
        }
    }

    return jsonArray
}


fun addJsonEntryLocal(context: Context,
    payer: String, description: String, sign: String, amount: String, entryDate: String, payedFor: String, payedForAmount: String, category: String
) {
    val jsonArray = readJsonFileLocal(context)
    val newEntry = JSONObject()
    newEntry.put("payer", payer)
    newEntry.put("description", description)
    newEntry.put("sign", sign)
    newEntry.put("amount", amount)
    newEntry.put("entryDate", entryDate)
    newEntry.put("payedFor", payedFor)
    newEntry.put("payedForAmount", payedForAmount)
    newEntry.put("category", category)

    jsonArray?.put(newEntry)

    val file = File(context.filesDir, fileName)
    file.writeText(jsonArray.toString(),Charsets.UTF_8)
}

fun removeJsonEntryLocal (context: Context, indexToRemove: String?) {
    val file = File(context.filesDir, fileName)

    if (file.exists()) {
        val jsonArray = readJsonFileLocal(context)

        /*if (indexToRemove >= 0 && indexToRemove < jsonArray!!.length()) {
            jsonArray.remove(indexToRemove)
        }*/

        file.writeText(jsonArray.toString(),Charsets.UTF_8)
    }
}

fun wipeJsonEntriesLocal (context: Context) {
    val file = File(context.filesDir, fileName)
    if (file.exists()) {
        file.delete()
    }
}

fun sortJsonArray(input: JSONArray): JSONArray {
    val jsonList = mutableListOf<JSONObject>()

    for (i in 0 until input.length()) {
        jsonList.add(input.getJSONObject(i))
    }

    val dateFormat = SimpleDateFormat("dd.MM.yyyy")

    jsonList.sortBy { dateFormat.parse(it.getString("entryDate")) }

    return JSONArray(jsonList)
}