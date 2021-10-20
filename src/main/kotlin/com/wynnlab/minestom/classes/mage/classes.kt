package com.wynnlab.minestom.classes.mage

import com.wynnlab.minestom.base.BaseClass

val classes: List<BaseClass> = listOf(
    Mage
)

private val classesById = classes.associateBy { it.id }

fun getClassById(id: String) = classesById[id]