package com.wynnlab.minestom.core

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

enum class Element(val color: TextColor, val icon: Char) {
    Neutral(NamedTextColor.DARK_RED, '❤'),
    Earth(NamedTextColor.DARK_GREEN, '✤'),
    Thunder(NamedTextColor.YELLOW, '✦'),
    Water(NamedTextColor.AQUA, '❉'),
    Fire(NamedTextColor.RED, '✹'),
    Air(NamedTextColor.WHITE, '❋')
}