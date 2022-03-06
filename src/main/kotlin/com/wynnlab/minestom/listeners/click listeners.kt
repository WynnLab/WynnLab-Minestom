package com.wynnlab.minestom.listeners

import com.wynnlab.minestom.base.BaseClass
import com.wynnlab.minestom.base.playerClassTag
import com.wynnlab.minestom.base.playerCloneClassTag
import com.wynnlab.minestom.classes.getClassById
import com.wynnlab.minestom.core.player.*
import com.wynnlab.minestom.gui.MenuGui
import com.wynnlab.minestom.items.Identification
import com.wynnlab.minestom.mob.MobCommand
import com.wynnlab.minestom.mob.getCustomMob
import com.wynnlab.minestom.mob.mobTypeIdTag
import com.wynnlab.minestom.tasks.RefreshDelayTask
import com.wynnlab.minestom.textColor
import com.wynnlab.minestom.util.displayNameNonItalic
import com.wynnlab.minestom.util.listen
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.item.builder
import net.minestom.server.sound.SoundEvent
import net.minestom.server.tag.Tag
import net.minestom.server.utils.time.TimeUnit
import kotlin.math.ceil
import kotlin.math.floor

val playerSpellClickListenersNode = EventNode.type("player-spell-click-listeners", EventFilter.PLAYER) { e, player ->
    e is PlayerBlockInteractEvent && e.hand == Player.Hand.MAIN
            || e is PlayerUseItemEvent && e.hand == Player.Hand.MAIN
            || e is PlayerHandAnimationEvent && e.hand == Player.Hand.MAIN && player.openInventory == null
}

private fun onPlayerHandAnimation(e: PlayerHandAnimationEvent) {
    e.isCancelled = true
    onPlayerLeftClick(e.player)
}

private fun onPlayerUseItem(e: PlayerUseItemEvent) {
    e.isCancelled = true
    onPlayerRightClick(e.player)
}

private fun onPlayerRightClick(player: Player) {
    when (player.heldSlot.toInt()) {
        6 -> MenuGui.show(player)
        7, 8 -> {}
        else -> {
            if (player.itemInMainHand.hasTag(mobTypeIdTag)) {
                MobCommand.spawn(player.instance ?: return, player.position, getCustomMob(player.itemInMainHand) ?: return)
                return
            } //TODO: structure
            if (player.itemWeapon == null) return
            val spell = when (addToClickSequence(player, true)) {
                clickSequenceSpellMap[1] /* rlr */ -> 1
                clickSequenceSpellMap[2] /* rrr */ -> 2
                else -> -1
            }
            scheduleResetClickSeqAndAB(player)
            if (spell > 0) castSpellAndResetClickSequence(player, spell)
        }
    }
}

private fun onPlayerLeftClick(player: Player) {
    if (player.heldSlot > 5) return
    val seq = addToClickSequence(player, false)
    if (seq == 2.toByte()) // 1x left-click
        castSpellAndResetClickSequence(player, 0)
    else {
        if (player.itemWeapon == null) return
        val spell = when (seq) {
            clickSequenceSpellMap[3] /* rll */ -> 3
            clickSequenceSpellMap[4] /* rrl */ -> 4
            else -> -1
        }
        scheduleResetClickSeqAndAB(player)
        if (spell > 0) castSpellAndResetClickSequence(player, spell)
    }
}

private fun addToClickSequence(player: Player, rightClick: Boolean): Byte {
    val count = if (rightClick) 1 else 2
    val prev = player.getTag(clickSequenceTag)!!.toInt()
    val new = (prev + (if (prev == 0) count else if (prev >= 3) count * 3 * 3 else count * 3)).toByte()
    player.setTag(clickSequenceTag, new)
    return new
}

fun clickSeqAbComponent(player: Player): TextComponent? {
    return when (val sequence = player.getTag(clickSequenceTag)!!) {
        in clickSequenceSpellMap -> null//spellCastAbComponent(clickSequenceSpellMap.indexOf(sequence))
        else -> {
            val c1 = sequence % 3
            val c2 = (sequence / 3) % 3
            val c3 = sequence / 9
            Component.text()
                .append(clickSequenceAbChar(c1))
                .append(hyphenComponent)
                .append(clickSequenceAbChar(c2))
                .append(hyphenComponent)
                .append(clickSequenceAbChar(c3))
                .build()
        }
    }
}

private fun spellCastAbComponent(clazz: BaseClass, clone: Boolean, index: Int, cost: Int) = Component.text()
    .append(Component.translatable("class.${clazz.id}.spell.$index${if (clone) ".clone" else ""}", 0x75ebf0.textColor))
    .append(Component.space())
    .append(Component.translatable("spell.message.success.case", 0x9feaed.textColor))
    .append(Component.text(" [", 0x23abb0.textColor))
    .append(Component.text(-cost, 0x23e1e8.textColor))
    .append(Component.text("✺ ", 0x2bd3d9.textColor))
    .append(Component.text("]", 0x23abb0.textColor))
    .build()
