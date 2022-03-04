package com.wynnlab.minestom

import net.kyori.adventure.text.format.TextColor

val COLOR_WYNN = 0x82e617.textColor
val COLOR_DISCORD = 0x5865f2.textColor

val COLOR_DARKER_GRAY = 0x222222.textColor
val COLOR_GRAY = 0x666666.textColor
val COLOR_LIGHTER_GRAY = 0xdddddd.textColor

val COLOR_RED = 0xb52714.textColor
val COLOR_ORANGE = 0xf45a38.textColor
val COLOR_PEACH = 0xf7942a.textColor
val COLOR_GOLD = 0xf7d874.textColor
val COLOR_GREEN = 0x14b54a.textColor
val COLOR_AQUA = 0x25d8f7.textColor
val COLOR_PALE_AQUA_BLUE = 0x95d7ed.textColor
val COLOR_PALE_BLUE = 0x74b3e34.textColor
val COLOR_LIGHT_BLUE = 0x2f8ed6.textColor
val COLOR_PURPLE = 0xd94ef4.textColor
val COLOR_PINK = 0xd34cca.textColor

val Int.textColor get() = TextColor.color(this)