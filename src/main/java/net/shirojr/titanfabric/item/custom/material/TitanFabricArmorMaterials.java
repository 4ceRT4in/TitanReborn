package net.shirojr.titanfabric.item.custom.material;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;
import net.shirojr.titanfabric.item.TitanFabricItems;

import java.util.function.Supplier;

public enum TitanFabricArmorMaterials implements ArmorMaterial {
    CITRIN("citrin", 15, new int[] { 2, 6, 7, 2 }, 9,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0f, 0.0f,
            () -> Ingredient.ofItems(TitanFabricItems.CITRIN_SHARD)),
    NETHER("nether", 30, new int[] { 3, 6, 8, 3 }, 10,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0f, 0.0f,
            () -> Ingredient.ofItems(TitanFabricItems.EMBER_INGOT)),
    LEGEND("legend", -1, new int[] { 3, 6, 8, 3 }, 25,
            SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 3.0f, 0.0f,
            () -> Ingredient.ofItems(TitanFabricItems.LEGEND_INGOT));

    private static final int[] BASE_DURABILITY;
    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Lazy<Ingredient> repairIngredientSupplier;

    private TitanFabricArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts,
                                      int enchantability, SoundEvent equipSound, float toughness,
                                      float knockbackResistance, Supplier<Ingredient> repairIngredientSupplier) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredientSupplier = new Lazy<Ingredient>(repairIngredientSupplier);
    }

    @Override
    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return this.protectionAmounts[slot.getEntitySlotId()];
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredientSupplier.get();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    static {
        BASE_DURABILITY = new int[]{13, 15, 16, 11};
    }
}
