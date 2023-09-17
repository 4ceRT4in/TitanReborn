package net.shirojr.titanfabric.item.custom.material;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Lazy;
import net.shirojr.titanfabric.item.TitanFabricItems;

import java.util.function.Supplier;

public enum TitanFabricToolMaterials implements ToolMaterial {

    WOOD(59, 1.0f, 0.0f, 1, 15,
            () -> Ingredient.fromTag(ItemTags.PLANKS)),
    GOLD(32, 6.0f, 0.0f, 6, 22,
            () -> Ingredient.ofItems(Items.GOLD_INGOT)),
    STONE(131, 2.0f, 0.0f, 2, 5,
            () -> Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS)),
    IRON(250, 3.0f, 0.0f, 3, 14,
            () -> Ingredient.ofItems(Items.IRON_INGOT)),
    DIAMOND(1561, 4.0f, 0.0f, 4, 10,
            () -> Ingredient.ofItems(Items.DIAMOND)),
    NETHERITE(2031, 5.0f, 0.0f, 5, 15,
            () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),

    CITRIN(250, 3.0f, 0.0f, 2, 14,
            () -> Ingredient.ofItems(TitanFabricItems.CITRIN_SHARD)),
    NETHER(375, 3.0f, 0.0f, 2, 18,
            () -> Ingredient.ofItems(TitanFabricItems.NETHER_INGOT)),
    LEGEND(-1, 4.0f, 0.0f, 3, 22,
            () -> Ingredient.ofItems(TitanFabricItems.LEGEND_INGOT)),

    CITRIN_GREAT(1000, 3.0f, 0.0f, 2, 10,
                         () -> Ingredient.ofItems(TitanFabricItems.CITRIN_SHARD)),
    NETHER_GREAT(1500, 3.0f, 0.0f, 2, 14,
                         () -> Ingredient.ofItems(TitanFabricItems.NETHER_INGOT)),
    DIAMOND_GREAT(2000, 4.0f, 0.0f, 3, 10,
            () -> Ingredient.ofItems(Items.DIAMOND)),
    NETHERITE_GREAT(2500, 5.0f, 0.0f, 4, 15,
            () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),
    LEGEND_GREAT(-1, 5.0f, 0.0f, 4, 18,
                         () -> Ingredient.ofItems(TitanFabricItems.LEGEND_INGOT));



    private final int durability;
    private final float miningSpeedMultiplier;
    private final float attackDamage;
    private final int miningLevel;
    private final int enchantability;
    private final Lazy<Ingredient> repairIngredientSupplier;

    TitanFabricToolMaterials(int durability, float miningSpeedMultiplier, float attackDamage,
                                     int miningLevel, int enchantability,
                                     Supplier<Ingredient> repairIngredientSupplier) {
        this.durability = durability;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.attackDamage = attackDamage;
        this.miningLevel = miningLevel;
        this.enchantability = enchantability;
        this.repairIngredientSupplier = new Lazy<Ingredient>(repairIngredientSupplier);
    }
    @Override
    public int getDurability() {
        return this.durability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeedMultiplier;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredientSupplier.get();
    }
}
