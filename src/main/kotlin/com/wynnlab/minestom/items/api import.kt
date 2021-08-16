package com.wynnlab.minestom.items

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap
import net.minestom.server.item.Material

fun itemBuilderFrom(json: JsonElement): ItemBuilder {
    val root = json.asJsonObject
    val builder = ItemBuilder.from(null,
        (root["displayName"] ?: root["name"]).asString,
        ItemType.valueOf((root["type"] ?: root["accessoryType"]).asString))

    root["material"]?.takeUnless { it is JsonNull }?.let {
        val (material, itemDamage) = it.asString.split(':')
        builder.setDesign(idToMaterial[material.toInt()]!!, itemDamage.toInt())
    }
    root["armorType"]?.let { builder.setDesign(it.asString) }

    builder.rarity = Rarity.valueOf(root["tier"].asString)
    builder.sockets = root["sockets"].asInt
    root["addedLore"]?.takeUnless { it is JsonNull }?.let {
        builder.setCustomLore(it.asString.split(' ').toTypedArray())
    }

    if (builder is ItemBuilder.Weapon) {
        builder.damage.neutral = root["damage"].asIntRange
        builder.damage.earth = root["earthDamage"].asIntRange
        builder.damage.thunder = root["thunderDamage"].asIntRange
        builder.damage.water = root["waterDamage"].asIntRange
        builder.damage.fire = root["fireDamage"].asIntRange
        builder.damage.air = root["airDamage"].asIntRange
        builder.attackSpeed = when(root["attackSpeed"].asString) {
            "SUPER_SLOW" -> AttackSpeed.SuperSlow; "VERY_SLOW" -> AttackSpeed.VerySlow; "SLOW" -> AttackSpeed.Slow
            "NORMAL" -> AttackSpeed.Normal
            "FAST" -> AttackSpeed.Fast; "VERY_FAST" -> AttackSpeed.VeryFast; "SUPER_FAST" -> AttackSpeed.SuperFast
            else -> error("Unknown Attack Speed")
        }
    }

    if (builder is ItemBuilder.Defense) {
        builder.health = root["health"].asInt
        builder.defense.earth = root["earthDefense"].asInt
        builder.defense.thunder = root["thunderDefense"].asInt
        builder.defense.water = root["waterDefense"].asInt
        builder.defense.fire = root["fireDefense"].asInt
        builder.defense.air = root["airDefense"].asInt
    }

    builder.skillRequirements.strength = root["strength"].asInt
    builder.skillRequirements.dexterity = root["dexterity"].asInt
    builder.skillRequirements.intelligence = root["intelligence"].asInt
    builder.skillRequirements.defense = root["defense"].asInt
    builder.skillRequirements.agility = root["agility"].asInt

    val identified = root["identified"]?.asBoolean ?: false
    val rawIds = Object2ShortOpenHashMap<Identification>()

    rawIds[Identification.HealthRegen] = root["healthRegen"].asShort
    rawIds[Identification.ManaRegen] = root["manaRegen"].asShort
    rawIds[Identification.SpellDamage] = root["spellDamage"].asShort
    rawIds[Identification.DamageBonus] = root["damageBonus"].asShort
    rawIds[Identification.LifeSteal] = root["lifeSteal"].asShort
    rawIds[Identification.ManaSteal] = root["manaSteal"].asShort
    rawIds[Identification.Reflection] = root["reflection"].asShort
    rawIds[Identification.Strength] = root["strengthPoints"].asShort
    rawIds[Identification.Dexterity] = root["dexterityPoints"].asShort
    rawIds[Identification.Intelligence] = root["intelligencePoints"].asShort
    rawIds[Identification.Defense] = root["defensePoints"].asShort
    rawIds[Identification.Agility] = root["agilityPoints"].asShort
    rawIds[Identification.Thorns] = root["thorns"].asShort
    rawIds[Identification.Exploding] = root["exploding"].asShort
    rawIds[Identification.WalkSpeed] = root["speed"].asShort
    rawIds[Identification.AttackSpeed] = root["attackSpeedBonus"].asShort
    rawIds[Identification.Poison] = root["poison"].asShort
    rawIds[Identification.HealthBonus] = root["healthBonus"].asShort
    rawIds[Identification.HealthRegenRaw] = root["healthRegenRaw"].asShort
    rawIds[Identification.SpellDamageRaw] = root["spellDamageRaw"].asShort
    rawIds[Identification.DamageBonusRaw] = root["damageBonusRaw"].asShort
    rawIds[Identification.BonusEarthDamage] = root["bonusEarthDamage"].asShort
    rawIds[Identification.BonusThunderDamage] = root["bonusThunderDamage"].asShort
    rawIds[Identification.BonusWaterDamage] = root["bonusWaterDamage"].asShort
    rawIds[Identification.BonusFireDamage] = root["bonusFireDamage"].asShort
    rawIds[Identification.BonusAirDamage] = root["bonusAirDamage"].asShort
    rawIds[Identification.BonusEarthDefense] = root["bonusEarthDefense"].asShort
    rawIds[Identification.BonusThunderDefense] = root["bonusThunderDefense"].asShort
    rawIds[Identification.BonusWaterDefense] = root["bonusWaterDefense"].asShort
    rawIds[Identification.BonusFireDefense] = root["bonusFireDefense"].asShort
    rawIds[Identification.BonusAirDefense] = root["bonusAirDefense"].asShort

    rawIds[Identification.JumpHeight] = root["jumpHeight"].asShort

    builder.mapIds(if (identified) rawIds else rawIds.mapValues { (_, v) ->
        if (v > 0) (v * 1.3).coerceAtLeast(1.0).toInt().toShort()
        else if (v < 0) (v * .7).coerceAtMost(-1.0).toInt().toShort()
        else 0.toShort()
    })

    return builder
}

private val JsonElement.asIntRange get() = asString.split('-').let { (start, end) -> start.toInt()..end.toInt() }

private val idToMaterial = hashMapOf(
    261 to Material.BOW,
    259 to Material.FLINT_AND_STEEL,
    256 to Material.IRON_SHOVEL,
    359 to Material.SHEARS,
    280 to Material.STICK,
    273 to Material.STONE_SHOVEL,
    269 to Material.WOODEN_SHOVEL,
    295 to Material.DIAMOND_AXE,
    277 to Material.DIAMOND_SHOVEL,
    278 to Material.DIAMOND_PICKAXE,
    284 to Material.GOLDEN_SHOVEL
)