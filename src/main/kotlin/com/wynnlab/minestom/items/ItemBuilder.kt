package com.wynnlab.minestom.items

import com.wynnlab.minestom.core.Element
import com.wynnlab.minestom.core.player.checkPlayerItemSafe
import com.wynnlab.minestom.core.player.greenCheck
import com.wynnlab.minestom.core.player.modifiedSkills
import com.wynnlab.minestom.util.displayNameNonItalic
import com.wynnlab.minestom.util.hideAllFlags
import com.wynnlab.minestom.util.loreNonItalic
import it.unimi.dsi.fastutil.objects.Object2ShortMap
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
import net.minestom.server.item.Enchantment
import net.minestom.server.item.ItemMetaBuilder
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import org.jglrxavpok.hephaistos.nbt.NBT

sealed class ItemBuilder(
    private val id: String?,
    var name: String,
    val type: ItemType,
) {
    private val custom = id != null

    fun setDesign(design: String) {
        type.designs[design]?.let { (m, d) ->
            item = ItemStack.builder(m).meta<ItemMetaBuilder> { itemMeta(it, d); it }
        }
    }

    fun setDesign(material: Material, damage: Int) {
        item = ItemStack.builder(material).meta<ItemMetaBuilder> { itemMeta(it, damage); it }
    }

    var rarity: Rarity = Rarity.Normal

    var sockets: Int = 0

    val skillRequirements = SkillRequirements(0, 0, 0, 0, 0)
    val skillRequirementsPositions = ByteArray(5) { -1 }
    private fun setSkillRequirementsPositionsAt(at: Int) {
        var pos = at.toByte()
        //println(pos)
        if (skillRequirements.strength != 0) skillRequirementsPositions[0] = pos++
        if (skillRequirements.dexterity != 0) skillRequirementsPositions[1] = pos++
        if (skillRequirements.intelligence != 0) skillRequirementsPositions[2] = pos++
        if (skillRequirements.defense != 0) skillRequirementsPositions[3] = pos++
        if (skillRequirements.agility != 0) skillRequirementsPositions[4] = pos//++
        /*println(pos)
        println(skillRequirements)
        println(skillRequirementsPositions.contentToString())*/
    }

    val ids: Object2ShortMap<Identification> = Object2ShortOpenHashMap()
    private val idsCats = BooleanArray(Identification.values().last().cat + 1)

    fun setId(id: Identification, value: Short) {
        if (value == 0.toShort()) {
            ids.removeShort(id)
            if (!ids.any { (k, _) -> k.cat == id.cat }) idsCats[id.cat] = false
        } else {
            idsCats[id.cat] = true
            ids[id] = value
        }
    }
    fun mapIds(source: Map<Identification, Short>) {
        ids.clear()
        idsCats.fill(false)
        for ((k, v) in source) {
            if (v != 0.toShort()) {
                idsCats[k.cat] = true
                ids[k] = v
            }
        }
    }
    fun getId(id: Identification) = ids.getShort(id)

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

    private val commonLore: List<() -> Component?> = mutableListOf(
        { skillRequirementComponent(skillRequirements.strength, "skill.strength") },
        { skillRequirementComponent(skillRequirements.dexterity, "skill.dexterity") },
        { skillRequirementComponent(skillRequirements.intelligence, "skill.intelligence") },
        { skillRequirementComponent(skillRequirements.defense, "skill.defense") },
        { skillRequirementComponent(skillRequirements.agility, "skill.agility") },
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
                val v = ids.getShort(id)
                if (v == 0.toShort()) return@add null
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

    private fun skillRequirementComponent(skill: Int, skillKey: String) =
        skill.takeIf { it != 0 }?.let {
            //Component.text().append(Component.text("?", NamedTextColor.GRAY)).append(Component.text(" $skillKey Min: $it", NamedTextColor.GRAY)).build()
            Component.translatable("item.skill_requirement", NamedTextColor.GRAY, Component.text("?", NamedTextColor.GRAY), Component.translatable(skillKey), Component.text(it))
        }

    private fun refreshDisplayName() {
        item.displayNameNonItalic(Component.text(name, rarity.nameColor))
        item.meta<ItemMetaBuilder> {
            it.set(displayNameTag, name)
            it.set(nameColorTag, rarity.nameColor.value())
        }
    }

    private var item = ItemStack.builder(type.designs["Basic"]!!.material)
        .meta<ItemMetaBuilder> {
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

    private fun item(build: Boolean = false) = item.apply {
        refreshDisplayName()
        val itemLore = mutableListOf<Component>()
        lore.mapNotNullTo(itemLore) { it() }
        if (this@ItemBuilder is Weapon) classReqPos = (itemLore.size - 1).toByte()
        setSkillRequirementsPositionsAt(itemLore.size)

        commonLore.mapNotNullTo(itemLore) { it() }

        if (custom) {
            itemLore.add(if (build) Component.translatable("item.custom_item", NamedTextColor.RED) else Component.translatable("item.concept_item", NamedTextColor.DARK_GRAY))
            itemLore.add(Component.empty())
        }

        customLore.mapTo(itemLore) { Component.text(it, NamedTextColor.DARK_GRAY) }
        if (customLore.isNotEmpty()) itemLore.add(Component.empty())

        itemLore.add(Component.text("$rarity $type", rarity.nameColor))
        if (sockets > 0) itemLore.add(Component.translatable("item.powder_slots", NamedTextColor.DARK_GRAY, Component.text("0"), Component.text(sockets)))

        loreNonItalic(itemLore)

        meta<ItemMetaBuilder> { writeItemMeta(it, this@ItemBuilder); it }
    }.build().let { if (build || !custom) it.withMeta<ItemMetaBuilder> { m -> m
        .clearEnchantment()
        m.removeTag(nameTag)
        m
    } else it }

    fun itemFor(player: Player, build: Boolean = false) = if (build || !custom) item(build).let { checkPlayerItemSafe(it, player.modifiedSkills) ?: it } else item(build)

    class Weapon(id: String?, name: String, type: ItemType) : ItemBuilder(id, name, type) {
        var attackSpeed: AttackSpeed = AttackSpeed.Normal

        val damage = Damage(0..0, 0..0, 0..0, 0..0, 0..0, 0..0)

        private val classReq = when (type) {
            ItemType.Wand -> "Mage"; ItemType.Bow -> "Archer"; ItemType.Dagger -> "Assassin"
            ItemType.Spear -> "Warrior"; ItemType.Relik -> "Shaman"; else -> error("Unreachable code")
        }
        var classReqPos: Byte = 0

        override val lore = mutableListOf(
            { Component.translatable("item.attack_speed", NamedTextColor.DARK_GRAY, attackSpeed.display) },
            { Component.empty() },
            { damage.neutral.takeIf { it.some }?.let {
                Component.text()
                    .append(Element.Neutral.componentWithIcon)
                    .append(Component.text(" "))
                    .append(Component.translatable("identification.damage"))
                    .append(Component.text(": ${it.dashed}"))
                    .color(NamedTextColor.GOLD)
                    .build()
            } },
            { damage.earth.damageComponent(Element.Earth) },
            { damage.thunder.damageComponent(Element.Thunder) },
            { damage.water.damageComponent(Element.Water) },
            { damage.fire.damageComponent(Element.Fire) },
            { damage.air.damageComponent(Element.Air) },
            { if (damage.allNone) null else Component.empty() },
            { Component.translatable("item.class_requirement", NamedTextColor.GRAY, greenCheck, Component.translatable(classReq)) }
        )

        private val IntRange.some get() = first != 0 && last != 0
        private val IntRange.dashed get() = "$first-$last"
        private val Damage.allNone get() = !neutral.some && !earth.some && !thunder.some && !water.some && !air.some
        private fun IntRange.damageComponent(element: Element): Component? = takeIf { it.some }?.let {
            Component.text()
                .append(element.componentWithIcon)
                .append(Component.text(" "))
                .append(Component.translatable("identification.damage", NamedTextColor.GRAY))
                .append(Component.text(": ${it.dashed}", NamedTextColor.GRAY))
                .build()
        }
    }

    sealed class Defense(id: String?, name: String, type: ItemType) : ItemBuilder(id, name, type) {
        var health: Int = 0

        val defense: com.wynnlab.minestom.items.Defense = Defense(0, 0, 0, 0, 0)

        protected val defenseLore: List<() -> Component?> = listOf(
            { health.takeIf { it != 0 }?.let { Component.translatable("item.health", NamedTextColor.DARK_RED, Component.text(it)) } },
            { defense.earth.defenseComponent(Element.Earth) },
            { defense.thunder.defenseComponent(Element.Thunder) },
            { defense.water.defenseComponent(Element.Water) },
            { defense.fire.defenseComponent(Element.Fire) },
            { defense.air.defenseComponent(Element.Air) },
        )

        protected val com.wynnlab.minestom.items.Defense.zero get() = earth == 0 && thunder == 0 && water == 0 && fire == 0 && air == 0
        private fun Int.defenseComponent(element: Element): Component? = takeIf { it != 0 }?.let {
            Component.text()
                .append(element.componentWithIcon)
                .append(Component.text(" "))
                .append(Component.translatable("identification.damage", NamedTextColor.GRAY))
                .append(Component.text(": $it", NamedTextColor.GRAY))
                .build()
        }
    }

    class Armor(id: String?, name: String, type: ItemType) : Defense(id, name, type) {
        override val lore = mutableListOf<() -> Component?>(
            { if (defense.zero && health == 0) null else Component.empty() }
        ).apply {
            addAll(defenseLore)
            add { if (skillRequirements.zero) null else Component.empty() }
        }
    }

    class Accessoire(id: String?, name: String, type: ItemType) : Defense(id, name, type) {
        override val lore = mutableListOf<() -> Component?>(
            { if (defense.zero && health == 0) null else Component.empty() }
        ).apply {
            addAll(defenseLore)
            add { if (skillRequirements.zero) null else Component.empty() }
        }
    }

    companion object {
        fun from(id: String?, name: String, type: ItemType) = when (type) {
            ItemType.Wand, ItemType.Bow, ItemType.Dagger, ItemType.Spear, ItemType.Relik -> Weapon(id, name, type)
            ItemType.Helmet, ItemType.Chestplate, ItemType.Leggings, ItemType.Boots -> Armor(id, name, type)
            else -> Accessoire(id, name, type)
        }

        fun itemBuilderName(player: Player) = "${player.username}#${System.currentTimeMillis()}"

        val nameTag = Tag.String("item-builder-name")
        val typeTag = Tag.String("item-builder-type")

        val displayNameTag = Tag.String("item-display-name")
        val nameColorTag = Tag.Integer("item-name-color")
    }
}