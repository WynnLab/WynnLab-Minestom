package com.wynnlab.minestom.items

import com.wynnlab.minestom.core.Element
import com.wynnlab.minestom.util.displayNameNonItalic
import com.wynnlab.minestom.util.hideAllFlags
import com.wynnlab.minestom.util.loreNonItalic
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
import net.minestom.server.item.Enchantment
import net.minestom.server.item.ItemMetaBuilder
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag

sealed class ItemBuilder(
    private val id: String?,
    var name: String,
    val type: ItemType,
) {
    private val custom = id != null

    fun setDesign(design: String) {
        type.designs[design]?.let { (m, d) ->
            item = ItemStack.builder(m).meta { itemMeta(it, d); it }
        }
    }

    fun setDesign(material: Material, damage: Int) {
        item = ItemStack.builder(material).meta { itemMeta(it, damage); it }
    }

    var rarity: Rarity = Rarity.Normal

    var sockets: Int = 0

    val skillRequirements = SkillRequirements(0, 0, 0, 0, 0)

    private val ids: Object2IntMap<Identification> = Object2IntOpenHashMap()
    private val idsCats = BooleanArray(Identification.values().last().cat + 1)

    fun setId(id: Identification, value: Int) {
        if (value == 0) {
            ids.removeInt(id)
            if (!ids.any { (k, _) -> k.cat == id.cat }) idsCats[id.cat] = false
        } else {
            idsCats[id.cat] = true
            ids[id] = value
        }
    }
    fun mapIds(source: Map<Identification, Int>) {
        ids.clear()
        idsCats.fill(false)
        for ((k, v) in source) {
            if (v != 0) {
                idsCats[k.cat] = true
                ids[k] = v
            }
        }
    }
    fun getId(id: Identification) = ids.getInt(id)

    private var customLore: List<String> = emptyList()

    fun setCustomLore(customLore: Array<String>) {
        val cl = mutableListOf<String>()
        var c = -1
        var nl = true
        var sb = StringBuilder()
        customLore.forEach {
            c += it.length + 1
            if (c > 30) {
                c = it.length
                cl.add(sb.toString())
                sb = StringBuilder()
                nl = true
            }
            if (!nl)
                sb.append(' ')
            else
                nl = false
            sb.append(it)
        }
        cl.add(sb.toString())
        this.customLore = cl
    }

    protected abstract val lore: List<() -> Component?>


    protected val greenCheck = Component.text("✔", NamedTextColor.GREEN)

    protected val commonLore: List<() -> Component?> = mutableListOf(
        { skillRequirementComponent(skillRequirements.strength, "Strength") },
        { skillRequirementComponent(skillRequirements.dexterity, "Dexterity") },
        { skillRequirementComponent(skillRequirements.intelligence, "Intelligence") },
        { skillRequirementComponent(skillRequirements.defense, "Defense") },
        { skillRequirementComponent(skillRequirements.agility, "Agility") },
        { Component.empty() }
    ).apply {
        var cat = -1
        for (id in Identification.values()) {
            if (id.cat > cat) {
                val c = cat
                if (c >= 0) add { if (idsCats[c]) Component.empty() else null }
                cat = id.cat
            }
            add {
                val v = ids.getInt(id)
                if (v == 0) return@add null
                val vColor = if (id.invertedColors xor (v > 0)) NamedTextColor.GREEN else NamedTextColor.RED
                Component.text()
                    .append(Component.text(if (v >= 0) "+$v" else v.toString(), vColor))
                    .append(Component.text("${id.suffix} ", vColor))
                    .append(id.display.colorIfAbsent(NamedTextColor.GRAY))
                    .build()
            }
        }
        val maxCat = idsCats.size - 1
        add { if (idsCats[maxCat]) Component.empty() else null }
    }

    protected val SkillRequirements.zero get() = strength == 0 && dexterity == 0 && intelligence == 0 && defense == 0 && agility == 0

    private fun skillRequirementComponent(skill: Int, name: String) =
        skill.takeIf { it != 0 }?.let { Component.text().append(greenCheck).append(Component.text(" $name Min: $it", NamedTextColor.GRAY)).build() }

    private fun refreshDisplayName() = item.displayNameNonItalic(Component.text(name, rarity.nameColor))

    private var item = ItemStack.builder(type.designs["Basic"]!!.material)
        .meta {
            itemMeta(it, type.designs["Basic"]!!.damage)
            it
        }

    private fun itemMeta(it: ItemMetaBuilder, damage: Int) {
        it
            .damage(damage)
            .unbreakable(true)
            .hideAllFlags()
            .enchantment(Enchantment.UNBREAKING, 1)
            .setTag(nameTag, id)
        it.setTag(typeTag, type.name)
    }

    fun item(build: Boolean = false) = item.apply {
        refreshDisplayName()
        val itemLore = mutableListOf<Component>()
        lore.mapNotNullTo(itemLore) { it() }

        if (custom) {
            itemLore.add(if (build) Component.text("Custom Item", NamedTextColor.RED) else Component.text("Concept Item", NamedTextColor.DARK_GRAY))
            itemLore.add(Component.empty())
        }

        customLore.mapTo(itemLore) { Component.text(it, NamedTextColor.DARK_GRAY) }
        if (customLore.isNotEmpty()) itemLore.add(Component.empty())

        itemLore.add(Component.text("$rarity $type", rarity.nameColor))
        if (sockets > 0) itemLore.add(Component.text("[0/$sockets] Powder Slots", NamedTextColor.DARK_GRAY))

        loreNonItalic(itemLore)
    }.build().let { if (build || !custom) it.withMeta { m -> m
        .clearEnchantment()
        m.removeTag(nameTag)
        m
    } else it }

    class Weapon(id: String?, name: String, type: ItemType) : ItemBuilder(id, name, type) {
        var attackSpeed: AttackSpeed = AttackSpeed.Normal

        val damage = Damage(0..0, 0..0, 0..0, 0..0, 0..0, 0..0)

        private val classReq = when (type) {
            ItemType.Wand -> "Mage"; ItemType.Bow -> "Archer"; ItemType.Dagger -> "Assassin"
            ItemType.Spear -> "Warrior"; ItemType.Relik -> "Shaman"; else -> error("Unreachable code")
        }

        override val lore = mutableListOf(
            { Component.text("${attackSpeed.display} Attack Speed", NamedTextColor.DARK_GRAY) },
            { Component.empty() },
            { damage.neutral.takeIf { it.some }?.let { Component.text("${Element.Neutral.icon} Neutral Damage: ${it.dashed}", NamedTextColor.GOLD) } },
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

    sealed class Defense(id: String?, name: String, type: ItemType) : ItemBuilder(id, name, type) {
        var health: Int = 0

        val defense: com.wynnlab.minestom.items.Defense = Defense(0, 0, 0, 0, 0)

        protected val defenseLore: List<() -> Component?> = listOf(
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

    class Armor(id: String?, name: String, type: ItemType) : Defense(id, name, type) {
        override val lore = mutableListOf<() -> Component?>(
            { if (defense.zero && health == 0) null else Component.empty() }
        ).apply {
            addAll(defenseLore)
            add { if (skillRequirements.zero) null else Component.empty() }
            addAll(commonLore)
        }
    }

    class Accessoire(id: String?, name: String, type: ItemType) : Defense(id, name, type) {
        override val lore = mutableListOf<() -> Component?>(
            { if (defense.zero && health == 0) null else Component.empty() }
        ).apply {
            addAll(defenseLore)
            add { if (skillRequirements.zero) null else Component.empty() }
            addAll(commonLore)
        }
    }

    companion object {
        fun from(id: String?, name: String, type: ItemType) = when (type) {
            ItemType.Wand, ItemType.Bow, ItemType.Dagger, ItemType.Spear, ItemType.Relik -> Weapon(id, name, type)
            ItemType.Helmet, ItemType.Chestplate, ItemType.Leggings, ItemType.Boots -> Armor(id, name, type)
            else -> Accessoire(id, name, type)
        }

        fun itemBuilderName(player: Player) = "${player.username}#${System.currentTimeMillis()}"

        val nameTag = Tag.String("builder-name")
        val typeTag = Tag.String("builder-type")
    }
}