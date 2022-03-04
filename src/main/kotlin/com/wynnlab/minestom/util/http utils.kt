package com.wynnlab.minestom.util

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Suppress("DeferredIsResult")
suspend fun get(url: String) = coroutineScope {
    async {
        httpClient.get<JsonObject>(url)
        /*val jUrl = URL(url)
    val conn = jUrl.openConnection() as HttpURLConnection
    conn.requestMethod = "GET"
    conn.connect()
    if (conn.responseCode != 200) throw HttpRequestException(url, conn.responseCode)
    val stream = jUrl.openStream()
    return JsonParser.parseReader(stream.reader()).asJsonObject*/
    }
}

/*private val client: HttpClient by lazy { HttpClient.newHttpClient() }
private val gson by lazy { Gson() }*/

suspend fun post(url: String, json: JsonObject) = coroutineScope {
    launch {
        httpClient.post<Unit>(url) {
            header("Content-Type", "application/json")
            body = json
        }
    }
}

//data class HttpRequestException(val address: String, val responseCode: Int) : Exception()

val json = Json {

}

val httpClient = HttpClient(Java) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(json)
    }
}