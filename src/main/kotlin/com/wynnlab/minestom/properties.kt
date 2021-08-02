package com.wynnlab.minestom

import java.io.File
import java.util.*

private val propertiesFile = File("./server.properties").apply {
    if (!exists()) createNewFile()
}

private val properties = Properties().apply {
    load(propertiesFile.inputStream())
}

@JvmName("getPropertyNullable")
fun getProperty(name: String, default: String? = null): String? = properties.getProperty(name, default)
fun getProperty(name: String, default: String): String = properties.getProperty(name, default)

fun getOrSetProperty(name: String, default: String) = properties.getProperty(name) ?: default.also { properties.setProperty(name, default) }

fun setProperty(name: String, value: String): String? = properties.setProperty(name, value) as? String?

fun saveProperties() = properties.store(propertiesFile.outputStream(), null)