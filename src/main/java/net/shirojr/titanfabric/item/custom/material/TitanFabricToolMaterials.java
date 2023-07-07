package net.shirojr.titanfabric.item.custom.material;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;
import net.shirojr.titanfabric.item.TitanFabricItems;

import java.util.function.Supplier;

public enum TitanFabricToolMaterials implements ToolMaterial {
    CITRIN(250, 3.0f, 0.0f, 2, 14,
            () -> Ingredient.ofItems(TitanFabricItems.CITRIN_INGOT)),
    NETHER(375, 3.0f, 0.0f, 2, 14,
            () -> Ingredient.ofItems(TitanFabricItems.NETHER_INGOT)),
    LEGEND(-1, 4.0f, 0.0f, 3, 22,
            () -> Ingredient.ofItems(TitanFabricItems.LEGEND_INGOT)),

    CITRIN_GREAT(1000, 3.0f, 0.0f, 2, 14,
                         () -> Ingredient.ofItems(TitanFabricItems.CITRIN_INGOT)),
    NETHER_GREAT(1500, 3.0f, 0.0f, 2, 14,
                         () -> Ingredient.ofItems(TitanFabricItems.NETHER_INGOT)),
    DIAMOND_GREAT(2000, 4.0f, 0.0f, 3, 14,
            () -> Ingredient.ofItems(Items.DIAMOND)),
    NETHERITE_GREAT(2500, 5.0f, 0.0f, 4, 14,
            () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),
    LEGEND_GREAT(-1, 5.0f, 0.0f, 4, 22,
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
