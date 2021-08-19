package com.wynnlab.minestom

import net.kyori.adventure.text.format.TextColor

const val COLOR_WYNN = 0x82e617

const val COLOR_DARKER_GRAY = 0x222222
const val COLOR_GRAY = 0x666666
const val COLOR_LIGHTER_GRAY = 0xdddddd

const val COLOR_RED = 0xb52714
const val COLOR_ORANGE = 0xf45a38
const val COLOR_PEACH = 0xf7942a
const val COLOR_GOLD = 0xf7d874
const val COLOR_GREEN = 0x14b54a
const val COLOR_AQUA = 0x25d8f7
const val COLOR_PALE_AQUA_BLUE = 0x95d7ed
const val COLOR_PALE_BLUE = 0x74b3e34
const val COLOR_LIGHT_BLUE = 0x2f8ed6
const val COLOR_PURPLE = 0xd94ef4
const val COLOR_PINK = 0xd34cca

val Int.textColor get() = TextColor.color(this)