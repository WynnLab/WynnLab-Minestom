package com.wynnlab.minestom.items

import com.wynnlab.minestom.core.Element
import com.wynnlab.minestom.util.displayNameNonItalic
import com.wynnlab.minestom.util.hideAllFlags
import com.wynnlab.minestom.util.loreNonItalic
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
import net.minestom.server.item.Enchantment
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

sealed class ItemBuilder(
    val id: String,
    var name: String,
    val type: ItemType,
) {
    var rarity: Rarity = Rarity.Normal

    val skillRequirements = SkillRequirements(0, 0, 0, 0, 0)

    protected abstract val lore: List<() -> Component?>


    protected val greenCheck = Component.text("✔", NamedTextColor.GREEN)

    protected val commonLore: List<() -> Component?> = listOf(
        { skillRequirementComponent(skillRequirements.strength, "Strength") },
        { skillRequirementComponent(skillRequirements.dexterity, "Dexterity") },
        { skillRequirementComponent(skillRequirements.intelligence, "Intelligence") },
        { skillRequirementComponent(skillRequirements.defense, "Defense") },
        { skillRequirementComponent(skillRequirements.agility, "Agility") }
    )

    protected val SkillRequirements.zero get() = strength == 0 && dexterity == 0 && intelligence == 0 && defense == 0 && agility == 0

    private fun skillRequirementComponent(skill: Int, name: String) =
        skill.takeIf { it != 0 }?.let { Component.text().append(greenCheck).append(Component.text(" $name Min: $it", NamedTextColor.GRAY)).build() }

    private fun refreshDisplayName() = item.displayNameNonItalic(Component.text(name, rarity.nameColor))

    private val item = ItemStack.builder(type.baseMaterial)
        .meta { it
            .damage(type.baseItemDamage)
            .unbreakable(true)
            .hideAllFlags()
            .enchantment(Enchantment.UNBREAKING, 1)
            .setTag(nameTag, id)
            it.setTag(typeTag, type.name)
            it
        }

    fun item() = item.apply {
        refreshDisplayName()
        val itemLore = mutableListOf<Component>()
        lore.mapNotNullTo(itemLore) { it() }
        itemLore.add(Component.empty())
        itemLore.add(Component.text("(Concept)", NamedTextColor.DARK_GRAY))
        loreNonItalic(itemLore)
    }.build()

    class Weapon(id: String, name: String, type: ItemType) : ItemBuilder(id, name, type) {
        var attackSpeed: AttackSpeed = AttackSpeed.Normal

        val damage = Damage(0..0, 0..0, 0..0, 0..0, 0..0, 0..0)

        private val classReq = when (type) {
            ItemType.Wand -> "Mage"; ItemType.Bow -> "Archer"; ItemType.Dagger -> "Assassin"
            ItemType.Spear -> "Warrior"; ItemType.Relik -> "Shaman"; else -> error("Unreachable code")
        }

        override val lore = mutableListOf(
            { Component.text("${attackSpeed.display} Attack Speed", NamedTextColor.DARK_GRAY) },
            { Component.empty() },
            { damage.neutral.takeIf { it.some }?.let { Component.text("${Element.Neutral} Neutral Damage: ${it.dashed}", NamedTextColor.GOLD) } },
            { damage.earth.damageComponent("Earth", Element.Earth) },
            { damage.thunder.damageComponent("Thunder", Element.Thunder) },
            { damage.water.damageComponent("Water", Element.Water) },
            { damage.fire.damageComponent("Fire", Element.Fire) },
            { damage.air.damageComponent("Air", Element.Air) },
            { if (damage.allNone) null else Component.empty() },
            { Component.text().append(greenCheck).append(Component.text(" Class Req: $classReq", NamedTextColor.GRAY)).build() }
        ).apply { addAll(commonLore) }

        private val IntRange.some get() = first != 0 && last != 0
        private val IntRange.dashed get() = "$first-$last"
        private val Damage.allNone get() = !neutral.some && !earth.some && !thunder.some && !water.some && !air.some
        private fun IntRange.damageComponent(name: String, element: Element): Component? = takeIf { it.some }?.let {
            Component.text().append(Component.text("${element.icon} $name ", element.color)).append(Component.text("Damage: ${it.dashed}", NamedTextColor.GRAY)).build()
        }
    }

    sealed class Defense(id: String, name: String, type: ItemType) : ItemBuilder(id, name, type) {
        var health: Int = 0

        val defense: com.wynnlab.minestom.items.Defense = com.wynnlab.minestom.items.Defense(0, 0, 0, 0, 0)

        protected val defenseLore: List<() -> Component?> = listOf<() -> Component?>(
            { health.takeIf { it != 0 }?.let { Component.text("❤ Health: $it", NamedTextColor.DARK_RED) } },
            { defense.earth.defenseComponent("Earth", Element.Earth) },
            { defense.thunder.defenseComponent("Thunder", Element.Thunder) },
            { defense.water.defenseComponent("Water", Element.Water) },
            { defense.fire.defenseComponent("Fire", Element.Fire) },
            { defense.air.defenseComponent("Air", Element.Air) },
        )

        protected val com.wynnlab.minestom.items.Defense.zero get() = earth == 0 && thunder == 0 && water == 0 && fire == 0 && air == 0
        private fun Int.defenseComponent(name: String, element: Element): Component? = takeIf { it != 0 }?.let {
            Component.text().append(Component.text("${element.icon} $name ", element.color)).append(Component.text("Defense: $it", NamedTextColor.GRAY)).build()
        }
    }

    class Armor(id: String, name: String, type: ItemType) : Defense(id, name, type) {
        override val lore = mutableListOf<() -> Component?>(
            { if (defense.zero && health == 0) null else Component.empty() }
        ).apply {
            addAll(defenseLore)
            add { if (skillRequirements.zero) null else Component.empty() }
            addAll(commonLore)
        }
    }

    class Accessoire(id: String, name: String, type: ItemType) : Defense(id, name, type) {
        override val lore = mutableListOf<() -> Component?>(
            { if (defense.zero && health == 0) null else Component.empty() }
        ).apply {
            addAll(defenseLore)
            add { if (skillRequirements.zero) null else Component.empty() }
            addAll(commonLore)
        }
    }

    companion object {
        fun from(id: String, name: String, type: ItemType) = when (type) {
            ItemType.Wand, ItemType.Bow, ItemType.Dagger, ItemType.Spear, ItemType.Relik -> Weapon(id, name, type)
            ItemType.Helmet, ItemType.Chestplate, ItemType.Leggings, ItemType.Boots -> Armor(id, name, type)
            else -> Accessoire(id, name, type)
        }

        fun itemBuilderName(player: Player) = "${player.username}#${System.currentTimeMillis()}"

        val nameTag = Tag.String("builder-name")
        val typeTag = Tag.String("builder-type")
    }
}