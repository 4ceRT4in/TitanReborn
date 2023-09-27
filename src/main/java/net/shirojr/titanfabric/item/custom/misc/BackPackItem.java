package net.shirojr.titanfabric.item.custom.misc;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;
import org.jetbrains.annotations.Nullable;

public class BackPackItem extends Item {
    private final BackPackItem.Type backpackType;
    private final Text itemDisplayName;
    public BackPackItem(Settings settings, Type backPackType) {
        super(settings.rarity(backPackType.getRarity()));
        this.backpackType = backPackType;
        this.itemDisplayName = this.getName();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            user.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeItemStack(user.getStackInHand(hand));
                }

                @Override
                public Text getDisplayName() {
                    return itemDisplayName;
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new BackPackItemScreenHandler(syncId, player.getInventory(), inv, backpackType);
                }
            });
        }
        return super.use(world, user, hand);
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    public enum Type {
        SMALL("small", 6, Rarity.UNCOMMON),
        MEDIUM("medium", 12, Rarity.RARE),
        BIG("big", 18, Rarity.EPIC);

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

