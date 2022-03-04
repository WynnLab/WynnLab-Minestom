package com.wynnlab.minestom.classes

import com.wynnlab.minestom.base.BaseClass
import com.wynnlab.minestom.classes.mage.Mage

val classes: List<BaseClass> = listOf(
    Mage
)

private val classesById = classes.associateBy { it.id }

fun getClassById(id: String) = classesById[id]