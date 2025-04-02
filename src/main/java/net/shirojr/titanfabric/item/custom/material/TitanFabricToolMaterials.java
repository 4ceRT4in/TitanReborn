package net.shirojr.titanfabric.item.custom.material;

import com.google.common.base.Suppliers;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.shirojr.titanfabric.init.TitanFabricItems;

import java.util.function.Supplier;

public enum TitanFabricToolMaterials implements ToolMaterial {

    WOOD(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 59, 1.0f, 0.0f, 15,
            Ingredient.fromTag(ItemTags.PLANKS)),
    GOLD(BlockTags.INCORRECT_FOR_GOLD_TOOL, 32, 6.0f, 0.0f, 22,
            Ingredient.ofItems(Items.GOLD_INGOT)),
    STONE(BlockTags.INCORRECT_FOR_STONE_TOOL, 131, 2.0f, 0.0f, 5,
            Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS)),
    IRON(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 3.0f, 0.0f, 14,
            Ingredient.ofItems(Items.IRON_INGOT)),
    DIAMOND(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 4.0f, 0.0f, 10,
            Ingredient.ofItems(Items.DIAMOND)),
    NETHERITE(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 5.0f, 0.0f, 15,
            Ingredient.ofItems(Items.NETHERITE_INGOT)),

    CITRIN(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 3.0f, 0.0f, 14,
            Ingredient.ofItems(TitanFabricItems.CITRIN_SHARD)),
    EMBER(BlockTags.INCORRECT_FOR_IRON_TOOL, 375, 3.0f, 0.0f, 18,
            Ingredient.ofItems(TitanFabricItems.EMBER_INGOT)),
    LEGEND(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, -1, 4.0f, 0.0f, 22,
            Ingredient.ofItems(TitanFabricItems.LEGEND_INGOT)),

    CITRIN_GREAT(BlockTags.INCORRECT_FOR_IRON_TOOL, 1000, 3.0f, 0.0f, 10,
            Ingredient.ofItems(TitanFabricItems.CITRIN_SHARD)),
    EMBER_GREAT(BlockTags.INCORRECT_FOR_IRON_TOOL, 1500, 3.0f, 0.0f, 14,
            Ingredient.ofItems(TitanFabricItems.EMBER_INGOT)),
    DIAMOND_GREAT(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 2000, 4.0f, 0.0f, 10,
            Ingredient.ofItems(Items.DIAMOND)),
    NETHERITE_GREAT(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2500, 5.0f, 0.0f, 15,
            Ingredient.ofItems(Items.NETHERITE_INGOT)),
    LEGEND_GREAT(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, -1, 5.0f, 0.0f, 18,
            Ingredient.ofItems(TitanFabricItems.LEGEND_INGOT));

    private final TagKey<Block> inverseTag;
    private final int durability;
    private final float miningSpeedMultiplier;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredientSupplier;

    TitanFabricToolMaterials(TagKey<Block> inverseTag, int durability, float miningSpeedMultiplier, float attackDamage,
                             int enchantability, Ingredient repairIngredient) {
        this.inverseTag = inverseTag;
        this.durability = durability;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredientSupplier = Suppliers.memoize(() -> repairIngredient);
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
    public TagKey<Block> getInverseTag() {
        return this.inverseTag;
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
