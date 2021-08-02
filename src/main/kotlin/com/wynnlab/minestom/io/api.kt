package com.wynnlab.minestom.io

/*import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.HttpURLConnection
import java.net.URL

fun getApiResultsJson(address: String): JsonObject {
    val url = URL(address)
    val conn = url.openConnection() as HttpURLConnection
    conn.requestMethod = "GET"
    conn.connect()
    if (conn.responseCode != 200) throw HttpRequestException(address, conn.responseCode)
    val stream = url.openStream()
    return JsonParser.parseReader(stream.reader()).asJsonObject
}*/