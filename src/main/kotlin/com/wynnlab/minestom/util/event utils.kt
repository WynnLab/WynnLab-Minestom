package com.wynnlab.minestom.util

import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

inline fun <reified E : Event> EventNode<in E>.listen(noinline listener: (E) -> Unit) = addListener(E::class.java, listener)