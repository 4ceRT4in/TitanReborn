package net.shirojr.titanfabric.color;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.ItemStack;

public class TitanFabricColorProviders {



    public void register(int color, ItemStack itemStack) {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> color, itemStack.getItem());
    }
}
