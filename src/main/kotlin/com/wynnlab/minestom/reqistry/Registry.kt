package com.wynnlab.minestom.reqistry

abstract class Registry<T> {
    protected abstract val entries: MutableList<T>

    fun register(entry: T) = entries.add(entry)
    fun unregister(entry: T) = entries.remove(entry)

    fun entries(): List<T> = entries
}