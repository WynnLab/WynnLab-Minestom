package com.wynnlab.minestom.util

import java.io.File
import java.util.*

fun loadImageBase64(path: String): String? {
    val file = File(path)
    if (!file.exists()) return null
    val fileContent = file.readBytes()
    return Base64.getEncoder().encodeToString(fileContent)
}