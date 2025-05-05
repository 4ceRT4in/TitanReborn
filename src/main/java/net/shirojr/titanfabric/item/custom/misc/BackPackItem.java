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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;

import java.util.Map;

public class BackPackItem extends Item {
    private final BackPackItem.Type backpackType;
    public static final String INVENTORY_NBT_KEY = TitanFabric.MOD_ID + ".backpack.inventory";

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
                    return null;
                }

                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeItemStack(backpackItemStack);
                }

                @Override
                public Text getDisplayName() {
                    return new TranslatableText(backpackItemStack.getItem().getTranslationKey());
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    Type backPackType = backPackItem.getBackpackType();
                    Inventory inventory = getInventory(backpackItemStack, backPackType);

                    return new BackPackItemScreenHandler(syncId, playerInventory, inventory, backPackType, backpackItemStack);
                }
            });
        }
    }

    public static Inventory getInventory(ItemStack itemStack, Type type) {
        NbtCompound nbtCompound = itemStack.getOrCreateNbt().getCompound(INVENTORY_NBT_KEY);

        Inventory inventory = new SimpleInventory(type.getSize());
        for (int i = 0; i < nbtCompound.getSize(); i++) {
            NbtCompound nbtStack = nbtCompound.getCompound(String.valueOf(i));
            inventory.setStack(i, ItemStack.fromNbt(nbtStack));
        }

        return inventory;
    }

    public static void writeNbtFromInventory(ItemStack itemStack, Inventory inventory) {
        NbtCompound nbtCompound = itemStack.getOrCreateNbt().getCompound(INVENTORY_NBT_KEY);
        for (int i = 0; i < inventory.size(); i++) {
            NbtCompound itemStackNbtCompound = inventory.getStack(i).writeNbt(new NbtCompound());
            nbtCompound.put(String.valueOf(i), itemStackNbtCompound);
        }
        itemStack.getOrCreateNbt().put(INVENTORY_NBT_KEY, nbtCompound);
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }


    public enum Type implements StringIdentifiable {
        SMALL("small", 6, Rarity.UNCOMMON),
        MEDIUM("medium", 12, Rarity.RARE),
        BIG("big", 18, Rarity.EPIC);

        private final Map<String, Type> TYPES = new Object2ObjectArrayMap<>();
        public final Codec<Type> CODEC = Codec.stringResolver(StringIdentifiable::asString, TYPES::get);

        private final String id;
        private final int size;
        private final Rarity rarity;

        Type(String id, int size, Rarity rarity) {
            this.id = id;
            this.size = size;
            this.rarity = rarity;
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
