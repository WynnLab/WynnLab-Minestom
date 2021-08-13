package com.wynnlab.minestom.items

import com.wynnlab.minestom.items.ItemDesign.*
import net.minestom.server.item.Material
import net.minestom.server.item.Material.WOODEN_SHOVEL
import net.minestom.server.item.Material.BOW
import net.minestom.server.item.Material.SHEARS
import net.minestom.server.item.Material.IRON_SHOVEL
import net.minestom.server.item.Material.STONE_SHOVEL
import net.minestom.server.item.Material.FLINT_AND_STEEL as FNS

enum class ItemType(
    val group: ItemGroup,
    designs: Map<ItemDesign, Design> = mapOf(),
) { // ETFAWM
    Wand(ItemGroup.Weapon, mapOf(Basic to Design(Material.STICK), Special to Design(WOODEN_SHOVEL, 1),
        Earth1 to Design(WOODEN_SHOVEL, 2), Earth2 to Design(WOODEN_SHOVEL, 3), Earth3 to Design(WOODEN_SHOVEL, 4),
        Thunder1 to Design(WOODEN_SHOVEL, 5), Thunder2 to Design(WOODEN_SHOVEL, 6), Thunder3 to Design(WOODEN_SHOVEL, 7),
        Fire1 to Design(WOODEN_SHOVEL, 8), Fire2 to Design(WOODEN_SHOVEL, 9), Fire3 to Design(WOODEN_SHOVEL, 10),
        Air1 to Design(WOODEN_SHOVEL, 11), Air2 to Design(WOODEN_SHOVEL, 12), Air3 to Design(WOODEN_SHOVEL, 13),
        Water1 to Design(WOODEN_SHOVEL, 14), Water2 to Design(WOODEN_SHOVEL, 15), Water3 to Design(WOODEN_SHOVEL, 16),
        Rainbow1 to Design(WOODEN_SHOVEL, 17), Rainbow2 to Design(WOODEN_SHOVEL, 18), Rainbow3 to Design(WOODEN_SHOVEL, 19)
    )),
    Bow(ItemGroup.Weapon, mapOf(Basic to Design(BOW), Special to Design(BOW, 1),
        Earth1 to Design(BOW, 2), Earth2 to Design(BOW, 3), Earth3 to Design(BOW, 4),
        Thunder1 to Design(BOW, 5), Thunder2 to Design(BOW, 6), Thunder3 to Design(BOW, 7),
        Fire1 to Design(BOW, 8), Fire2 to Design(BOW, 9), Fire3 to Design(BOW, 10),
        Air1 to Design(BOW, 11), Air2 to Design(BOW, 12), Air3 to Design(BOW, 13),
        Water1 to Design(BOW, 14), Water2 to Design(BOW, 15), Water3 to Design(BOW, 16),
        Rainbow1 to Design(BOW, 17), Rainbow2 to Design(BOW, 18), Rainbow3 to Design(BOW, 19)
    )),
    Dagger(ItemGroup.Weapon, mapOf(Basic to Design(SHEARS), Special to Design(SHEARS, 1),
        Earth1 to Design(SHEARS, 2), Earth2 to Design(SHEARS, 3), Earth3 to Design(SHEARS, 4),
        Thunder1 to Design(SHEARS, 5), Thunder2 to Design(SHEARS, 6), Thunder3 to Design(SHEARS, 7),
        Fire1 to Design(SHEARS, 8), Fire2 to Design(SHEARS, 9), Fire3 to Design(SHEARS, 10),
        Air1 to Design(SHEARS, 11), Air2 to Design(SHEARS, 12), Air3 to Design(SHEARS, 13),
        Water1 to Design(SHEARS, 14), Water2 to Design(SHEARS, 15), Water3 to Design(SHEARS, 16),
        Rainbow1 to Design(SHEARS, 17), Rainbow2 to Design(SHEARS, 18), Rainbow3 to Design(SHEARS, 19)
    )),
    Spear(ItemGroup.Weapon, mapOf(Basic to Design(IRON_SHOVEL), Special to Design(IRON_SHOVEL, 1),
        Earth1 to Design(IRON_SHOVEL, 2), Earth2 to Design(IRON_SHOVEL, 3), Earth3 to Design(IRON_SHOVEL, 4),
        Thunder1 to Design(IRON_SHOVEL, 5), Thunder2 to Design(IRON_SHOVEL, 6), Thunder3 to Design(IRON_SHOVEL, 7),
        Fire1 to Design(IRON_SHOVEL, 8), Fire2 to Design(IRON_SHOVEL, 9), Fire3 to Design(IRON_SHOVEL, 10),
        Air1 to Design(IRON_SHOVEL, 11), Air2 to Design(IRON_SHOVEL, 12), Air3 to Design(IRON_SHOVEL, 13),
        Water1 to Design(IRON_SHOVEL, 14), Water2 to Design(IRON_SHOVEL, 15), Water3 to Design(IRON_SHOVEL, 16),
        Rainbow1 to Design(IRON_SHOVEL, 17), Rainbow2 to Design(IRON_SHOVEL, 18), Rainbow3 to Design(IRON_SHOVEL, 19)
    )),
    Relik(ItemGroup.Weapon, mapOf(Basic to Design(STONE_SHOVEL, 7), Special to Design(STONE_SHOVEL, 1),
        Earth1 to Design(STONE_SHOVEL, 2), Earth2 to Design(STONE_SHOVEL, 3), Earth3 to Design(STONE_SHOVEL, 4),
        Thunder1 to Design(STONE_SHOVEL, 5), Thunder2 to Design(STONE_SHOVEL, 6), Thunder3 to Design(STONE_SHOVEL, 7),
        Fire1 to Design(STONE_SHOVEL, 8), Fire2 to Design(STONE_SHOVEL, 9), Fire3 to Design(STONE_SHOVEL, 10),
        Air1 to Design(STONE_SHOVEL, 11), Air2 to Design(STONE_SHOVEL, 12), Air3 to Design(STONE_SHOVEL, 13),
        Water1 to Design(STONE_SHOVEL, 14), Water2 to Design(STONE_SHOVEL, 15), Water3 to Design(STONE_SHOVEL, 16),
        Rainbow1 to Design(STONE_SHOVEL, 17), Rainbow2 to Design(STONE_SHOVEL, 18), Rainbow3 to Design(STONE_SHOVEL, 19)
    )),
    Helmet(ItemGroup.Helmet, mapOf(Basic to Design(Material.LEATHER_HELMET), Leather to Design(Material.LEATHER_HELMET), Gold to Design(Material.GOLDEN_HELMET),
        Chain to Design(Material.CHAINMAIL_HELMET), Iron to Design(Material.IRON_HELMET), Diamond to Design(Material.DIAMOND_HELMET), Netherite to Design(Material.NETHERITE_HELMET),
        Turtle to Design(Material.TURTLE_HELMET)
    )),
    Chestplate(ItemGroup.Armor, mapOf(Basic to Design(Material.LEATHER_CHESTPLATE), Leather to Design(Material.LEATHER_CHESTPLATE), Gold to Design(Material.GOLDEN_CHESTPLATE),
        Chain to Design(Material.CHAINMAIL_CHESTPLATE), Iron to Design(Material.IRON_CHESTPLATE), Diamond to Design(Material.DIAMOND_CHESTPLATE), Netherite to Design(Material.NETHERITE_CHESTPLATE)
    )),
    Leggings(ItemGroup.Armor, mapOf(Basic to Design(Material.LEATHER_LEGGINGS), Leather to Design(Material.LEATHER_LEGGINGS), Gold to Design(Material.GOLDEN_LEGGINGS),
        Chain to Design(Material.CHAINMAIL_LEGGINGS), Iron to Design(Material.IRON_LEGGINGS), Diamond to Design(Material.DIAMOND_LEGGINGS), Netherite to Design(Material.NETHERITE_LEGGINGS)
    )),
    Boots(ItemGroup.Armor, mapOf(Basic to Design(Material.LEATHER_BOOTS), Leather to Design(Material.LEATHER_BOOTS), Gold to Design(Material.GOLDEN_BOOTS),
        Chain to Design(Material.CHAINMAIL_BOOTS), Iron to Design(Material.IRON_BOOTS), Diamond to Design(Material.DIAMOND_BOOTS), Netherite to Design(Material.NETHERITE_BOOTS)
    )),
    Ring(ItemGroup.Accessoire, mapOf(Basic to Design(FNS, 1), Special to Design(FNS, 2),
        Earth1 to Design(FNS, 3), Earth2 to Design(FNS, 4),
        Thunder1 to Design(FNS, 5), Thunder2 to Design(FNS, 6),
        Fire1 to Design(FNS, 7), Fire2 to Design(FNS, 8),
        Air1 to Design(FNS, 9), Air2 to Design(FNS, 10),
        Water1 to Design(FNS, 11), Water2 to Design(FNS, 12),
        Rainbow1 to Design(FNS, 13), Rainbow2 to Design(FNS, 14),
        Wedding to Design(FNS, 15), Pearl to Design(FNS, 16), Thick to Design(FNS, 17)
    )),
    Necklace(ItemGroup.Accessoire, mapOf(Basic to Design(FNS, 18), Special to Design(FNS, 19),
        Earth1 to Design(FNS, 20), Earth2 to Design(FNS, 21),
        Thunder1 to Design(FNS, 22), Thunder2 to Design(FNS, 23),
        Fire1 to Design(FNS, 24), Fire2 to Design(FNS, 25),
        Air1 to Design(FNS, 26), Air2 to Design(FNS, 27),
        Water1 to Design(FNS, 28), Water2 to Design(FNS, 29),
        Rainbow1 to Design(FNS, 30), Rainbow2 to Design(FNS, 31),
        Cross to Design(FNS, 32), Thick to Design(FNS, 33), Pearl to Design(FNS, 34)
    )),
    Bracelet(ItemGroup.Accessoire, mapOf(Basic to Design(FNS, 35), Special to Design(FNS, 36),
        Earth1 to Design(FNS, 37), Earth2 to Design(FNS, 39),
        Thunder1 to Design(FNS, 41), Thunder2 to Design(FNS, 42),
        Fire1 to Design(FNS, 43), Fire2 to Design(FNS, 44),
        Air1 to Design(FNS, 45), Air2 to Design(FNS, 46),
        Water1 to Design(FNS, 47), Water2 to Design(FNS, 48),
        Rainbow1 to Design(FNS, 49), Rainbow2 to Design(FNS, 50)
    ));

    val designs = designs.mapKeys { (k, _) -> k.name }
}

sealed class ItemGroup {
    object Weapon : ItemGroup()

    sealed class GenericArmor : ItemGroup()

    object Armor : GenericArmor()

    object Helmet : GenericArmor()

    object Accessoire : ItemGroup()
}

data class Design(val material: Material, val damage: Int = 0)