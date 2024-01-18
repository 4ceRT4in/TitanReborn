package net.shirojr.titanfabric.item.custom.misc;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.entity.CitrinStarEntity;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;

public class CitrinStarItem extends Item {

    public CitrinStarItem() {
        super(new FabricItemSettings().group(TitanFabricItemGroups.TITAN).maxCount(16));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        user.getItemCooldownManager().set(this, 20);
        if (!world.isClient()) {
            CitrinStarEntity citrinStarEntity = new CitrinStarEntity(world, user);
            citrinStarEntity.setItem(itemStack);
            citrinStarEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 1.0f);
            world.spawnEntity(citrinStarEntity);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }
        return TypedActionResult.success(itemStack, world.isClient());
    }
}
