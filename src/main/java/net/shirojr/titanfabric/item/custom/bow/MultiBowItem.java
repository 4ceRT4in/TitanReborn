package net.shirojr.titanfabric.item.custom.bow;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.enchant.TitanFabricEnchantments;
import net.shirojr.titanfabric.item.custom.TitanFabricBowItem;
import net.shirojr.titanfabric.util.items.MultiBowHelper;

public class MultiBowItem extends TitanFabricBowItem {
    private int projectileTick = 0;

    public MultiBowItem() {
        super();
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack itemStack = new ItemStack(this);
        MultiBowHelper.setConcurrentArrowCount(itemStack, 1);
        return itemStack;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack itemStack = context.getStack();
        int arrows = MultiBowHelper.getConcurrentArrowCount(itemStack) + 1;
        if (arrows > MultiBowHelper.getAfterShotLevel(itemStack)) arrows = 1;
        if (!context.getWorld().isClient()) MultiBowHelper.setConcurrentArrowCount(itemStack, arrows);
        TitanFabric.devLogger("Concurrent Arrows: " + MultiBowHelper.getConcurrentArrowCount(itemStack));
        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return super.use(world, user, hand);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity) || world.isClient()) return;
        int cooldown = 30 * EnchantmentHelper.getLevel(TitanFabricEnchantments.AFTER_SHOT, stack);
        playerEntity.getItemCooldownManager().set(stack.getItem(), cooldown);

        if (!stack.getOrCreateNbt().contains(MultiBowHelper.ARROWS_LEFT_NBT_KEY)) {
            stack.getOrCreateNbt().putInt(MultiBowHelper.ARROWS_LEFT_NBT_KEY, MultiBowHelper.getAfterShotLevel(stack));
            projectileTick = 10 * EnchantmentHelper.getLevel(TitanFabricEnchantments.AFTER_SHOT, stack);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (MultiBowHelper.getAfterShotLevel(stack) < 1) return;
        if (!selected || !(entity instanceof PlayerEntity player)) return;

        if (projectileTick > 0) {
            projectileTick--;
            if (projectileTick % 10 != 0) return;
        }

        if (!stack.getOrCreateNbt().contains(MultiBowHelper.CONCURRENT_ARROWS_NBT_KEY)) {
            stack.getOrCreateNbt().putInt(MultiBowHelper.CONCURRENT_ARROWS_NBT_KEY, 1);
        }

        boolean noArrowsNeeded = player.getAbilities().creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
        double pullProgress = BowItem.getPullProgress(this.getMaxUseTime(stack) - player.getItemUseTimeLeft());
        ItemStack arrowStack = MultiBowHelper.searchFirstArrowStack(player);
        int powerEnchantLevel = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
        int punchEnchantLevel = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
        int flameEnchantLevel = EnchantmentHelper.getLevel(Enchantments.FLAME, stack);

        if (pullProgress < 0.1) return;

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + (float) pullProgress * 0.5f);

        if (world.isClient()) return;

        PersistentProjectileEntity persistentProjectileEntity = MultiBowHelper.prepareArrow(world, player, arrowStack,
                player.getPitch(), player.getYaw(), pullProgress, powerEnchantLevel, punchEnchantLevel, flameEnchantLevel);

        //TODO: reduce arrow stack

        stack.damage(1, player, p -> p.sendToolBreakStatus(p.getActiveHand()));
        world.spawnEntity(persistentProjectileEntity);
    }
}
