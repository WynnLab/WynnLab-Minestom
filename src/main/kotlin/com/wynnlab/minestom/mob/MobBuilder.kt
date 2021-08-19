package com.wynnlab.minestom.mob

import com.wynnlab.minestom.COLOR_DARKER_GRAY
import com.wynnlab.minestom.COLOR_LIGHTER_GRAY
import com.wynnlab.minestom.core.Element
import com.wynnlab.minestom.entities.CustomEntity
import com.wynnlab.minestom.items.*
import com.wynnlab.minestom.textColor
import com.wynnlab.minestom.util.displayNameNonItalic
import com.wynnlab.minestom.util.hideAllFlags
import com.wynnlab.minestom.util.loreNonItalic
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.item.Enchantment
import net.minestom.server.item.ItemMetaBuilder
import net.minestom.server.item.ItemStack
import net.minestom.server.sound.SoundEvent
import net.minestom.server.tag.Tag

class MobBuilder(val id: String, var name: String, type: EntityType, var level: Int) {
    var type = type
    set(value) {
        field = value
        item = ItemStack.builder(typeItemOf(value)).meta { itemMeta(it); it }
    }

    var attackSpeed = AttackSpeed.Normal
    val damage = Damage(0..0, 0..0, 0..0, 0..0, 0..0, 0..0)

    var maxHealth = 1
    var baseDefense = 0f
    val elementalDefense = Defense(0, 0, 0, 0, 0)

    var hurtSound: Sound? = Sound(SoundEvent.ENTITY_GENERIC_HURT, 1f)
    var deathSound: Sound? = Sound(SoundEvent.ENTITY_GENERIC_DEATH, 1f)
    var ambientSound: Sound? = null

    var glowing = false
    //var invisible = false
    var burning = false

    private fun refreshDisplayName() = item.displayNameNonItalic(Component.text(name, NamedTextColor.GOLD))

    private var item = ItemStack.builder(typeItemOf(type)).meta { itemMeta(it); it }

    private fun itemMeta(it: ItemMetaBuilder) {
        it
            .hideAllFlags()
            .enchantment(Enchantment.UNBREAKING, 1)
            .setTag(nameTag, id)
        //it.setTag(ItemBuilder.typeTag, type.name())
    }

    fun item(build: Boolean = false) = item.apply {
        refreshDisplayName()

        val itemLore = mutableListOf<Component>()

        itemLore.add(Component.text("Level $level", NamedTextColor.GRAY))
        itemLore.add(Component.text(type.namespace().toString(), NamedTextColor.DARK_GRAY))
        itemLore.add(Component.empty())

        itemLore.add(Component.text("${attackSpeed.display} Attack Speed", NamedTextColor.DARK_GRAY))
        damageComponent("Neutral", Element.Neutral, damage.neutral)?.let { itemLore.add(it) }
        damageComponent("Earth", Element.Neutral, damage.earth)?.let { itemLore.add(it) }
        damageComponent("Thunder", Element.Thunder, damage.thunder)?.let { itemLore.add(it) }
        damageComponent("Water", Element.Water, damage.water)?.let { itemLore.add(it) }
        damageComponent("Fire", Element.Fire, damage.fire)?.let { itemLore.add(it) }
        damageComponent("Air", Element.Air, damage.air)?.let { itemLore.add(it) }
        itemLore.add(Component.empty())

        itemLore.add(Component.text("❤ Health: $maxHealth", NamedTextColor.DARK_RED))
        itemLore.add(Component.text().append(Component.text("${Element.Neutral.icon} Base Defense: ", NamedTextColor.GOLD))
            .append(Component.text(baseDefense, NamedTextColor.GRAY)).build())
        elementalDefenseComponent("Earth", Element.Earth, elementalDefense.earth)?.let { itemLore.add(it) }
        elementalDefenseComponent("Thunder", Element.Thunder, elementalDefense.thunder)?.let { itemLore.add(it) }
        elementalDefenseComponent("Water", Element.Water, elementalDefense.water)?.let { itemLore.add(it) }
        elementalDefenseComponent("Fire", Element.Fire, elementalDefense.fire)?.let { itemLore.add(it) }
        elementalDefenseComponent("Air", Element.Air, elementalDefense.air)?.let { itemLore.add(it) }
        itemLore.add(Component.empty())

        itemLore.add(soundComponent("Hurt Sound", hurtSound))
        itemLore.add(soundComponent("Death Sound", deathSound))
        itemLore.add(soundComponent("Ambient Sound", ambientSound))
        itemLore.add(Component.empty())

        if (glowing) itemLore.add(Component.text("+Glowing", NamedTextColor.GRAY))
        //if (invisible) itemLore.add(Component.text("+Invisible", NamedTextColor.GRAY))
        if (burning) itemLore.add(Component.text("+Burning", NamedTextColor.GRAY))

        if (glowing || burning) itemLore.add(Component.empty())
        itemLore.add(Component.text(if (build) "Custom Mob" else "Concept Mob", if (build) NamedTextColor.RED else NamedTextColor.DARK_GRAY))

        loreNonItalic(itemLore)

        meta { writeMobMeta(it, this@MobBuilder); it }
    }.build().let { if (build) it.withMeta { m -> m
        .clearEnchantment()
        m.removeTag(nameTag)
        m
    } else it }

