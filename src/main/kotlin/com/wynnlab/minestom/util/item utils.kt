package com.wynnlab.minestom.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.item.ItemMetaBuilder
import net.minestom.server.item.ItemStackBuilder

fun ItemStackBuilder.displayNameNonItalic(displayName: Component) = displayName(displayName.nonItalic)

fun ItemStackBuilder.loreNonItalic(vararg lore: Component) = lore(lore.map { it.nonItalic })
fun ItemStackBuilder.loreNonItalic(lore: List<Component>) = lore(lore.map { it.nonItalic })

private val Component.nonItalic get() = style { it.decoration(TextDecoration.ITALIC, false) }

fun ItemMetaBuilder.hideAllFlags() = hideFlag(0b111111)