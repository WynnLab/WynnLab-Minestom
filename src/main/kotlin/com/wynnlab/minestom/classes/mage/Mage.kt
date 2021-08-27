package com.wynnlab.minestom.classes.mage

import com.wynnlab.minestom.base.BaseClass
import com.wynnlab.minestom.base.BasePlayerSpell
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

object Mage : BaseClass("MAGE", ItemStack.of(Material.STICK), MetaStats(2, 3, 3, 5)) {
    override val spells: List<(Player) -> BasePlayerSpell> = listOf(
        ::MageMain
    )
}