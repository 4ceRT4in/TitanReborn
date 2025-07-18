package net.shirojr.titanfabric.init;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.shirojr.titanfabric.TitanFabric;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface TitanFabricArmorMaterials {
    CustomArmorMaterial CITRIN = new CustomArmorMaterial(
            register(
                    "citrin",
                    Map.of(
                            ArmorItem.Type.HELMET, 2,
                            ArmorItem.Type.CHESTPLATE, 7,
                            ArmorItem.Type.LEGGINGS, 6,
                            ArmorItem.Type.BOOTS, 2
                    ),
                    SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                    9, 0.0f, 0.0f,
                    () -> Ingredient.ofItems(TitanFabricItems.CITRIN_SHARD)
            ),
            15
    );

    CustomArmorMaterial EMBER = new CustomArmorMaterial(
            register(
                    "ember",
                    Map.of(
                            ArmorItem.Type.HELMET, 3,
                            ArmorItem.Type.CHESTPLATE, 8,
                            ArmorItem.Type.LEGGINGS, 6,
                            ArmorItem.Type.BOOTS, 3
                    ),
                    SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                    10, 1.0f, 0.0f,
                    () -> Ingredient.ofItems(TitanFabricItems.EMBER_INGOT)
            ),
            30
    );

    CustomArmorMaterial LEGEND = new CustomArmorMaterial(
            register(
                    "legend",
                    Map.of(
                            ArmorItem.Type.HELMET, 3,
                            ArmorItem.Type.CHESTPLATE, 8,
                            ArmorItem.Type.LEGGINGS, 6,
                            ArmorItem.Type.BOOTS, 3
                    ),
                    SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
                    25, 3.0f, 0.0f,
                    () -> Ingredient.ofItems(TitanFabricItems.LEGEND_INGOT)
            ),
            -1
    );


    @SuppressWarnings("SameParameterValue")
    private static ArmorMaterial register(String name, Map<ArmorItem.Type, Integer> defense,
                                          RegistryEntry<SoundEvent> equipSound,
                                          int enchantability, float toughness, float knockbackResistance,
                                          Supplier<Ingredient> repairMaterial) {
        ArmorMaterial material = new ArmorMaterial(defense, enchantability, equipSound, repairMaterial,
                List.of(new ArmorMaterial.Layer(TitanFabric.getId(name))), toughness, knockbackResistance);
        return Registry.register(Registries.ARMOR_MATERIAL, TitanFabric.getId(name), material);
    }

    record CustomArmorMaterial(ArmorMaterial material, int durabilityMultiplier) {
        public RegistryEntry<ArmorMaterial> getRegistryEntry() {
            return Registries.ARMOR_MATERIAL.getEntry(material());
        }

        public int getDurability(ArmorItem.Type type) {
            return type.getMaxDamage(durabilityMultiplier());
        }
    }


    static void initialize() {
        // static initialisation
    }
}
