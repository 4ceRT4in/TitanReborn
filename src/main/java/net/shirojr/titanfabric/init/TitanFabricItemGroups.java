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
import net.shirojr.titanfabric.item.custom.spear.TitanFabricSpearItem;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.SwordType;
import net.shirojr.titanfabric.util.VariationHolder;

import java.util.List;

public class TitanFabricItemGroups {
    public static final RegistryKey<ItemGroup> TITAN = register("titan",
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(TitanFabricBlocks.LEGEND_CRYSTAL))
                    .displayName(Text.translatable("itemGroup.titanfabric.titan"))
                    .build());
    public static final RegistryKey<ItemGroup> SWORD_VARIANTS = register("sword_variants",
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(TitanFabricItems.LEGEND_SWORD))
                    .displayName(Text.translatable("itemGroup.titanfabric.sword_variants"))
                    .build());
    public static final RegistryKey<ItemGroup> GREATSWORD_VARIANTS = register("greatsword_variants",
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(TitanFabricItems.LEGEND_GREATSWORD))
                    .displayName(Text.translatable("itemGroup.titanfabric.greatsword_variants"))
                    .build());
    public static final RegistryKey<ItemGroup> SPEAR_VARIANTS = register("spear_variants",
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(TitanFabricItems.LEGEND_SPEAR))
                    .displayName(Text.translatable("itemGroup.titanfabric.spear_variants"))
                    .build());

    static {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries ->
                addPotionFamilies(entries,
                        List.of(TitanFabricPotions.STRONG_FIRE_RESISTANCE),
                        List.of(TitanFabricPotions.STRONG_WEAKNESS))
        );

        ItemGroupEvents.modifyEntriesEvent(TITAN).register(entries -> {
            // ores/materials
            addRaw(entries,
                    TitanFabricBlocks.CITRIN_ORE, TitanFabricItems.CITRIN_SHARD, TitanFabricBlocks.CITRIN_BLOCK,
                    TitanFabricBlocks.EMBER_ORE, TitanFabricItems.EMBER_SHARD, TitanFabricItems.EMBER_INGOT, TitanFabricBlocks.EMBER_BLOCK,
                    TitanFabricBlocks.DEEPSLATE_LEGEND_ORE, TitanFabricBlocks.LEGEND_CRYSTAL, TitanFabricItems.LEGEND_POWDER, TitanFabricItems.LEGEND_INGOT, TitanFabricBlocks.LEGEND_BLOCK,
                    TitanFabricItems.SWORD_HANDLE
            );

            // swords
            addRaw(entries,
                    TitanFabricItems.CITRIN_SWORD, TitanFabricItems.CITRIN_GREATSWORD,
                    TitanFabricItems.EMBER_SWORD, TitanFabricItems.EMBER_GREATSWORD,
                    TitanFabricItems.DIAMOND_SWORD, TitanFabricItems.DIAMOND_GREATSWORD,
                    TitanFabricItems.LEGEND_SWORD, TitanFabricItems.LEGEND_GREATSWORD,
                    TitanFabricItems.NETHERITE_SWORD,
                    TitanFabricItems.NETHERITE_GREATSWORD
            );

            // spears
            addRaw(entries,
                    TitanFabricItems.SPEAR_POLE,
                    TitanFabricItems.CITRIN_SPEAR, TitanFabricItems.EMBER_SPEAR,
                    TitanFabricItems.DIAMOND_SPEAR, TitanFabricItems.LEGEND_SPEAR,
                    TitanFabricItems.NETHERITE_SPEAR
            );

            // essences
            addVar(entries, TitanFabricItems.ESSENCE);

            // bows
            addRaw(entries,
                    TitanFabricItems.MULTI_BOW_1, TitanFabricItems.MULTI_BOW_2, TitanFabricItems.MULTI_BOW_3,
                    TitanFabricItems.LEGEND_BOW, TitanFabricItems.TITAN_CROSSBOW
            );

            // arrows
            addVar(entries, TitanFabricItems.EFFECT_ARROW);

            // shields
            addRaw(entries,
                    TitanFabricItems.DIAMOND_SHIELD, TitanFabricItems.LEGEND_SHIELD, TitanFabricItems.NETHERITE_SHIELD
            );

            // armors
            addRaw(entries,
                    TitanFabricItems.CITRIN_HELMET, TitanFabricItems.CITRIN_CHESTPLATE, TitanFabricItems.CITRIN_LEGGINGS, TitanFabricItems.CITRIN_BOOTS,
                    TitanFabricItems.EMBER_HELMET, TitanFabricItems.EMBER_CHESTPLATE, TitanFabricItems.EMBER_LEGGINGS, TitanFabricItems.EMBER_BOOTS,
                    TitanFabricItems.LEGEND_HELMET, TitanFabricItems.LEGEND_CHESTPLATE, TitanFabricItems.LEGEND_LEGGINGS, TitanFabricItems.LEGEND_BOOTS
            );

            // platings
            addRaw(entries,
                    TitanFabricItems.CITRIN_ARMOR_PLATING, TitanFabricItems.EMBER_ARMOR_PLATING, TitanFabricItems.DIAMOND_ARMOR_PLATING, TitanFabricItems.LEGEND_ARMOR_PLATING, TitanFabricItems.NETHERITE_ARMOR_PLATING
            );

            // backpacks
            addRaw(entries,
                    TitanFabricItems.BACKPACK_SMALL, TitanFabricItems.BACKPACK_MEDIUM, TitanFabricItems.BACKPACK_BIG, TitanFabricItems.POTION_BUNDLE
            );

            // misc items
            addRaw(entries,
                    TitanFabricItems.FLINT_AND_EMBER,
                    TitanFabricItems.PARACHUTE,
                    TitanFabricItems.CITRIN_STAR,
                    TitanFabricItems.DIAMOND_APPLE,
                    TitanFabricItems.ENCHANTED_DIAMOND_APPLE
            );

            // enchantment books
            entries.getContext().lookup().getOptionalWrapper(RegistryKeys.ENCHANTMENT).ifPresent(wrapper -> {
                addEnchBook(entries, wrapper, Enchantments.POWER, 6);
                addEnchBook(entries, wrapper, Enchantments.PROTECTION, 5);
                addEnchBook(entries, wrapper, Enchantments.SHARPNESS, 6);
            });

            // potions
            addPotionFamilies(entries,
                    List.of(TitanFabricPotions.INDESTRUCTIBLE_POTION, TitanFabricPotions.LONG_INDESTRUCTIBLE_POTION),
                    List.of(TitanFabricPotions.FROSTBURN_POTION, TitanFabricPotions.LONG_FROSTBURN_POTION, TitanFabricPotions.STRONG_FROSTBURN_POTION),
                    List.of(TitanFabricPotions.IMMUNITY_POTION, TitanFabricPotions.LONG_IMMUNITY_POTION),
                    List.of(TitanFabricPotions.RECOVERY_POTION, TitanFabricPotions.LONG_RECOVERY_POTION, TitanFabricPotions.STRONG_RECOVERY_POTION)
            );

            // blocks
            addRaw(entries,
                    TitanFabricBlocks.DIAMOND_FURNACE, TitanFabricBlocks.NETHERITE_ANVIL,
                    TitanFabricBlocks.CITRIN_BARREL_BOMB, TitanFabricBlocks.EMBER_BARREL_BOMB
            );
        });

        ItemGroupEvents.modifyEntriesEvent(SWORD_VARIANTS).register(entries ->
                addEffectSwords(entries, SwordType.DEFAULT));
        ItemGroupEvents.modifyEntriesEvent(GREATSWORD_VARIANTS).register(entries ->
                addEffectSwords(entries, SwordType.GREAT_SWORD));
        ItemGroupEvents.modifyEntriesEvent(SPEAR_VARIANTS).register(TitanFabricItemGroups::addEffectSpears);
    }

    private static void addRaw(FabricItemGroupEntries entries, ItemConvertible... items) {
        for (ItemConvertible item : items) entries.add(item.asItem().getDefaultStack());
    }

    private static void addVar(FabricItemGroupEntries entries, ItemConvertible item) {
        if (item instanceof VariationHolder holder) {
            for (ItemStack st : holder.getVariations()) entries.add(st);
        } else {
            entries.add(new ItemStack(item));
        }
    }

    private static void addEffectSwords(FabricItemGroupEntries entries, SwordType type) {
        for (SwordItem s : TitanFabricItems.EFFECT_SWORDS) {
            if (s instanceof TitanFabricSwordItem tf && tf.getSwordType() == type) {
                for (ItemStack stack : EffectHelper.generateWeaponEffectStacks(s, false)) entries.add(stack);
            }
        }
    }

    private static void addEnchBook(FabricItemGroupEntries entries, RegistryWrapper<Enchantment> wrapper, RegistryKey<Enchantment> key, int level) {
        wrapper.getOptional(key).ifPresent(entry -> {
            ItemStack stack = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(entry, level));
            entries.add(stack);
        });
    }

    private static void addEffectSpears(FabricItemGroupEntries entries) {
        for (TitanFabricSpearItem spear : TitanFabricItems.EFFECT_SPEARS) {
            for (ItemStack stack : EffectHelper.generateWeaponEffectStacks(spear, false)) entries.add(stack);
        }
    }

    @SafeVarargs
    private static void addPotionFamilies(FabricItemGroupEntries entries, List<RegistryEntry<Potion>>... potionFamilies) {
        addPotionType(entries, Items.POTION, potionFamilies);
        addPotionType(entries, Items.SPLASH_POTION, potionFamilies);
        addPotionType(entries, Items.LINGERING_POTION, potionFamilies);
    }

    @SafeVarargs
    private static void addPotionType(FabricItemGroupEntries entries, Item potionItem, List<RegistryEntry<Potion>>... potionFamilies) {
        for (List<RegistryEntry<Potion>> potionFamily : potionFamilies) {
            for (RegistryEntry<Potion> potion : potionFamily) {
                entries.add(PotionContentsComponent.createStack(potionItem, potion));
            }
        }
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
