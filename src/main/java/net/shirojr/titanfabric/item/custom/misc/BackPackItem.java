package net.shirojr.titanfabric.item.custom.misc;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.network.packet.BackPackScreenPacket;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;
import net.shirojr.titanfabric.data.BackPackContent;

import java.util.Map;

public class BackPackItem extends Item {
    public BackPackItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        openScreen(user, user.getStackInHand(hand));
        return super.use(world, user, hand);
    }

    public static void openScreen(PlayerEntity user, ItemStack stack) {
        World world = user.getWorld();
        if (!(stack.getItem() instanceof BackPackItem backPackItem))
            return;
        if (!world.isClient()) {
            user.openHandledScreen(new ExtendedScreenHandlerFactory<BackPackScreenPacket>() {

                @Override
                public BackPackScreenPacket getScreenOpeningData(ServerPlayerEntity player) {
                    return new BackPackScreenPacket(stack);
                }

                @Override
                public Text getDisplayName() {
                    return Text.translatable(stack.getItem().getTranslationKey());
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    return new BackPackItemScreenHandler(syncId, playerInventory, stack);
                }
            });
        }
    }

    public BackPackItem.Type getBackpackType(ItemStack itemStack) {
        return BackPackContent.getOrThrow(itemStack).type();
    }


    public static Inventory getInventory(ItemStack itemStack, Type type) {
        return BackPackContent.getOrThrow(itemStack).inventory();
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }


    public enum Type implements StringIdentifiable {
        SMALL("small", 6, Rarity.UNCOMMON, "textures/gui/backpack_small.png"),
        MEDIUM("medium", 12, Rarity.RARE, "textures/gui/backpack_medium.png"),
        BIG("big", 18, Rarity.EPIC, "textures/gui/backpack_big.png");

        private static final Map<String, Type> TYPES = new Object2ObjectArrayMap<>();
        public static final Codec<Type> CODEC = Codec.stringResolver(StringIdentifiable::asString, TYPES::get);

        private final String id;
        private final int size;
        private final Rarity rarity;
        private final Identifier screenTexture;

        Type(String id, int size, Rarity rarity, String screenTexture) {
            this.id = id;
            this.size = size;
            this.rarity = rarity;
            this.screenTexture = TitanFabric.getId(screenTexture);
            addType();
        }

        private void addType() {
            TYPES.put(id, this);
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

        public Identifier getScreenTexture() {
            return screenTexture;
        }

        @Override
        public String asString() {
            return getId();
        }
    }
}
