package net.shirojr.titanfabric.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.recipe.special.EmiAnvilRepairItemRecipe;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class NetheriteAnvilRepairItemRecipe extends EmiAnvilRepairItemRecipe {
    public NetheriteAnvilRepairItemRecipe(Item tool, Identifier id) {
        super(tool, id);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return TitanRebornEmiPlugin.NETHERITE_ANVIL_CATEGORY;
    }
}
