package com.wynnlab.minestom.core

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

enum class Element(val color: TextColor, val icon: Char) {
    Health(NamedTextColor.DARK_RED, '❤'),
    Neutral(NamedTextColor.GOLD, '❤'), //TODO
    Earth(NamedTextColor.DARK_GREEN, '✤'),
    Thunder(NamedTextColor.YELLOW, '✦'),
    Water(NamedTextColor.AQUA, '❉'),
    Fire(NamedTextColor.RED, '✹'),
    Air(NamedTextColor.WHITE, '❋')
}