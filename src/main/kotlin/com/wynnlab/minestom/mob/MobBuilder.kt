package com.wynnlab.minestom.mob

import com.wynnlab.minestom.core.Element
import com.wynnlab.minestom.items.*
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
    var invisible = false
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
        if (invisible) itemLore.add(Component.text("+Invisible", NamedTextColor.GRAY))
        if (burning) itemLore.add(Component.text("+Burning", NamedTextColor.GRAY))

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
    private fun soundComponent(name: String, value: Sound?) = if (value == null) Component.text(name, NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH)
        else Component.text().append(Component.text("$name: ", NamedTextColor.GREEN))
            .append(Component.text("${value.sound.namespace()} ", NamedTextColor.GRAY))
            .append(Component.text(value.pitch, NamedTextColor.AQUA))
            .build()

    companion object {
        fun mobBuilderName(player: Player) = "${player.username}#${System.currentTimeMillis()}"

        val nameTag = Tag.String("mob-builder-name")
    }
}