    private fun elementalDefenseComponent(name: String, element: Element, value: Int) = value.takeIf { it != 0 }?.let {
        Component.text().append(Component.text("${element.icon} $name ", element.color)).append(Component.text("Defense: $it", NamedTextColor.GRAY)).build()
    }

    private fun damageComponent(name: String, element: Element, value: IntRange) = value.takeIf { it.last > 0 }?.let {
        Component.text().append(Component.text("${element.icon} $name ", element.color)).append(Component.text("Damage: ${it.first}-${it.last}", NamedTextColor.GRAY)).build()
    }

    private fun soundComponent(name: String, value: Sound?) = if (value == null) Component.text(name, NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH)
        else Component.text().append(Component.text("$name: ", NamedTextColor.GREEN))
            .append(Component.text("${value.sound.namespace().path} ", NamedTextColor.GRAY))
            .append(Component.text(value.pitch, NamedTextColor.AQUA))
            .build()

    companion object {
        fun mobBuilderName(player: Player) = "${player.username}#${System.currentTimeMillis()}"

        val nameTag = Tag.String("mob-builder-name")

        fun setCustomEntityBelowNameTag(entity: CustomEntity) {
            val c = Component.text()
            var hasDam = false
            for ((e, v) in entity.damage) {
                if (e == Element.Neutral) continue
                if (v.last > 0) {
                    c.append(Component.text(e.icon, e.color))
                    hasDam = true
                }
            }
            if (hasDam)
                c.append(Component.text(" Dam", COLOR_LIGHTER_GRAY.textColor))
            val def = Component.text()
            var hasDef = false
            val weak = Component.text()
            var hasWeak = false
            for ((e, v) in  entity.defense) {
                if (v == 0) continue
                else if (v > 0) {
                    def.append(Component.text(e.icon, e.color))
                    hasDef = true
                } else {
                    weak.append(Component.text(e.icon, e.color))
                    hasWeak = true
                }
            }
            if (hasDef) {
                if (hasDam) c.append(Component.text(" "))
                c.append(def.build())
                c.append(Component.text(" Def", COLOR_LIGHTER_GRAY.textColor))
            }
            if (hasWeak) {
                if (hasDam || hasDef) c.append(Component.text(" "))
                c.append(weak.build())
                c.append(Component.text(" Weak", COLOR_LIGHTER_GRAY.textColor))
            }
            if (hasDam || hasDef || hasWeak)
                entity.belowNameTagDefault = c.build()
        }
    }
}