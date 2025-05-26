package net.shirojr.titanfabric.screen.handler;

import com.mojang.datafixers.util.Pair;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.network.packet.ExtendedInventoryOpenPacket;
import net.shirojr.titanfabric.persistent.PersistentPlayerData;
import net.shirojr.titanfabric.persistent.PersistentWorldData;
import net.shirojr.titanfabric.screen.TitanFabricScreenHandlers;

public class ExtendedInventoryScreenHandler extends ScreenHandler {
    public static final Identifier BLOCK_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/blocks.png");
    public static final Identifier EMPTY_HELMET_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_helmet");
    public static final Identifier EMPTY_CHESTPLATE_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_chestplate");
    public static final Identifier EMPTY_LEGGINGS_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_leggings");
    public static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_boots");
    static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{EMPTY_BOOTS_SLOT_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE};
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private final PlayerInventory baseInventory;
    private final ExtendedInventoryOpenPacket data;

    public ExtendedInventoryScreenHandler(int syncId, PlayerInventory playerInventory, ExtendedInventoryOpenPacket data) {
        super(TitanFabricScreenHandlers.EXTENDED_INVENTORY_SCREEN_HANDLER, syncId);
        addInventorySlots(playerInventory);
        addHotbarSlots(playerInventory);
        addEquipmentSlots(playerInventory);
        addExtendedInventorySlots(data.inventory().asInventory());
        this.baseInventory = playerInventory;
        this.data = data;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            //remove 1 from the base inventory size because we don't have the offhand slot.
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
        if (player instanceof ServerPlayerEntity serverPlayer) {
            this.data.savePersistent(serverPlayer);
        }
        super.onClosed(player);
    }

    private void addInventorySlots(PlayerInventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }
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

    private void addExtendedInventorySlots(Inventory extendedInventory) {
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 4; column++) {
                int index = column + row * 4;
                int x = column * 18;
                int y = row * 18;
                this.addSlot(new Slot(extendedInventory, index, x + 90, y + 18));
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

}
