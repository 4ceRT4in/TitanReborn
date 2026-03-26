package net.shirojr.titanfabric.recipe.custom;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.init.TitanFabricRecipeSerializers;

public class EnchantedDiamondAppleRepairRecipe extends SpecialCraftingRecipe {
    public EnchantedDiamondAppleRepairRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        ItemStack apple = ItemStack.EMPTY;
        boolean foundDiamondBlock = false;
        for (int i = 0; i < input.getStacks().size(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            if (stack.isOf(TitanFabricItems.ENCHANTED_DIAMOND_APPLE)) {
                if (!apple.isEmpty() || !stack.isDamaged()) {
                    return false;
                }
                apple = stack;
                continue;
            }

            if (stack.isOf(Items.DIAMOND_BLOCK)) {
                if (foundDiamondBlock) {
                    return false;
                }
                foundDiamondBlock = true;
                continue;
            }

            return false;
        }

        return !apple.isEmpty() && foundDiamondBlock;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        for (int i = 0; i < input.getStacks().size(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (!stack.isOf(TitanFabricItems.ENCHANTED_DIAMOND_APPLE) || !stack.isDamaged()) continue;

            ItemStack repaired = stack.copy();
            repaired.setCount(1);
            repaired.setDamage(stack.getDamage() - 1);
            return repaired;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipeSerializers.ENCHANTED_DIAMOND_APPLE_REPAIR;
    }
}
