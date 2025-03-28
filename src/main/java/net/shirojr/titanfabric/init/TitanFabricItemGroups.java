package net.shirojr.titanfabric.init;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.TitanFabric;

public class TitanFabricItemGroups {
    public static final RegistryKey<ItemGroup> TITAN = register("titan",
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(TitanFabricBlocks.LEGEND_CRYSTAL))
                    .displayName(Text.translatable("itemGroup.titanfabric.titan"))
                    .build());

    static {
        ItemGroupEvents.modifyEntriesEvent(TITAN).register(entries -> entries.addAll(TitanFabricItems.ALL_ITEMS));
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
