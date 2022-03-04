@file:Suppress("PackageDirectoryMismatch")

package net.minestom.server.tag

@Suppress("FunctionName")
fun BooleanTag(key: String) = Tag(key, { it.getByte(key) != 0.toByte() }, { nbt, v -> nbt.setByte(key, if (v) 1 else 0) }, { false })