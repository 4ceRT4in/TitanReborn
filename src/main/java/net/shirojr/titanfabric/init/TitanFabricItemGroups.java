package net.shirojr.titanfabric.init;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.util.VariationHolder;

public class TitanFabricItemGroups {
    public static final RegistryKey<ItemGroup> TITAN = register("titan",
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(TitanFabricItems.LEGEND_INGOT))
                    .displayName(Text.translatable("itemGroup.titanfabric.titan"))
                    .build());

    static {
        ItemGroupEvents.modifyEntriesEvent(TITAN).register(entries -> {
            for (Item registeredItem : TitanFabricItems.ALL_ITEMS) {
                if (!(registeredItem instanceof VariationHolder holder)) {
                    entries.add(registeredItem);
                    continue;
                }
                entries.addAll(holder.getVariations());
            }
        });
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
