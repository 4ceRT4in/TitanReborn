package net.shirojr.titanfabric.item.custom;

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

public class TitanFabricParachuteItem extends Item {

    public TitanFabricParachuteItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        boolean isActive = stack.getOrDefault(TitanFabricDataComponents.ACTIVATED, false);
        if (entity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isOnGround() && isActive) playerEntity.getItemCooldownManager().remove(this);
            if (isActive) {
                if (!selected && !playerEntity.getOffHandStack().getItem().equals(this)) {
                    removeParachute(stack, playerEntity);
                }
                boolean shouldReset = playerEntity.isOnGround() || playerEntity.isOnGround() || playerEntity.getVelocity().getY() > 0 ||
                        playerEntity.isFallFlying() || playerEntity.isTouchingWater() || playerEntity.isInLava();
                if (shouldReset) {
                    stack.set(TitanFabricDataComponents.ACTIVATED, false);
                } else {
                    /*if (Math.random() <= 0.05) {
                        stack.damage(1, playerEntity, playerEntity.getActiveHand().equals(Hand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                    }*/
                    Vec3d rotationVec3d = playerEntity.getRotationVector().multiply(0.01, 0, 0.01);
                    Vec3d newVec3d = playerEntity.getVelocity().add(rotationVec3d);
                    playerEntity.setVelocity(new Vec3d(MathHelper.clamp(newVec3d.getX(), -1.5D, 1.5D), newVec3d.getY(), MathHelper.clamp(newVec3d.getZ(), -1.5D, 1.5D)));
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.isOnGround()) {
            if (user.isSneaking()) {
                if (removeParachute(user.getStackInHand(hand), user)) return super.use(world, user, hand);
            }
            if (isParachuteActivated(user)) {
                return super.use(world, user, hand);
            }
            Vec3d velocity = new Vec3d(user.getVelocity().getX(), user.getVelocity().getY() * 0.05D, user.getVelocity().getZ());
            user.setVelocity(velocity);
            ItemStack stack = user.getStackInHand(hand);
            stack.set(TitanFabricDataComponents.ACTIVATED, true);
            if (world.isClient()) {
                user.playSound(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA.value(), 1.0f, 1.0f);
                TitanFabricSoundHandler.playParachuteSoundInstance((ClientPlayerEntity) user);
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
            if (entity.getMainHandStack().getOrDefault(TitanFabricDataComponents.ACTIVATED, false)) return true;
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
