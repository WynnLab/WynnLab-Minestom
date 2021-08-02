package com.wynnlab.minestom.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.HttpURLConnection
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun get(url: String): JsonObject {
    val jUrl = URL(url)
    val conn = jUrl.openConnection() as HttpURLConnection
    conn.requestMethod = "GET"
    conn.connect()
    if (conn.responseCode != 200) throw HttpRequestException(url, conn.responseCode)
    val stream = jUrl.openStream()
    return JsonParser.parseReader(stream.reader()).asJsonObject
}

private val client: HttpClient by lazy { HttpClient.newHttpClient() }
private val gson by lazy { Gson() }

//"https://discord.com/api/webhooks/871724748818763836/a_T9R18nU51xMmWjUIYAZgY1kOvuWaaJHEzUu45mDEOwoEKnZmAr_k6hUSIP4rORCK6T"
fun post(url: String, json: JsonObject) {
    val request = HttpRequest.newBuilder(URL(url).toURI())
        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(json)))
        .header("Content-Type", "application/json")
        .build()
    client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
}

data class HttpRequestException(val address: String, val responseCode: Int) : Exception()