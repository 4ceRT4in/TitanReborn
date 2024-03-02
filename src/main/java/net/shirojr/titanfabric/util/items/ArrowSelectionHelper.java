package net.shirojr.titanfabric.util.items;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.util.Pair;
import net.shirojr.titanfabric.TitanFabric;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ArrowSelectionHelper {
    public static final String ARROW_TYPE_NBT_KEY = TitanFabric.MODID + ".arrowType";

    private ArrowSelectionHelper() {
        // private constructor to avoid instantiation
    }

    @Nullable
    public static ItemStack findFirstSupportedArrowStack(Inventory inventory, SelectableArrows rangedWeapon) {
        if (inventory.isEmpty()) return null;

        ItemStack outputStack = null;
        for (int i = 0; i < inventory.size(); i++) {
            if (rangedWeapon.supportedArrows().contains(inventory.getStack(i).getItem())) {
                outputStack = inventory.getStack(i);
                break;
            }
        }
        return outputStack;
    }

    public static List<ItemStack> findAllSupportedArrowStacks(Inventory inventory, SelectableArrows rangedWeapon) {
        if (inventory.isEmpty()) return List.of();

        List<ItemStack> outputStacks = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            if (rangedWeapon.supportedArrows().contains(inventory.getStack(i).getItem())) {
                outputStacks.add(inventory.getStack(i));
            }
        }
        return outputStacks;
    }

    public static void writeProjectileData(ItemStack bowStack, Pair<ArrowType, Potion> map) {
        bowStack.getOrCreateNbt().putString(ARROW_TYPE_NBT_KEY, map.getLeft().getNbtKey());
    }

    /**
     * Gets information from a Ranged Weapon to find similar ItemStacks in inventories.
     * This method avoids checks for ItemStack count, durability and other information
     * which would get in the way of accessing correct ItemStacks in inventories
     *
     * @param bowStack original ItemStack, where the information will be retrieved from
     * @return Returns a Pair consisting of the {@link ArrowType ArrowType}, and its
     */
/*    @Nullable
    public static Pair<ArrowType, EffectNbtKeys> readProjectileData(ItemStack bowStack) {
        NbtCompound bowNbt = bowStack.getOrCreateNbt();
        if (!bowNbt.contains(ARROW_TYPE_NBT_KEY)) return null;
        ArrowType arrowType = ArrowType.valueOf(bowNbt.getString(ARROW_TYPE_NBT_KEY));
        EffectNbtKeys effectKeys;

        if (bowNbt.contains(POTION_DATA_NBT_KEY)) {
            effectHandler = EffectNbtKeys.POTION;
        }
        return new Pair<>(arrowType, potion);
    }*/
    public static void consumeArrowFromBowData(ItemStack bowStack, Inventory inventory, int amount) {
        ArrowType arrowType = ArrowType.get(bowStack.getOrCreateNbt().getString(ARROW_TYPE_NBT_KEY));

        //TODO: consume correct ItemStack
    }

    public enum ArrowType {
        ARROW("arrow", stack -> stack.getItem().equals(Items.ARROW)),
        SPECTRAL_ARROW("spectral_arrow", stack -> stack.getItem().equals(Items.SPECTRAL_ARROW)),
        POTION_PROJECTILE("potion_projectile", stack -> stack.getItem().equals(Items.POTION));
        // WEAPON_EFFECT_ARROW("effect_arrow", TitanFabricItems.ARROW),
        // TIPPED_ARROW("tipped_arrow", Items.TIPPED_ARROW);

        private final String id;
        private final Predicate<ItemStack> predicate;

        ArrowType(String id, Predicate<ItemStack> matchesLoosely) {
            this.id = id;
            this.predicate = matchesLoosely;
        }

        public String getNbtKey() {
            return this.id;
        }

        public boolean doesMatch(ItemStack stack) {
            return this.predicate.test(stack);
        }

        @Nullable
        public static ArrowType get(String arrowId) {
            return Arrays.stream(values()).filter(arrowType -> arrowType.id.equals(arrowId)).findFirst().orElse(null);
        }
    }

    enum EffectNbtKeys {
        POTION(TitanFabric.MODID + ".potionData"),
        WEAPON_EFFECT(TitanFabric.MODID + ".weaponEffectData"),
        NONE(TitanFabric.MODID + ".noData");

        private final String nbtKey;

        EffectNbtKeys(String nbtKey) {
            this.nbtKey = nbtKey;
        }

        public String getNbtKey() {
            return this.nbtKey;
        }
    }
}
