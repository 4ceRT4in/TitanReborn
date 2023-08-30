package net.shirojr.titanfabric.item.custom.bow;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.TitanFabricBowItem;
import net.shirojr.titanfabric.network.TitanFabricNetworking;
import net.shirojr.titanfabric.util.items.MultiBowHelper;

public class MultiBowItem extends TitanFabricBowItem {
    private int projectileTick = 0;
    private int fullArrowCount;

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
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack itemStack = context.getStack();
        //int arrows = MultiBowHelper.getFullArrowCount(itemStack) + 1;
        //if (arrows > MultiBowHelper.getAfterShotLevel(itemStack)) arrows = 1;
        if (!context.getWorld().isClient()) MultiBowHelper.setFullArrowCount(itemStack, this.fullArrowCount);
        TitanFabric.devLogger("Concurrent Arrows: " + MultiBowHelper.getFullArrowCount(itemStack));
        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return super.use(world, user, hand);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity) || world.isClient()) return;
        int cooldown = 30 * MultiBowHelper.getFullArrowCount(stack);
        playerEntity.getItemCooldownManager().set(stack.getItem(), cooldown);

        MultiBowHelper.setArrowsLeft(stack, MultiBowHelper.getFullArrowCount(stack));
        projectileTick = 10 * MultiBowHelper.getFullArrowCount(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClient() || !(entity instanceof PlayerEntity player)) return;
        if (!stack.getOrCreateNbt().contains(MultiBowHelper.FULL_ARROW_COUNT_NBT_KEY))
            MultiBowHelper.setFullArrowCount(stack, this.fullArrowCount);
        if (!selected) return;

        double pullProgress = BowItem.getPullProgress(this.getMaxUseTime(stack) - player.getItemUseTimeLeft());

        if (projectileTick < 1) return;
        projectileTick--;
        if (!validTick(projectileTick)) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeItemStack(MultiBowHelper.searchFirstArrowStack(player));
        buf.writeDouble(pullProgress);
        ClientPlayNetworking.send(TitanFabricNetworking.MULTI_BOW_ARROWS_CHANNEL, buf);

        stack.damage(MultiBowHelper.getFullArrowCount(stack), player, p -> p.sendToolBreakStatus(p.getActiveHand()));
        handleAfterShotNbtValues(stack);
    }

    private static void resetStats(ItemStack bowStack) {
        MultiBowHelper.setArrowsLeft(bowStack, 0);
    }

    private static boolean validTick(int tick) {
        return tick % 10 == 0;
    }

    private static void handleAfterShotNbtValues(ItemStack bowStack) {
        if (bowStack.getOrCreateNbt().contains(MultiBowHelper.ARROWS_LEFT_NBT_KEY)) {
            int arrows = bowStack.getOrCreateNbt().getInt(MultiBowHelper.ARROWS_LEFT_NBT_KEY) - 1;
            if (arrows > 0) bowStack.getOrCreateNbt().putInt(MultiBowHelper.ARROWS_LEFT_NBT_KEY, arrows);
            else bowStack.removeSubNbt(MultiBowHelper.ARROWS_LEFT_NBT_KEY);
        }
    }
}
