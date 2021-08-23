package com.wynnlab.minestom.util

import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagReadable
import net.minestom.server.tag.TagWritable
import kotlin.reflect.KProperty

fun <T, H> tag(handler: H, tag: Tag<T>): TagDelegate<T, H> where H : TagReadable, H : TagWritable =
    TagDelegate(handler, tag)

class TagDelegate<T, H>(private val handler: H, private val tag: Tag<T>) where H : TagReadable, H : TagWritable {
    operator fun getValue(thisRef: Any, property: KProperty<*>): T? = handler.getTag(tag)

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        handler.setTag(tag, value)
    }
}