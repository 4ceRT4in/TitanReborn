package net.shirojr.titanfabric.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.shirojr.titanfabric.entity.BarrelBombEntity;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.init.TitanFabricBlocks;

public class BarrelBombBlock extends Block {
    private final BarrelBombEntity.Type type;
    public BarrelBombBlock(Settings settings, BarrelBombEntity.Type type) { super(settings); this.type = type; }

    @Override public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        if (!world.isClient && placer instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(TitanFabricBlocks.CITRIN_BARREL_BOMB.asItem(), 5 * 20);
            player.getItemCooldownManager().set(TitanFabricBlocks.EMBER_BARREL_BOMB.asItem(), 5 * 20);
        }
        if (!world.isClient && world.getGameRules().getBoolean(TitanFabricGamerules.SELF_IGNITING_BARREL_BOMBS)) prime(world, pos, placer);
    }

    @Override protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, net.minecraft.util.Hand hand, BlockHitResult hit) {
        if (stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.FIRE_CHARGE) || stack.isOf(TitanFabricItems.FLINT_AND_EMBER)) {
            if (!world.isClient) {
                prime(world, pos, player);
                if (!player.getAbilities().creativeMode) {
                    if (stack.isOf(Items.FIRE_CHARGE)) stack.decrement(1);
                    else stack.damage(1, player, net.minecraft.entity.EquipmentSlot.MAINHAND);
                }
            }
            return ItemActionResult.success(world.isClient);
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private void prime(World world, BlockPos pos, LivingEntity owner) {
        if (!(world.getBlockState(pos).getBlock() instanceof BarrelBombBlock)) return;
        BarrelBombEntity bomb = new BarrelBombEntity(world, pos.getX() + .5, pos.getY(), pos.getZ() + .5, owner, type);
        world.spawnEntity(bomb);
        world.removeBlock(pos, false);
        world.playSound(null, pos, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
        if (type == BarrelBombEntity.Type.CITRIN) {
            world.addParticle(net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 0, .1, 0);
        } else {
            world.addParticle(net.minecraft.particle.ParticleTypes.FLAME, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 0, .1, 0);
        }
    }
}
