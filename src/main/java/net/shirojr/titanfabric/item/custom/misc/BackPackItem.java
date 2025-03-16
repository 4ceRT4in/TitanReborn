package net.shirojr.titanfabric.item.custom.misc;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
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
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.screen.handler.BackPackItemScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BackPackItem extends Item {
    private final BackPackItem.Type backpackType;
    public static final String INVENTORY_NBT_KEY = TitanFabric.MODID + ".backpack.inventory";

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
                    Inventory inventory = getInventoryFromNbt(backpackItemStack, backPackType);

                    return new BackPackItemScreenHandler(syncId, playerInventory, inventory, backPackType, backpackItemStack);
                }
            });
        }
    }

    public static Inventory getInventoryFromNbt(ItemStack itemStack, Type type) {
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
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt()) {
            if (stack.getNbt().contains("red")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorRed"));
                return;
            }
            if(stack.getNbt().contains("orange")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorOrange"));
                return;
            }
            if (stack.getNbt().contains("blue")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorBlue"));
                return;
            }
            if (stack.getNbt().contains("gray")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorGray"));
                return;
            }
            if (stack.getNbt().contains("lime")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorLime"));
                return;
            }
            if (stack.getNbt().contains("pink")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorPink"));
                return;
            }
            if (stack.getNbt().contains("purple")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorPurple"));
                return;
            }
            if (stack.getNbt().contains("light_blue")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorLightBlue"));
                return;
            }
            if (stack.getNbt().contains("light_gray")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorLightGray"));
                return;
            }
            if (stack.getNbt().contains("yellow")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorYellow"));
                return;
            }
            if (stack.getNbt().contains("magenta")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorMagenta"));
                return;
            }
            if (stack.getNbt().contains("cyan")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorCyan"));
                return;
            }
            if (stack.getNbt().contains("brown")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorBrown"));
                return;
            }
            if (stack.getNbt().contains("green")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorGreen"));
                return;
            }
            if (stack.getNbt().contains("black")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorBlack"));
                return;
            }
            if (stack.getNbt().contains("white")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorWhite"));
                return;
            }
        }
    }

    public enum Type {
        SMALL("small", 6, Rarity.UNCOMMON), MEDIUM("medium", 12, Rarity.RARE), BIG("big", 18, Rarity.EPIC), POTION("potion", 9, Rarity.RARE);

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
