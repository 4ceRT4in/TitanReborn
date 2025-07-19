package net.shirojr.titanfabric.item.custom.misc;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.sound.TitanFabricSoundHandler;

import java.util.List;

public class ParachuteItem extends Item {

    public ParachuteItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        boolean isActive = stack.getOrDefault(TitanFabricDataComponents.ACTIVATED, false);
        if (!(entity instanceof PlayerEntity playerEntity)) return;
        boolean shouldReset = playerEntity.isOnGround() || playerEntity.isFallFlying() || playerEntity.isTouchingWater() || playerEntity.isInLava();
        if (shouldReset) {
            stack.set(TitanFabricDataComponents.ACTIVATED, false);
            if (playerEntity.getItemCooldownManager().isCoolingDown(this)) {
                playerEntity.getItemCooldownManager().remove(this);
                return;
            }
        }
        if (!isActive) return;
        if (!selected && !playerEntity.getOffHandStack().getItem().equals(this)) {
            removeParachute(stack, playerEntity);
        }
        if (shouldReset) {
            stack.set(TitanFabricDataComponents.ACTIVATED, false);
            return;
        }
        Vec3d rotationVec3d = playerEntity.getRotationVector().multiply(0.01, 0, 0.01);
        Vec3d newVec3d = playerEntity.getVelocity().add(rotationVec3d);
        double x = MathHelper.clamp(newVec3d.getX(), -1.5, 1.5);
        double y = newVec3d.getY();
        double z = MathHelper.clamp(newVec3d.getZ(), -1.5, 1.5);
        playerEntity.setVelocity(new Vec3d(x, y, z));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.isOnGround()) {
            if (user.isSneaking()) {
                if (removeParachute(user.getStackInHand(hand), user)) {
                    return super.use(world, user, hand);
                }
            }
            if (isParachuteActivated(user)) {
                return super.use(world, user, hand);
            }
            Vec3d velocity = new Vec3d(user.getVelocity().getX(), user.getVelocity().getY() * 0.05D, user.getVelocity().getZ());
            user.setVelocity(velocity);
            ItemStack stack = user.getStackInHand(hand);
            stack.set(TitanFabricDataComponents.ACTIVATED, true);
            if (world.isClient()) {
                TitanFabricSoundHandler.playParachuteSoundInstance((ClientPlayerEntity) user);
            } else {
                world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA.value(), SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
        return super.use(world, user, hand);

    }

    public boolean removeParachute(ItemStack stack, PlayerEntity user) {
        boolean isActive = stack.getOrDefault(TitanFabricDataComponents.ACTIVATED, false);
        if (isActive) {
            stack.set(TitanFabricDataComponents.ACTIVATED, false);
            user.getItemCooldownManager().set(this, 80);
            if (!user.getWorld().isClient()) {
                user.getWorld().playSound(null, user.getBlockPos(), SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1f, 0.8f);
                user.getWorld().playSound(null, user.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA.value(), SoundCategory.PLAYERS, 1f, 0.8f);
            }
            return true;
        }
        return false;
    }

    public static boolean isParachuteActivated(LivingEntity entity) {
        if (entity.getMainHandStack().isOf(TitanFabricItems.PARACHUTE)) {
            return entity.getMainHandStack().getOrDefault(TitanFabricDataComponents.ACTIVATED, false);
        }
        if (entity.getOffHandStack().isOf(TitanFabricItems.PARACHUTE)) {
            return entity.getOffHandStack().getOrDefault(TitanFabricDataComponents.ACTIVATED, false);
        }
        return false;
    }


    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isIn(ItemTags.WOOL);
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("tooltip.titanfabric.parachute_line1"));
        tooltip.add(Text.translatable("tooltip.titanfabric.parachute_line2"));
    }
}
