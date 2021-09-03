package com.wynnlab.minestom.items

import com.wynnlab.minestom.core.Element
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

enum class Identification(val cat: Int, val display: Component, val suffix: String = "%", val invertedColors: Boolean = false) {
    Strength(0, Component.translatable("skill.strength"), ""),
    Dexterity(0, Component.translatable("skill.dexterity"), ""),
    Intelligence(0, Component.translatable("skill.intelligence"), ""),
    Defense(0, Component.translatable("skill.defense"), ""),
    Agility(0, Component.translatable("skill.agility"), ""),

    SpellDamageRaw(1, Component.text()
        .append(Component.text("✣", NamedTextColor.GOLD))
        .append(Component.translatable("identification.spell_damage.spell", NamedTextColor.GOLD))
        .append(Component.translatable("identification.damage", NamedTextColor.GRAY))
        .build(), ""),
    SpellDamage(1, Component.translatable("identification.spell_damage")),
    DamageBonusRaw(1, Component.text()
        .append(Component.text("✣", NamedTextColor.GOLD))
        .append(Component.translatable("identification.damage_bonus.main_attack", NamedTextColor.GOLD))
        .append(Component.translatable("identification.damage", NamedTextColor.GRAY))
        .build(), ""),
    DamageBonus(1, Component.translatable("identification.damage_bonus")),

    BonusEarthDamage(2, elementalComponent(Element.Earth, "identification.damage")),
    BonusThunderDamage(2, elementalComponent(Element.Thunder, "identification.damage")),
    BonusWaterDamage(2, elementalComponent(Element.Water, "identification.damage")),
    BonusFireDamage(2, elementalComponent(Element.Fire, "identification.damage")),
    BonusAirDamage(2, elementalComponent(Element.Air, "identification.damage")),

    BonusEarthDefense(3, elementalComponent(Element.Earth, "identification.defense")),
    BonusThunderDefense(3, elementalComponent(Element.Thunder, "identification.defense")),
    BonusWaterDefense(3, elementalComponent(Element.Water, "identification.defense")),
    BonusFireDefense(3, elementalComponent(Element.Fire, "identification.defense")),
    BonusAirDefense(3, elementalComponent(Element.Air, "identification.defense")),

    ManaRegen(4, Component.translatable("identification.mana_regen"), "/4s"),
    ManaSteal(4, Component.translatable("identification.mana_steal"), "/4s"),

    HealthBonus(5, Component.translatable("identification.health_bonus"), ""),
    HealthRegen(5, Component.translatable("identification.health_regen")),
    HealthRegenRaw(5, Component.translatable("identification.health_regen"), ""),
    LifeSteal(5, Component.translatable("identification.life_steal"), "/4s"),

    WalkSpeed(6, Component.translatable("identification.walk_speed")),

    AttackSpeed(7, Component.translatable("identification.attack_speed"), " tier"),

    Poison(8, Component.translatable("identification.poison"), "/3s"),
    Reflection(8, Component.translatable("identification.reflection")),
    Thorns(8, Component.translatable("identification.thorns")),
    Exploding(8, Component.translatable("identification.exploding")),

    SpellCost1Pct(9, Component.translatable("identification.spell_cost_1"), invertedColors = true),
    SpellCost2Pct(9, Component.translatable("identification.spell_cost_2"), invertedColors = true),
    SpellCost3Pct(9, Component.translatable("identification.spell_cost_3"), invertedColors = true),
    SpellCost4Pct(9, Component.translatable("identification.spell_cost_4"), invertedColors = true),
    SpellCost1Raw(9, Component.translatable("identification.spell_cost_1"), "", true),
    SpellCost2Raw(9, Component.translatable("identification.spell_cost_2"), "", true),
    SpellCost3Raw(9, Component.translatable("identification.spell_cost_3"), "", true),
    SpellCost4Raw(9, Component.translatable("identification.spell_cost_4"), "", true),

    RainbowSpellDamageRaw(10, Component.translatable("identification.spell_damage"), ""),

    JumpHeight(11, Component.translatable("identification.jump_height"), ""),
    ;

    val tag: Tag<Short> = Tag.Short("I_$name").defaultValue(0)

    fun get(item: ItemStack) = item.getTag(tag)!!
}

fun elementalComponent(element: Element, secondKey: String) = Component.text()
    .append(element.componentWithIcon)
    .append(Component.text(" "))
    .append(Component.translatable(secondKey, NamedTextColor.GRAY))
    .build()