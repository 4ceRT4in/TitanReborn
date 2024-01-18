package net.shirojr.titanfabric.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.TitanFabricItems;

public class TitanFabricParachuteItem extends Item {

    public TitanFabricParachuteItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.getTime() % 20 == 0 && entity instanceof PlayerEntity playerEntity && playerEntity.isOnGround() && stack.getNbt() != null && stack.getNbt().contains("Activated")
                && stack.getNbt().getBoolean("Activated")) {
            NbtCompound nbtCompound = stack.getOrCreateNbt();
            nbtCompound.putBoolean("Activated", false);
            stack.setNbt(nbtCompound);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.isOnGround()) {
            NbtCompound nbtCompound = user.getStackInHand(hand).getOrCreateNbt();
            nbtCompound.putBoolean("Activated", true);
            user.getStackInHand(hand).setNbt(nbtCompound);
        }
        return super.use(world, user, hand);

    }

    public static boolean isParachuteActivated(LivingEntity entity) {
        if (entity.getMainHandStack().isOf(TitanFabricItems.PARACHUTE)) {
            if (entity.getMainHandStack().getNbt() != null && entity.getMainHandStack().getNbt().contains("Activated") && entity.getMainHandStack().getNbt().getBoolean("Activated")) {
                return true;
            }
        } else if (entity.getOffHandStack().isOf(TitanFabricItems.PARACHUTE)) {
            if (entity.getOffHandStack().getNbt() != null && entity.getOffHandStack().getNbt().contains("Activated") && entity.getOffHandStack().getNbt().getBoolean("Activated")) {
                return true;
            }
        }
        return false;
    }

}
