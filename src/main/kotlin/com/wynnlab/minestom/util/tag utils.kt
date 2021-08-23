package net.minestom.server.tag

fun BooleanTag(key: String) = Tag(key, { it.getByte(key) != 0.toByte() }, { nbt, v -> nbt.setByte(key, if (v) 1 else 0) }, { false })