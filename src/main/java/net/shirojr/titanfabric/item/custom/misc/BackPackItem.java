package net.shirojr.titanfabric.item.custom.misc;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.network.packet.BackPackScreenPacket;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;
import net.shirojr.titanfabric.util.BackPackContent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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

    public static void openScreen(PlayerEntity user, ItemStack stack) {
        World world = user.getWorld();
        if (!(stack.getItem() instanceof BackPackItem backPackItem))
            return;
        if (!world.isClient()) {
            user.openHandledScreen(new ExtendedScreenHandlerFactory<BackPackScreenPacket>() {

                @Override
                public BackPackScreenPacket getScreenOpeningData(ServerPlayerEntity player) {
                    return new BackPackScreenPacket(user.getId());
                }

                @Override
                public Text getDisplayName() {
                    return Text.translatable(stack.getItem().getTranslationKey());
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    Type backPackType = backPackItem.getBackpackType();
                    Inventory inventory = getInventory(stack, backPackType);

                    return new BackPackItemScreenHandler(syncId, playerInventory, inventory, backPackType, stack);
                }
            });
        }
    }

    @Nullable
    public static Inventory getInventory(ItemStack itemStack, Type type) {
        BackPackContent content = itemStack.get(TitanFabricDataComponents.BACKPACK_CONTENT);
        if (content == null) return null;

        Inventory inventory = new SimpleInventory(content.size());
        for (int i = 0; i < content.size(); i++) {
            inventory.setStack(i, content.stacks().get(i));
        }

        return inventory;
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }


    public enum Type implements StringIdentifiable {
        SMALL("small", 6, Rarity.UNCOMMON),
        MEDIUM("medium", 12, Rarity.RARE),
        BIG("big", 18, Rarity.EPIC);

        private static final Map<String, Type> TYPES = new Object2ObjectArrayMap<>();
        public static final Codec<Type> CODEC = Codec.stringResolver(StringIdentifiable::asString, TYPES::get);

        private final String id;
        private final int size;
        private final Rarity rarity;

        Type(String id, int size, Rarity rarity) {
            this.id = id;
            this.size = size;
            this.rarity = rarity;
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

        @Override
        public String asString() {
            return getId();
        }
    }
}
