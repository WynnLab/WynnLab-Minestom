package com.wynnlab.minestom.players

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

@Suppress("EnumEntryName")
enum class Rank(val tag: Component?, val nameColor: TextColor) {
    Player(null, NamedTextColor.WHITE),
    Vip("VIP", NamedTextColor.DARK_GREEN, NamedTextColor.GREEN),

    `Vip+`("VIP+", NamedTextColor.AQUA, NamedTextColor.DARK_AQUA),
    Hero("HERO", NamedTextColor.DARK_PURPLE, NamedTextColor.LIGHT_PURPLE),
    Champion(
        Component.text()
            .append(Component.text("[", NamedTextColor.YELLOW))
            .append(Component.text("i", Style.style(NamedTextColor.AQUA, TextDecoration.OBFUSCATED)))
            .append(Component.text("CHAMPION", NamedTextColor.GOLD))
            .append(Component.text("i", Style.style(NamedTextColor.AQUA, TextDecoration.OBFUSCATED)))
            .append(Component.text("]", NamedTextColor.YELLOW))
            .build(),
        NamedTextColor.YELLOW
    ),
    Admin("ADMIN", NamedTextColor.DARK_RED, NamedTextColor.RED),
    Mod("MOD", NamedTextColor.GOLD, NamedTextColor.YELLOW),
    Media("MEDIA", NamedTextColor.LIGHT_PURPLE, NamedTextColor.WHITE),
    CT("CT", NamedTextColor.DARK_AQUA, NamedTextColor.AQUA),
    ;

    constructor(name: String, bracketColor: TextColor, rankNameColor: TextColor) : this(
        Component.text()
            .append(Component.text("[", bracketColor))
            .append(Component.text(name, rankNameColor))
            .append(Component.text("]", bracketColor))
            .build(),
        bracketColor)
}