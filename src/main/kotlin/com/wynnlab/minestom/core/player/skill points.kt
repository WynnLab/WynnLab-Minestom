package com.wynnlab.minestom.core.player

import com.wynnlab.minestom.items.Identification
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import kotlin.math.roundToInt

fun skillPercentage(skill: Int): Float = when {
    skill >= 150 -> .808f
    skill <= 0 -> 0f
    else -> (10f * (-0.0000000166f * skill * skill * skill * skill + 0.0000122614f * skill * skill * skill - 0.0044972984f * skill * skill + 0.9931907398f * skill + 0.0093811967f)).roundToInt() / 1000f
}

private val assignedSkillPointsTag = Tag.ByteArray("assigned-skill-points").defaultValue(ByteArray(5))

val Player.assignedSkills get() = getTag(assignedSkillPointsTag)!!

val Player.remainingSkillAssigns get() = 200 - getTag(assignedSkillPointsTag)!!.sum()

val Player.modifiedSkills: IntArray get() {
    val a = assignedSkills
    val r = IntArray(5) { a[it].toInt() }
    for (e in defenseEquipment) {
        if (e == null) continue
        addSkills(r, e)
    }
    return r
}

fun addSkills(r: IntArray, e: ItemStack) {
    r[0] += Identification.Strength.get(e).toInt()
    r[1] += Identification.Dexterity.get(e).toInt()
    r[2] += Identification.Intelligence.get(e).toInt()
    r[3] += Identification.Defense.get(e).toInt()
    r[4] += Identification.Agility.get(e).toInt()
}

val Player.effectiveSkills: IntArray get() {
    val a = assignedSkills
    val r = IntArray(5) { a[it].toInt() }
    for (e in equipment) {
        if (e == null) continue
        r[0] += Identification.Strength.get(e).toInt()
        r[1] += Identification.Dexterity.get(e).toInt()
        r[2] += Identification.Intelligence.get(e).toInt()
        r[3] += Identification.Defense.get(e).toInt()
        r[4] += Identification.Agility.get(e).toInt()
    }
    return r
}

fun Player.getEffectiveSkill(index: Int): Int {
    var r = assignedSkills[index].toInt()
    for (e in equipment) {
        if (e == null) continue
        r += when (index) {
            0 -> Identification.Strength
            1 -> Identification.Dexterity
            2 -> Identification.Intelligence
            3 -> Identification.Defense
            4 -> Identification.Agility
            else -> throw IllegalArgumentException()
        }.get(e)
    }
    return r
}

fun Player.upgradeSkill(index: Int, add: Int) = setTag(assignedSkillPointsTag, assignedSkills.also {
    it[index] = (it[index] + add).toByte()
})

fun Player.resetSkillAssigns() = setTag(assignedSkillPointsTag, ByteArray(5))