package net.shirojr.titanfabric.item.custom.bow;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.TitanFabricBowItem;
import net.shirojr.titanfabric.network.TitanFabricNetworking;
import net.shirojr.titanfabric.util.items.MultiBowHelper;

public class MultiBowItem extends TitanFabricBowItem {
    private int projectileTick = 0;
    private final int fullArrowCount;
    private float pullProgress;

    public MultiBowItem(int arrowCount) {
        super();
        this.fullArrowCount = arrowCount;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack itemStack = new ItemStack(this);
        MultiBowHelper.setFullArrowCount(itemStack, 1);
        return itemStack;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity) || world.isClient()) return;

        int maxUseTime = this.getMaxUseTime(stack);
        int useTimeLeft = playerEntity.getItemUseTimeLeft();
        this.pullProgress = BowItem.getPullProgress(maxUseTime - useTimeLeft);
        if (pullProgress < 0.2f) return;

        int cooldown = 30 * MultiBowHelper.getFullArrowCount(stack);
        playerEntity.getItemCooldownManager().set(stack.getItem(), cooldown);

        MultiBowHelper.setArrowsLeft(stack, MultiBowHelper.getFullArrowCount(stack));
        projectileTick = 10 * MultiBowHelper.getFullArrowCount(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient()) return;

    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClient() || !(entity instanceof PlayerEntity player)) return;
        if (!stack.getOrCreateNbt().contains(MultiBowHelper.FULL_ARROW_COUNT_NBT_KEY))
            MultiBowHelper.setFullArrowCount(stack, this.fullArrowCount);
        if (!selected) return;

        if (projectileTick < 1) return;
        projectileTick--;
        if (!validTick(projectileTick + 1)) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeItemStack(MultiBowHelper.searchFirstArrowStack(player));
        buf.writeDouble(this.pullProgress);
        ClientPlayNetworking.send(TitanFabricNetworking.MULTI_BOW_ARROWS_CHANNEL, buf);

        handleAfterShotValues(stack, player);
    }

    private static boolean validTick(int tick) {
        return tick % 10 == 0;
    }

    private static void handleAfterShotValues(ItemStack bowStack, PlayerEntity player) {
        if (bowStack.getOrCreateNbt().contains(MultiBowHelper.ARROWS_LEFT_NBT_KEY)) {
            int arrows = bowStack.getOrCreateNbt().getInt(MultiBowHelper.ARROWS_LEFT_NBT_KEY) - 1;
            if (arrows > 0) bowStack.getOrCreateNbt().putInt(MultiBowHelper.ARROWS_LEFT_NBT_KEY, arrows);
            else bowStack.removeSubNbt(MultiBowHelper.ARROWS_LEFT_NBT_KEY);
        }
        bowStack.damage(MultiBowHelper.getFullArrowCount(bowStack), player, p -> p.sendToolBreakStatus(p.getActiveHand()));
    }
}
