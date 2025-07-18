package net.shirojr.titanfabric.item.custom.misc;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Blocks;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.shirojr.titanfabric.color.TitanFabricDyeProviders;
import net.shirojr.titanfabric.data.BackPackContent;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;
import net.shirojr.titanfabric.util.TitanFabricTags;

import java.util.ArrayList;
import java.util.List;

public class BackPackItem extends Item {
    private final BackPackItem.Type backpackType;

    public BackPackItem(Settings settings, Type backPackType) {
        super(settings);
        this.backpackType = backPackType;
    }

    public BackPackItem.Type getBackpackType() {
        return this.backpackType;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        openScreen(user, user.getStackInHand(hand));
        return super.use(world, user, hand);
    }

    public static void openScreen(PlayerEntity user, ItemStack backpackItemStack) {
        World world = user.getWorld();
        if (!(backpackItemStack.getItem() instanceof BackPackItem backPackItem))
            return;
        if (!world.isClient()) {
            user.openHandledScreen(new ExtendedScreenHandlerFactory() {

                @Override
                public Object getScreenOpeningData(ServerPlayerEntity player) {
                    return backpackItemStack;
                }

                @Override
                public Text getDisplayName() {
                    if(backPackItem.getBackpackType() == Type.POTION) return Text.translatable("item.titanfabric.potion_bundle_screen");
                    return Text.translatable(backpackItemStack.getItem().getTranslationKey());
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    Type backPackType = backPackItem.getBackpackType();
                    Inventory inventory = getInventoryFromComponents(backpackItemStack, backPackType);

                    return new BackPackItemScreenHandler(syncId, playerInventory, inventory, backPackType, backpackItemStack);
                }
            });
        }
    }

    public static Inventory getInventoryFromComponents(ItemStack itemStack, Type type) {
        BackPackContent content = itemStack.get(TitanFabricDataComponents.BACKPACK_CONTENT);

        Inventory inventory = new SimpleInventory(type.getSize());

        if (content != null) {
            for (int i = 0; i < Math.min(content.getItems().size(), type.getSize()); i++) {
                if(content.getItems().get(i).getItem() != TitanFabricItems.BACKPACK_BIG && content.getItems().get(i) != ItemStack.EMPTY) {
                    inventory.setStack(i, content.getItems().get(i));
                }
            }
        }

        return inventory;
    }

    public static void writeComponentsFromInventory(ItemStack itemStack, Inventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack != ItemStack.EMPTY && stack.getItem() != Blocks.AIR.asItem()) {
                items.add(stack);
            } else {
                items.add(new ItemStack(TitanFabricItems.BACKPACK_BIG)); // the codec doesnt allow empty itemstacks to be saved -> game crash, therefore just this placeholder item since it cannot be placed inside anyway lol
            }
        }
        if(itemStack.isEmpty() || itemStack == null || itemStack.getItem() == Blocks.AIR.asItem()) return;

        BackPackContent content = new BackPackContent(items);
        itemStack.set(TitanFabricDataComponents.BACKPACK_CONTENT, content);
    }


    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if(getBackpackType() == Type.POTION) {
            TitanFabricDyeProviders.applyExtendedTooltip(tooltip,"tooltip.titanfabric.potionBundle");
        }
        TitanFabricDyeProviders.applyColorTooltip(tooltip, stack);
    }

    public enum Type {
        SMALL("small", 6, Rarity.UNCOMMON),
        MEDIUM("medium", 12, Rarity.RARE),
        BIG("big", 18, Rarity.EPIC),
        POTION("potion", 9, Rarity.RARE);

        private final String id;
        private final int size;
        private final Rarity rarity;

        Type(String id, int size, Rarity rarity) {
            this.id = id;
            this.size = size;
            this.rarity = rarity;
        }

        public String getId() {
            return id;
        }

        public int getSize() {
            return size;
        }

        public Rarity getRarity() {
            return rarity;
        }
    }
}