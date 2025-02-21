package net.shirojr.titanfabric.recipe.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.color.TitanFabricColorProviders;
import net.shirojr.titanfabric.util.recipes.SlotArrangementType;

public class DyeableRecipe extends ShapelessRecipe {
    public DyeableRecipe(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input) {
        super(id, group, output, input);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        ItemStack itemToDye = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;

        for (int i = 0; i < craftingInventory.size(); i++) {
            ItemStack stack = craftingInventory.getStack(i);
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
        return !itemToDye.isEmpty() && !dye.isEmpty();
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack itemToDye = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;

        for (int i = 0; i < craftingInventory.size(); i++) {
            ItemStack stack = craftingInventory.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof DyeItem) {
                    dye = stack;
                } else {
                    itemToDye = stack.copy();
                }
            }
        }

        if (itemToDye.isEmpty() || dye.isEmpty()) {
            return ItemStack.EMPTY;
        }

        NbtCompound nbt = itemToDye.getOrCreateNbt();
        String newDyeName = ((DyeItem) dye.getItem()).getColor().getName();

        for (String key : TitanFabricColorProviders.COLOR_KEYS) {
            if (nbt.contains(key)) {
                nbt.remove(key);
            }
        }
        nbt.putBoolean(newDyeName, true);

        return itemToDye;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DyeableRecipe.Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<DyeableRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public DyeableRecipe read(Identifier identifier, JsonObject jsonObject) {
            String group = JsonHelper.getString(jsonObject, "group", "");
            DefaultedList<Ingredient> ingredients = getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));
            if (ingredients.size() != 2) {
                throw new JsonParseException("DyableRecipe requires 2 ingredients: one item and dye tag //");
            }
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
            return new DyeableRecipe(identifier, group, output, ingredients);
        }

        private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
            DefaultedList<Ingredient> ingredients = DefaultedList.of();
            for (int i = 0; i < json.size(); i++) {
                Ingredient ingredient = Ingredient.fromJson(json.get(i));
                ingredients.add(ingredient);
            }
            return ingredients;
        }

        @Override
        public DyeableRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            String group = packetByteBuf.readString();
            int size = packetByteBuf.readVarInt();
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) {
                ingredients.set(i, Ingredient.fromPacket(packetByteBuf));
            }
            ItemStack output = packetByteBuf.readItemStack();
            return new DyeableRecipe(identifier, group, output, ingredients);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, DyeableRecipe recipe) {
            packetByteBuf.writeString(recipe.getGroup());
            packetByteBuf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(packetByteBuf);
            }
            packetByteBuf.writeItemStack(recipe.getOutput());
        }
    }

    public static class Type implements RecipeType<DyeableRecipe> {
        private Type() {
        }

        public static final DyeableRecipe.Type INSTANCE = new Type();
        public static final String ID = "dyeable_crafting";
    }
}
