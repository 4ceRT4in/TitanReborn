package net.shirojr.titanfabric.screen.handler;

import com.mojang.datafixers.util.Pair;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.cca.component.ExtendedInventoryComponent;
import net.shirojr.titanfabric.init.TitanFabricScreenHandlers;
import net.shirojr.titanfabric.network.packet.ExtendedInventoryOpenPacket;

public class ExtendedInventoryScreenHandler extends ScreenHandler {
    public static final Identifier BLOCK_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/blocks.png");
    public static final Identifier EMPTY_HELMET_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_helmet");
    public static final Identifier EMPTY_CHESTPLATE_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_chestplate");
    public static final Identifier EMPTY_LEGGINGS_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_leggings");
    public static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_boots");
    public static final Identifier EMPTY_OFFHAND_ARMOR_SLOT = Identifier.ofVanilla("item/empty_armor_slot_shield");
    static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{EMPTY_BOOTS_SLOT_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE};
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private final PlayerInventory baseInventory;
    private final ExtendedInventoryComponent data;

    public ExtendedInventoryScreenHandler(int syncId, PlayerInventory playerInventory, ExtendedInventoryComponent inventoryComponent) {
        super(TitanFabricScreenHandlers.EXTENDED_INVENTORY, syncId);

        this.baseInventory = playerInventory;
        this.data = inventoryComponent;

        addInventorySlots(playerInventory);
        addHotbarSlots(playerInventory);
        addEquipmentSlots(playerInventory);
        addExtendedInventorySlots();
    }

    public ExtendedInventoryScreenHandler(int syncId, PlayerInventory playerInventory, ExtendedInventoryOpenPacket packet) {
        this(syncId, playerInventory, packet.getExtendedInventory(playerInventory.player.getWorld()));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < baseInventory.size() - 1 ? !this.insertItem(itemStack2, baseInventory.size() - 1, this.slots.size(), false) : !this.insertItem(itemStack2, 0, baseInventory.size() - 1, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            this.data.sync();
        }
        super.onClosed(player);
    }

    private void addInventorySlots(PlayerInventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }
        this.addSlot(new Slot(inventory, 40, 77, 62) {
            @Override
            public void setStack(ItemStack stack, ItemStack previousStack) {
                super.setStack(stack, previousStack);
            }

            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });
    }

    private void addHotbarSlots(PlayerInventory inventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    private void addEquipmentSlots(PlayerInventory playerInventory) {
        for (int i = 0; i < 4; ++i) {
            final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
            this.addSlot(new Slot(playerInventory, 39 - i, 8, 8 + i * 18) {

                @Override
                public int getMaxItemCount() {
                    return 1;
                }

                @Override
                public boolean canInsert(ItemStack stack) {
                    return equipmentSlot == playerInventory.player.getPreferredEquipmentSlot(stack);
                }

                @Override
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    ItemStack itemStack = this.getStack();
                    if (!itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE)) {
                        return false;
                    }
                    return super.canTakeItems(playerEntity);
                }

                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
                }
            });
        }
    }

    private void addExtendedInventorySlots() {
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 4; column++) {
                int index = column + row * 4;
                int x = column * 18;
                int y = row * 18;
                this.addSlot(new Slot(this.data.getInventory(), index, x + 90, y + 18));
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

}
