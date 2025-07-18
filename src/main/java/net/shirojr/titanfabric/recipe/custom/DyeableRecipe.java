package net.shirojr.titanfabric.recipe.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricRecipeSerializers;

public class DyeableRecipe extends ShapelessRecipe {
    public DyeableRecipe(String group, CraftingRecipeCategory category, ItemStack result, DefaultedList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        ItemStack itemToDye = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;

        for (int i = 0; i < input.getSize(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof DyeItem) {
                    if (!dye.isEmpty()) return false;
                    dye = stack;
                } else {
                    if (!itemToDye.isEmpty()) return false;
                    itemToDye = stack;
                }
            }
        }

        if (itemToDye.isEmpty() || dye.isEmpty()) return false;

        return itemToDye.getItem() == this.getResult(null).getItem();
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack inputItem = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;

        for (int i = 0; i < input.getSize(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof DyeItem) {
                    dye = stack;
                } else {
                    inputItem = stack;
                }
            }
        }

        if (inputItem.isEmpty() || dye.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = this.getResult(lookup).copy();
        if(inputItem.get(TitanFabricDataComponents.BACKPACK_CONTENT) != null) {
            result.set(TitanFabricDataComponents.BACKPACK_CONTENT, inputItem.get(TitanFabricDataComponents.BACKPACK_CONTENT));
        }

        result.set(DataComponentTypes.BASE_COLOR, inputItem.get(DataComponentTypes.BASE_COLOR));

        DyeColor color = ((DyeItem) dye.getItem()).getColor();
        result.set(DataComponentTypes.BASE_COLOR, color);

        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TitanFabricRecipeSerializers.DYEABLE_RECIPE;
    }

    public static class Serializer implements RecipeSerializer<DyeableRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private static final MapCodec<DyeableRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(DyeableRecipe::getGroup),
                        CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(DyeableRecipe::getCategory),
                        ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(r -> r.getResult(null)),
                        Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
                            Ingredient[] filtered = ingredients.stream().filter(i -> !i.isEmpty()).toArray(Ingredient[]::new);
                            if (filtered.length != 2) {
                                return DataResult.error(() -> "DyeableRecipe requires exactly 2 ingredients");
                            }
                            return DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, filtered));
                        }, DataResult::success).forGetter(CraftingRecipe::getIngredients)
                ).apply(instance, DyeableRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, DyeableRecipe> PACKET_CODEC = PacketCodec.ofStatic(Serializer::write, Serializer::read);

        @Override
        public MapCodec<DyeableRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, DyeableRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static DyeableRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            int size = buf.readVarInt();
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) {
                ingredients.set(i, Ingredient.PACKET_CODEC.decode(buf));
            }
            ItemStack result = ItemStack.PACKET_CODEC.decode(buf);
            return new DyeableRecipe(group, category, result, ingredients);
        }

        private static void write(RegistryByteBuf buf, DyeableRecipe recipe) {
            buf.writeString(recipe.getGroup());
            buf.writeEnumConstant(recipe.getCategory());
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.PACKET_CODEC.encode(buf, ingredient);
            }
            ItemStack.PACKET_CODEC.encode(buf, recipe.getResult(null));
        }
    }

    public static class Type implements RecipeType<DyeableRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "dyeable_crafting";
    }
}