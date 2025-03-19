package dev.corgitaco.blockswap.itempredicate;

import net.minecraft.world.item.ItemStack;

public interface ItemPredicate {



    boolean test(ItemStack stack);
}
