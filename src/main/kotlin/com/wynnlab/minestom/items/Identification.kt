package com.wynnlab.minestom.items

import com.wynnlab.minestom.core.Element
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

enum class Identification(val cat: Int, val display: Component, val suffix: String = "%", val invertedColors: Boolean = false) {
    Strength(0, Component.text("Strength"), ""),
    Dexterity(0, Component.text("Dexterity"), ""),
    Intelligence(0, Component.text("Intelligence"), ""),
    Defense(0, Component.text("Defense"), ""),
    Agility(0, Component.text("Agility"), ""),

    SpellDamageRaw(1, Component.text("✣ Spell", NamedTextColor.GOLD).append(Component.text(" Damage", NamedTextColor.GRAY)), ""),
    SpellDamage(1, Component.text("Spell Damage")),
    DamageBonusRaw(1, Component.text("✣ Main Attack", NamedTextColor.GOLD).append(Component.text(" Damage", NamedTextColor.GRAY)), ""),
    DamageBonus(1, Component.text("Main Attack Damage")),

    BonusEarthDamage(2, Component.text("${Element.Earth.icon} Earth", Element.Earth.color).append(Component.text(" Damage", NamedTextColor.GRAY))),
    BonusThunderDamage(2, Component.text("${Element.Thunder.icon} Thunder", Element.Thunder.color).append(Component.text(" Damage", NamedTextColor.GRAY))),
    BonusWaterDamage(2, Component.text("${Element.Water.icon} Water", Element.Water.color).append(Component.text(" Damage", NamedTextColor.GRAY))),
    BonusFireDamage(2, Component.text("${Element.Fire.icon} Fire", Element.Fire.color).append(Component.text(" Damage", NamedTextColor.GRAY))),
    BonusAirDamage(2, Component.text("${Element.Air.icon} Air", Element.Air.color).append(Component.text(" Damage", NamedTextColor.GRAY))),

    BonusEarthDefense(3, Component.text("${Element.Earth.icon} Earth", Element.Earth.color).append(Component.text(" Defense", NamedTextColor.GRAY))),
    BonusThunderDefense(3, Component.text("${Element.Thunder.icon} Thunder", Element.Thunder.color).append(Component.text(" Defense", NamedTextColor.GRAY))),
    BonusWaterDefense(3, Component.text("${Element.Water.icon} Water", Element.Water.color).append(Component.text(" Defense", NamedTextColor.GRAY))),
    BonusFireDefense(3, Component.text("${Element.Fire.icon} Fire", Element.Fire.color).append(Component.text(" Defense", NamedTextColor.GRAY))),
    BonusAirDefense(3, Component.text("${Element.Air.icon} Air", Element.Air.color).append(Component.text(" Defense", NamedTextColor.GRAY))),

    ManaRegen(4, Component.text("Mana Regen"), "/4s"),
    ManaSteal(4, Component.text("Mana Steal"), "/4s"),

    HealthBonus(5, Component.text("Health"), ""),
    HealthRegen(5, Component.text("Health Regen")),
    HealthRegenRaw(5, Component.text("Health Regen"), ""),
    LifeSteal(5, Component.text("Life Steal"), "/4s"),

    WalkSpeed(6, Component.text("Walk Speed")),

    AttackSpeed(7, Component.text("Attack Speed"), " tier"),

    Poison(8, Component.text("Poison"), "/3s"),
    Reflection(8, Component.text("Reflection")),
    Thorns(8, Component.text("Thorns")),
    Exploding(8, Component.text("Exploding")),

    SpellCost1Pct(9, Component.text("1st Spell Cost"), invertedColors = true),
    SpellCost2Pct(9, Component.text("2nd Spell Cost"), invertedColors = true),
    SpellCost3Pct(9, Component.text("3rd Spell Cost"), invertedColors = true),
    SpellCost4Pct(9, Component.text("4th Spell Cost"), invertedColors = true),
    SpellCost1Raw(9, Component.text("1st Spell Cost"), "", true),
    SpellCost2Raw(9, Component.text("2nd Spell Cost"), "", true),
    SpellCost3Raw(9, Component.text("3rd Spell Cost"), "", true),
    SpellCost4Raw(9, Component.text("4th Spell Cost"), "", true),

    RainbowSpellDamageRaw(10, Component.text("Rainbow Spell Damage"), ""),

    JumpHeight(11, Component.text("Jump Height"), ""),
    ;

    val tag: Tag<Short> = Tag.Short("I_$name").defaultValue(0)

    fun get(item: ItemStack) = item.getTag(tag)!!
}