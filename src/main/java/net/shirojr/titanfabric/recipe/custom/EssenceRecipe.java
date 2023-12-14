package net.shirojr.titanfabric.recipe.custom;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.recipe.TitanFabricRecipes;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.recipes.SlotArrangementType;

public class EssenceRecipe extends SpecialCraftingRecipe {
    private final Ingredient effectModifier;
    private final Ingredient base;

    private SlotArrangementType slotArrangementType;

    public EssenceRecipe(Identifier id, Ingredient effectModifier, Ingredient base) {
        super(id);
        this.effectModifier = effectModifier;
        this.base = base;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        LoggerUtil.devLogger("testing for recipe");
        int width = inventory.getWidth(), height = inventory.getHeight();
        if (width != 3 || height != 3) return false;
        for (SlotArrangementType slotArrangementType : SlotArrangementType.values()) {
            if (!slotArrangementType.slotsHaveMatchingItems(inventory)) return false;
            this.slotArrangementType = slotArrangementType;
        }

        if (!effectModifier.test(inventory.getStack(1))) return false;
        return (base.test(inventory.getStack(2)));
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        SlotArrangementType.ESSENCE.getEffect(inventory);
        return new ItemStack(Items.SEA_PICKLE);
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Items.ACACIA_BOAT);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipes.WEAPON_EFFECT_RECIPE_SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<EssenceRecipe> {
        @Override
        public EssenceRecipe read(Identifier id, JsonObject json) {
            Ingredient effectModifier = Ingredient.fromJson(JsonHelper.getObject(json, "effect_modifier"));
            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(json, "base"));
            return new EssenceRecipe(id, effectModifier, base);
        }

        @Override
        public EssenceRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient effectModifier = Ingredient.fromPacket(buf);
            Ingredient base = Ingredient.fromPacket(buf);
            return new EssenceRecipe(id, effectModifier, base);
        }

        @Override
        public void write(PacketByteBuf buf, EssenceRecipe recipe) {
            recipe.effectModifier.write(buf);
            recipe.base.write(buf);
        }
    }
}
