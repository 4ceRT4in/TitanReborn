package net.shirojr.titanfabric.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;

import java.util.Arrays;
import java.util.stream.Collectors;

public record IngredientModule(Ingredient ingredient, int[] slots) {
    public static final MapCodec<IngredientModule> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(IngredientModule::ingredient),
                    Codec.INT.listOf().xmap(
                            list -> list.stream().mapToInt(Integer::intValue).toArray(),
                            array -> Arrays.stream(array).boxed().toList()
                    ).fieldOf("slots").forGetter(IngredientModule::slots)
            ).apply(instance, IngredientModule::new)
    );

    public static final PacketCodec<RegistryByteBuf, IngredientModule> PACKET_CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC, IngredientModule::ingredient,
            PacketCodecs.VAR_INT.collect(PacketCodecs.toList()).xmap(
                    list -> list.stream().mapToInt(Integer::intValue).toArray(),
                    array -> Arrays.stream(array).boxed().collect(Collectors.toList())
            ), IngredientModule::slots,
            IngredientModule::new
    );

    public boolean test(ItemStack stack, int index) {
        if (!this.ingredient.test(stack)) return false;
        for (int slot : this.slots) {
            if (slot == index) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(int slot) {
        for (int i : slots) {
            if (i == slot) return true;
        }
        return false;
    }

    public int highestSlot() {
        int lastSlot = 0;
        for (int slot : slots) {
            if (slot > lastSlot) lastSlot = slot;
        }
        return lastSlot;
    }

    public boolean fits(int width, int height) {
        int lastIndex = width * height - 1;
        return lastIndex <= highestSlot();
    }
}