private val notEnoughManaComponent = Component.translatable("spell.message.not_enough_mana", NamedTextColor.DARK_RED)

private val hyphenComponent = Component.text("-", NamedTextColor.GRAY)

private fun clickSequenceAbChar(c: Int) = when (c) {
    1 -> Component.text("R", NamedTextColor.GREEN, TextDecoration.UNDERLINED)
    2 -> Component.text("L", NamedTextColor.GREEN, TextDecoration.UNDERLINED)
    else -> Component.text("?", NamedTextColor.WHITE)
}

// stored as base 3 value: r = 1, l = 2
// r-?-? = 1
// r-r-? = 4; r-l-? = 7
private val clickSequenceTag = Tag.Byte("click-sequence").defaultValue(0)

private val clickSequenceSpellMap = byteArrayOf(-1, 16, 13, 25, 22)

private fun scheduleResetClickSeqAndAB(player: Player) {
    refreshClickSequenceBar(player)
    RefreshDelayTask(player, "click-seq-ab", player::clickSeqAb).schedule(1, TimeUnit.SECOND)
}

private fun Player.clickSeqAb() {
    resetClickSequence(this)
    resetClickSequenceBar(this)
}

//val clickSeqAbTag = Tag.Byte("click-seq-ab")

private fun castSpellAndResetClickSequence(player: Player, index: Int) {
    /*if (spell == 0) player.rayCastEntity(maxDistance = 4.0) { it is CustomEntity || it is Player }?.let {
        if (it is CustomEntity) player.attack(it, NeutralDamageModifiers)
        else player.attack(it as Player, NeutralDamageModifiers)
    }
    if (spell == 1) Mage.spells[0](player).schedule()*/
    resetClickSequence(player)

    val playerClass = getClassById(player.getTag(playerClassTag) ?: "null") ?: return
    val spell = playerClass.spells.getOrNull(index)?.invoke(player) ?: return
    //player.setGravity(player.gravityDragPerTick / 2f, player.gravityAcceleration / 2f)
    //val p = Particle.particle(HAPPY_VILLAGER, 10, ParticleType.OffsetAndSpeed(0f, 0f, 0f, 0f))
    //val p = Particle.particle(ITEM, 10, ParticleType.OffsetAndSpeed(0f, 0f, 0f, 0f), Item(player.itemInMainHand))
    //player.showParticle(p, player.position)
    //player.sendMessage("spell $spell")
    if (index > 0) {
        val cost = spellCost(player, spell.cost, index)

        player.setTag(extraSpellCostTag, (if (player.getTag(lastCastSpellTag)!!.toInt() == index) player.getTag(extraSpellCostTag)!! + 1 else 0).toByte())
        player.setTag(lastCastSpellTag, index.toByte())

        RefreshDelayTask(player, "spell-cost-extra") {
            player.setTag(extraSpellCostTag, null)
            player.setTag(lastCastSpellTag, null)
        }.schedule(5L, TimeUnit.SECOND)

        val currentMana = player.food

        player.playSound(Sound.sound(
            if (currentMana >= cost) SoundEvent.ENTITY_EXPERIENCE_ORB_PICKUP else SoundEvent.BLOCK_ANVIL_PLACE, Sound.Source.MASTER,
            1f,
            if (currentMana >= cost) .5f else 1f
        ))
        player.itemInMainHand = player.itemInMainHand.builder()
            .displayNameNonItalic(if (currentMana >= cost) spellCastAbComponent(playerClass, player.getTag(playerCloneClassTag)!!, index, cost) else notEnoughManaComponent)
            .build()

        if (currentMana < cost) return
        player.food = currentMana - cost
    }

    spell.schedule()
}

fun spellCost(player: Player, rawCost: Int, index: Int) =
    floor(
        ceil(rawCost * (1.0 - skillPercentage(player.getEffectiveSkill(2))/*.let { if (player.hasScoreboardTag("pvp")) it.coerceAtMost(0.55) else it }*/)
            + getId(player, Identification.valueOf("SpellCost${index}Raw"))) * (1.0 + getId(player, Identification.valueOf("SpellCost${index}Pct")) / 100.0)
            + player.getTag(extraSpellCostTag)!!
    ).coerceAtLeast(1.0).toInt()

private val extraSpellCostTag = Tag.Byte("player-extra-spell-cost").defaultValue(0)
private val lastCastSpellTag = Tag.Byte("player-last-cast-spell").defaultValue(0)

private fun resetClickSequence(player: Player) {
    player.setTag(clickSequenceTag, 0)
}

fun initClickListeners() {
    playerSpellClickListenersNode.listen(::onPlayerHandAnimation)
    playerSpellClickListenersNode.listen(::onPlayerUseItem)

    wynnLabPlayerListenersNode.addChild(playerSpellClickListenersNode)
}