package net.shirojr.titanfabric.init;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.item.custom.sword.CitrinSwordItem;
import net.shirojr.titanfabric.item.custom.sword.DiamondSwordItem;
import net.shirojr.titanfabric.item.custom.sword.EmberSwordItem;
import net.shirojr.titanfabric.item.custom.sword.LegendSwordItem;
import net.shirojr.titanfabric.util.SwordType;
import net.shirojr.titanfabric.util.VariationHolder;

import java.util.function.Predicate;

public class TitanFabricItemGroups {
    public static final RegistryKey<ItemGroup> TITAN = register("titan",
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(TitanFabricBlocks.LEGEND_CRYSTAL))
                    .displayName(Text.translatable("itemGroup.titanfabric.titan"))
                    .build());

    static {
        ItemGroupEvents.modifyEntriesEvent(TITAN).register(entries -> {
            addRaw(entries,
                    TitanFabricBlocks.CITRIN_ORE, TitanFabricItems.CITRIN_SHARD, TitanFabricBlocks.CITRIN_BLOCK,
                    TitanFabricBlocks.EMBER_ORE, TitanFabricItems.EMBER_SHARD, TitanFabricItems.EMBER_INGOT, TitanFabricBlocks.EMBER_BLOCK,
                    TitanFabricBlocks.DEEPSLATE_LEGEND_ORE, TitanFabricBlocks.LEGEND_CRYSTAL, TitanFabricItems.LEGEND_POWDER, TitanFabricItems.LEGEND_INGOT, TitanFabricBlocks.LEGEND_BLOCK,
                    TitanFabricItems.CITRIN_HELMET, TitanFabricItems.CITRIN_CHESTPLATE, TitanFabricItems.CITRIN_LEGGINGS, TitanFabricItems.CITRIN_BOOTS,
                    TitanFabricItems.EMBER_HELMET, TitanFabricItems.EMBER_CHESTPLATE, TitanFabricItems.EMBER_LEGGINGS, TitanFabricItems.EMBER_BOOTS,
                    TitanFabricItems.LEGEND_HELMET, TitanFabricItems.LEGEND_CHESTPLATE, TitanFabricItems.LEGEND_LEGGINGS, TitanFabricItems.LEGEND_BOOTS,
                    TitanFabricItems.CITRIN_ARMOR_PLATING, TitanFabricItems.EMBER_ARMOR_PLATING, TitanFabricItems.DIAMOND_ARMOR_PLATING, TitanFabricItems.NETHERITE_ARMOR_PLATING, TitanFabricItems.LEGEND_ARMOR_PLATING,
                    TitanFabricItems.DIAMOND_SHIELD, TitanFabricItems.NETHERITE_SHIELD, TitanFabricItems.LEGEND_SHIELD,
                    TitanFabricItems.MULTI_BOW_1, TitanFabricItems.MULTI_BOW_2, TitanFabricItems.MULTI_BOW_3,
                    TitanFabricItems.LEGEND_BOW, TitanFabricItems.TITAN_CROSSBOW
            );

            addVar(entries, TitanFabricItems.EFFECT_ARROW);

            addRaw(entries,
                    TitanFabricItems.SWORD_HANDLE,
                    TitanFabricItems.CITRIN_SWORD, TitanFabricItems.CITRIN_GREATSWORD,
                    TitanFabricItems.EMBER_SWORD, TitanFabricItems.EMBER_GREATSWORD,
                    TitanFabricItems.DIAMOND_GREATSWORD,
                    TitanFabricItems.LEGEND_SWORD, TitanFabricItems.LEGEND_GREATSWORD,
                    TitanFabricItems.NETHERITE_GREATSWORD
            );

            addEffectSwords(entries, CitrinSwordItem.class, SwordType.DEFAULT);
            addEffectSwords(entries, CitrinSwordItem.class, SwordType.GREAT_SWORD);
            addEffectSwords(entries, EmberSwordItem.class, SwordType.DEFAULT);
            addEffectSwords(entries, EmberSwordItem.class, SwordType.GREAT_SWORD);
            addEffectSwords(entries, DiamondSwordItem.class, SwordType.DEFAULT);
            addEffectSwords(entries, DiamondSwordItem.class, SwordType.GREAT_SWORD);
            addEffectSwords(entries, LegendSwordItem.class, SwordType.DEFAULT);
            addEffectSwords(entries, LegendSwordItem.class, SwordType.GREAT_SWORD);

            addVar(entries, TitanFabricItems.ESSENCE);

            addRaw(entries,
                    TitanFabricItems.BACKPACK_SMALL, TitanFabricItems.BACKPACK_MEDIUM, TitanFabricItems.BACKPACK_BIG,
                    TitanFabricItems.POTION_BUNDLE,
                    TitanFabricItems.FLINT_AND_EMBER,
                    TitanFabricItems.PARACHUTE,
                    TitanFabricItems.CITRIN_STAR,
                    TitanFabricItems.DIAMOND_APPLE,
                    TitanFabricBlocks.DIAMOND_FURNACE,
                    TitanFabricBlocks.NETHERITE_ANVIL
            );

            entries.getContext().lookup().getOptionalWrapper(RegistryKeys.ENCHANTMENT).ifPresent(wrapper -> {
                addEnchBook(entries, wrapper, Enchantments.POWER, 6);
                addEnchBook(entries, wrapper, Enchantments.PROTECTION, 5);
                addEnchBook(entries, wrapper, Enchantments.SHARPNESS, 6);
            });
            addPotionFamily(entries, TitanFabricPotions.INDESTRUCTIBLE_POTION, TitanFabricPotions.LONG_INDESTRUCTIBLE_POTION);
            addPotionFamily(entries, TitanFabricPotions.FROSTBURN_POTION, TitanFabricPotions.STRONG_FROSTBURN_POTION);
            addPotionFamily(entries, TitanFabricPotions.IMMUNITY_POTION, TitanFabricPotions.LONG_IMMUNITY_POTION);
        });
    }

    private static void addRaw(FabricItemGroupEntries entries, ItemConvertible... items) {
        for (ItemConvertible item : items) entries.add(new ItemStack(item));
    }

    private static void addVar(FabricItemGroupEntries entries, ItemConvertible item) {
        if (item instanceof VariationHolder holder) {
            for (ItemStack st : holder.getVariations()) entries.add(st);
        } else {
            entries.add(new ItemStack(item));
        }
    }

    private static void addEffectSwords(FabricItemGroupEntries entries, Class<? extends SwordItem> cls, SwordType type) {
        addEffectSwords(entries, s -> cls.isInstance(s) && s instanceof TitanFabricSwordItem tf && tf.getSwordType() == type);
    }

    private static void addEffectSwords(FabricItemGroupEntries entries, Predicate<SwordItem> filter) {
        for (SwordItem s : TitanFabricItems.EFFECT_SWORDS) {
            if (filter.test(s)) addVar(entries, s);
        }
    }

    private static void addEnchBook(FabricItemGroupEntries entries, RegistryWrapper<Enchantment> wrapper, RegistryKey<Enchantment> key, int level) {
        wrapper.getOptional(key).ifPresent(entry -> {
            ItemStack stack = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(entry, level));
            entries.add(stack);
        });
    }

    private static void addPotionFamily(FabricItemGroupEntries entries, RegistryEntry<Potion> level1, RegistryEntry<Potion> level2) {
        entries.add(PotionContentsComponent.createStack(Items.POTION, level1));
        entries.add(PotionContentsComponent.createStack(Items.POTION, level2));
        entries.add(PotionContentsComponent.createStack(Items.SPLASH_POTION, level1));
        entries.add(PotionContentsComponent.createStack(Items.SPLASH_POTION, level2));
        entries.add(PotionContentsComponent.createStack(Items.LINGERING_POTION, level1));
        entries.add(PotionContentsComponent.createStack(Items.LINGERING_POTION, level2));
    }

    @SuppressWarnings("SameParameterValue")
    private static RegistryKey<ItemGroup> register(String name, ItemGroup group) {
        Registry.register(Registries.ITEM_GROUP, TitanFabric.getId(name), group);
        return RegistryKey.of(Registries.ITEM_GROUP.getKey(), TitanFabric.getId(name));
    }

    public static void initialize() {
        // static initialisation
    }
}