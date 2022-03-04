package com.wynnlab.minestom.core.player

import com.wynnlab.minestom.items.Identification
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import kotlin.math.roundToInt

fun skillPercentage(skill: Int): Float = when {
    skill >= 150 -> .808f
    skill <= 0 -> 0f
    else -> (10f * (-0.0000000166 * skill * skill * skill * skill + 0.0000122614 * skill * skill * skill - 0.0044972984 * skill * skill + 0.9931907398 * skill + 0.0093811967)).roundToInt() / 1000f
}

private val assignedSkillPointsTag = Tag.Long("assigned-skill-points").defaultValue(0L)

val Player.assignedSkills get() = getTag(assignedSkillPointsTag)!!

fun Player.assignedSkill(index: Int, skills: Long = assignedSkills) = ((skills and (0xff shl (8 * index)).toLong()) shr (8 * index)).toByte()

val Player.remainingSkillAssigns: Int get() {//get() = 200 - getTag(assignedSkillPointsTag)!!.sum()
    val asp = assignedSkills
    return ((asp and 0xff) +
            ((asp and (0xff shl 4)) shr 8) +
            ((asp and (0xff shl 8)) shr 16) +
            ((asp and (0xff shl 16)) shr 24) +
            ((asp and (0xff shl 24)) shr 32)).toInt()
}

val Player.modifiedSkills: IntArray get() {
    val a = assignedSkills
    val r = IntArray(5) { assignedSkill(it, a).toInt() }
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
    val r = IntArray(5) { assignedSkill(it, a).toInt() }
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
    var r = assignedSkill(index).toInt()
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
    //it[index] = (it[index] + add).toByte()
    it + (add shl (index * 8))
})

fun Player.resetSkillAssigns() = setTag(assignedSkillPointsTag, 0L)