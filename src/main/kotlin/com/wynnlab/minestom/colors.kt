package com.wynnlab.minestom

import net.kyori.adventure.text.format.TextColor

const val COLOR_WYNN = 0x82e617

const val COLOR_DARKER_GRAY = 0x222222

const val COLOR_ORANGE = 0xf45a38
const val COLOR_PEACH = 0xd39e69
const val COLOR_PURPLE = 0x984cd3
const val COLOR_PINK = 0xd34cca

val Int.textColor get() = TextColor.color(this)