package dev.corgitaco.blockswap.itempredicate;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ItemIsPredicate(Either<ResourceKey<Item>, TagKey<Item>> check) implements ItemPredicate {
    @Override
    public boolean test(ItemStack stack) {
        Optional<ResourceKey<Item>> left = check.left();
        if (left.isPresent()) {
           return stack.is(itemHolder -> itemHolder.unwrapKey().orElseThrow() == left.get());
        }

        Optional<TagKey<Item>> right = check.right();
        if (right.isPresent()) {
            return stack.is(right.orElseThrow());
        }

        return false;
    }
}
