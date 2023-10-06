package net.shirojr.titanfabric.util.items;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.TitanFabricArrowItem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrowSelectionHelper {
    public static final String ARROW_TYPE_NBT_KEY = TitanFabric.MODID + ".arrowType";
    public static final String ARROW_DATA_NBT_KEY = TitanFabric.MODID + ".arrowData";
    private ArrowSelectionHelper() {
        // private ctor to avoid instantiation
    }

    @Nullable
    public static ItemStack findFirstSupportedArrowStack(Inventory inventory, List<ItemStack> supportedArrows) {
        if (inventory.isEmpty()) return null;

        ItemStack outputStack = null;
        for (int i = 0; i < inventory.size(); i++) {
            if (supportedArrows.contains(inventory.getStack(i))) {
                outputStack = inventory.getStack(i);
                break;
            }
        }
        return outputStack;
    }

    public static List<ItemStack> findAllSupportedArrowStacks(Inventory inventory, List<ItemStack> supportedArrows) {
        if (inventory.isEmpty()) return List.of();

        List<ItemStack> outputStacks = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            if (supportedArrows.contains(inventory.getStack(i))) {
                outputStacks.add(inventory.getStack(i));
            }
        }
        return outputStacks;
    }

    @Nullable
    public static ItemStack findFirstMatchingArrowStack(Inventory inventory, ItemStack itemStack) {
        if (inventory.isEmpty()) return null;

        ItemStack outputStack = null;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() instanceof TitanFabricArrowItem arrowItem) {
                outputStack = inventory.getStack(i);
                break;
            }
        }
        return outputStack;
    }

    public static void writeBowProjectileData(ItemStack bowStack, ArrowType arrowType) {
        bowStack.getOrCreateNbt().putString(ARROW_TYPE_NBT_KEY, arrowType.getNbtKey());
    }

    @Nullable
    public static ArrowType getBowProjectileData(ItemStack bowStack) {
        NbtCompound bowNbt = bowStack.getOrCreateNbt();
        if (!bowNbt.contains(ARROW_TYPE_NBT_KEY)) return null;

        return ArrowType.valueOf(bowNbt.getString(ARROW_TYPE_NBT_KEY));
    }

    public static void consumeArrowFromBowData(ItemStack bowStack, Inventory inventory, int amount) {
        ArrowType arrowType = ArrowType.get(bowStack.getOrCreateNbt().getString(ARROW_TYPE_NBT_KEY));

        //TODO: consume correct ItemStack
    }

    public enum ArrowType {
        ARROW("arrow"),
        SPECTRAL_ARROW("spectral_arrow", true),
        POTION_ARROW("potion_arrow", true),
        EFFECT_ARROW("effect_arrow", true),
        TIPPED_ARROW("tipped_arrow", true);

        private final String id;
        private final boolean hasEffect;

        ArrowType(String id, boolean hasEffect) {
            this.id = id;
            this.hasEffect = hasEffect;
        }

        ArrowType(String id) {
            this.id = id;
            this.hasEffect = false;
        }

        public String getNbtKey() {
            return this.id;
        }

        @Nullable
        public static ArrowType get(final String arrowId) {
            return Arrays.stream(values()).filter(arrowType -> arrowType.id.equals(arrowId)).findFirst().orElse(null);
        }
    }
}
