package com.wynnlab.minestom.items

import net.minestom.server.item.Material

enum class ItemType(
    val group: ItemGroup,
    val baseMaterial: Material,
    val baseItemDamage: Int = 0
) {
    Wand(ItemGroup.Weapon, Material.STICK),
    Bow(ItemGroup.Weapon, Material.BOW),
    Dagger(ItemGroup.Weapon, Material.SHEARS),
    Spear(ItemGroup.Weapon, Material.IRON_SHOVEL),
    Relik(ItemGroup.Weapon, Material.STONE_SHOVEL),
    Helmet(ItemGroup.Helmet, Material.LEATHER_HELMET),
    Chestplate(ItemGroup.Armor, Material.LEATHER_CHESTPLATE),
    Leggings(ItemGroup.Armor, Material.LEATHER_LEGGINGS),
    Boots(ItemGroup.Armor, Material.LEATHER_BOOTS),
    Ring(ItemGroup.Accessoire, Material.FLINT_AND_STEEL, 1),
    Bracelet(ItemGroup.Accessoire, Material.FLINT_AND_STEEL, 18),
    Necklace(ItemGroup.Accessoire, Material.FLINT_AND_STEEL, 35)
}

sealed class ItemGroup {
    object Weapon : ItemGroup()

    sealed class GenericArmor : ItemGroup()

    object Armor : GenericArmor()

    object Helmet : GenericArmor()

    object Accessoire : ItemGroup()
}