package com.wynnlab.minestom.mob

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

        itemLore.add(Component.translatable("mob.item.level", NamedTextColor.GRAY, Component.text(level)))
        itemLore.add(Component.text(type.namespace().toString(), NamedTextColor.DARK_GRAY))
        itemLore.add(Component.empty())

        itemLore.add(Component.translatable("item.attack_speed", NamedTextColor.DARK_GRAY, attackSpeed.display))
        damageComponent(Element.Neutral, damage.neutral)?.let { itemLore.add(it) }
        damageComponent(Element.Neutral, damage.earth)?.let { itemLore.add(it) }
        damageComponent(Element.Thunder, damage.thunder)?.let { itemLore.add(it) }
        damageComponent(Element.Water, damage.water)?.let { itemLore.add(it) }
        damageComponent(Element.Fire, damage.fire)?.let { itemLore.add(it) }
        damageComponent(Element.Air, damage.air)?.let { itemLore.add(it) }
        itemLore.add(Component.empty())

        itemLore.add(Component.translatable("item.health", NamedTextColor.DARK_RED, Component.text(maxHealth)))
        itemLore.add(Component.translatable("mob.item.base_defense", Element.Neutral.color, Component.text(Element.Neutral.icon), Component.text(baseDefense, NamedTextColor.GRAY)))
        elementalDefenseComponent(Element.Earth, elementalDefense.earth)?.let { itemLore.add(it) }
        elementalDefenseComponent(Element.Thunder, elementalDefense.thunder)?.let { itemLore.add(it) }
        elementalDefenseComponent(Element.Water, elementalDefense.water)?.let { itemLore.add(it) }
        elementalDefenseComponent(Element.Fire, elementalDefense.fire)?.let { itemLore.add(it) }
        elementalDefenseComponent(Element.Air, elementalDefense.air)?.let { itemLore.add(it) }
        itemLore.add(Component.empty())

        itemLore.add(soundComponent("mob.item.sound.hurt", hurtSound))
        itemLore.add(soundComponent("mob.item.sound.death", deathSound))
        itemLore.add(soundComponent("mob.item.sound.ambient", ambientSound))
        itemLore.add(Component.empty())

        if (glowing) itemLore.add(Component.translatable("mob.item.glowing", NamedTextColor.GRAY))
        //if (invisible) itemLore.add(Component.text("+Invisible", NamedTextColor.GRAY))
        if (burning) itemLore.add(Component.translatable("mob.item.burning", NamedTextColor.GRAY))

        if (glowing || burning) itemLore.add(Component.empty())
        itemLore.add(Component.translatable(if (build) "mob.item.custom_mob" else "mob.item.concept_mob", if (build) NamedTextColor.RED else NamedTextColor.DARK_GRAY))

        loreNonItalic(itemLore)

        meta { writeMobMeta(it, this@MobBuilder); it }
    }.build().let { if (build) it.withMeta { m -> m
        .clearEnchantment()
        m.removeTag(nameTag)
        m
    } else it }

    private fun elementalDefenseComponent(element: Element, value: Int) = value.takeIf { it != 0 }?.let {
        Component.text()
            .append(element.componentWithIcon)
            .append(Component.text(" "))
            .append(Component.translatable("identification.defense", NamedTextColor.GRAY))
            .append(Component.text(": $it", NamedTextColor.GRAY))
            .build()
    }

    private fun damageComponent(element: Element, value: IntRange) = value.takeIf { it.last > 0 }?.let {
        Component.text()
            .append(element.componentWithIcon)
            .append(Component.text(" "))
            .append(Component.translatable("identification.damage", NamedTextColor.GRAY))
            .append(Component.text(": ${it.first}-${it.last}", NamedTextColor.GRAY))
            .build()
    }

    private fun soundComponent(nameKey: String, value: Sound?) = if (value == null) Component.text(nameKey, NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH)
        else Component.translatable(nameKey, NamedTextColor.GREEN, Component.text("${value.sound.namespace().path} ", NamedTextColor.GRAY), Component.text(value.pitch, NamedTextColor.AQUA))

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
                c.append(Component.translatable("mob.below_name.damage", COLOR_LIGHTER_GRAY.textColor))
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
                c.append(Component.translatable("mob.below_name.defense", COLOR_LIGHTER_GRAY.textColor))
            }
            if (hasWeak) {
                if (hasDam || hasDef) c.append(Component.text(" "))
                c.append(weak.build())
                c.append(Component.translatable("mob.below_name.weakness", COLOR_LIGHTER_GRAY.textColor))
            }
            if (hasDam || hasDef || hasWeak)
                entity.belowNameTagDefault = c.build()
        }
    }
}