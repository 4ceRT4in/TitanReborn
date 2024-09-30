package net.shirojr.titanfabric.item.custom.bow;

import com.google.common.collect.Lists;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.items.Anvilable;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.SelectableArrows;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TitanCrossBowItem extends CrossbowItem implements SelectableArrows, Anvilable {
    public static final String CHARGED_KEY = "Charged";
    public static final String CHARGED_PROJECTILES_KEY = "ChargedProjectiles";
    public static final int RANGE = 8;
    private boolean charged = false;
    private boolean loaded = false;

    public TitanCrossBowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public List<Item> titanFabric$supportedArrows() {
        return List.of(Items.ARROW, Items.SPECTRAL_ARROW, Items.LINGERING_POTION, Items.SPLASH_POTION);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ArrowSelectionHelper.cleanUpProjectileSelection(user, this);
        ItemStack itemStack = user.getStackInHand(hand);
        if (isCharged(itemStack)) {
            shootAll(world, user, hand, itemStack, getSpeed(itemStack), 1.0f);
            setCharged(itemStack, false);
            return TypedActionResult.consume(itemStack);
        }

        if (!user.getArrowType(itemStack).isEmpty() || !getProjectileType(user, itemStack).isEmpty()) {
            if (!isCharged(itemStack)) {
                this.charged = false;
                this.loaded = false;
                user.setCurrentHand(hand);
            }
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    private static float getSpeed(ItemStack stack) {
        if (hasProjectile(stack, Items.FIREWORK_ROCKET)) {
            return 1.6f;
        }
        return 3.15f;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        float f = getPullProgress(i, stack);
        if (f >= 1.0f && !isCharged(stack) && loadProjectiles(user, stack)) {
            setCharged(stack, true);
            SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, soundCategory, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
    }

    private static boolean loadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative) {
        if (projectile.isEmpty()) {
            return false;
        }
        if (!(crossbow.getItem() instanceof SelectableArrows selectableArrows)) {
            return false;
        }
        ItemStack itemStack;
        boolean bl = creative && selectableArrows.titanFabric$supportedArrows().contains(projectile.getItem());
        if (!(bl || creative || simulated)) {
            itemStack = projectile.split(1);
            if (projectile.isEmpty() && shooter instanceof PlayerEntity) {
                ((PlayerEntity) shooter).getInventory().removeOne(projectile);
            }
        } else {
            itemStack = projectile.copy();
        }
        putProjectile(crossbow, itemStack);
        return true;
    }

    private static boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        int multiShotLevel = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, crossbow);
        int projectileCount = multiShotLevel == 0 ? 1 : 3;
        boolean bl = shooter instanceof PlayerEntity && ((PlayerEntity) shooter).getAbilities().creativeMode;
        ItemStack itemStack = shooter.getArrowType(crossbow);
        if (itemStack.isEmpty()) {
            itemStack = getProjectileType(shooter, crossbow);
        }
        ItemStack itemStack2 = itemStack.copy();
        for (int k = 0; k < projectileCount; ++k) {
            if (k > 0) {
                itemStack = itemStack2.copy();
            }
            if (itemStack.isEmpty() && bl) {
                itemStack = new ItemStack(Items.ARROW);
                itemStack2 = itemStack.copy();
            }
            if (loadProjectile(shooter, crossbow, itemStack, k > 0, bl)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static boolean isCharged(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.getBoolean(CHARGED_KEY);
    }

    public static void setCharged(ItemStack stack, boolean charged) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putBoolean(CHARGED_KEY, charged);
    }

    private static void putProjectile(ItemStack crossbow, ItemStack projectile) {
        NbtCompound nbtCompound = crossbow.getOrCreateNbt();
        NbtList nbtList = nbtCompound.contains(CHARGED_PROJECTILES_KEY, 9) ? nbtCompound.getList(CHARGED_PROJECTILES_KEY, 10) : new NbtList();
        NbtCompound nbtCompound2 = new NbtCompound();
        projectile.writeNbt(nbtCompound2);
        nbtList.add(nbtCompound2);
        nbtCompound.put(CHARGED_PROJECTILES_KEY, nbtList);
    }

    public static List<ItemStack> getProjectiles(ItemStack crossbow) {
        NbtList nbtList;
        ArrayList<ItemStack> list = Lists.newArrayList();
        NbtCompound nbtCompound = crossbow.getNbt();
        if (nbtCompound != null && nbtCompound.contains(CHARGED_PROJECTILES_KEY, 9) && (nbtList = nbtCompound.getList(CHARGED_PROJECTILES_KEY, 10)) != null) {
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound2 = nbtList.getCompound(i);
                list.add(ItemStack.fromNbt(nbtCompound2));
            }
        }
        return list;
    }

    private static void clearProjectiles(ItemStack crossbow) {
        NbtCompound nbtCompound = crossbow.getNbt();
        if (nbtCompound != null) {
            NbtList nbtList = nbtCompound.getList(CHARGED_PROJECTILES_KEY, 9);
            nbtList.clear();
            nbtCompound.put(CHARGED_PROJECTILES_KEY, nbtList);
        }
    }

    public static boolean hasProjectile(ItemStack crossbow, Item projectile) {
        return getProjectiles(crossbow).stream().anyMatch(s -> s.isOf(projectile));
    }

    private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence,
                              float simulated) {
        ProjectileEntity projectileEntity;
        if (world.isClient()) {
            return;
        }
        boolean bl = projectile.isOf(Items.FIREWORK_ROCKET);
        if (bl) {
            projectileEntity = new FireworkRocketEntity(world, projectile, shooter, shooter.getX(), shooter.getEyeY() - (double) 0.15f, shooter.getZ(), true);
        } else {
            if (projectile.getItem() instanceof ThrowablePotionItem) {
                projectileEntity = new PotionEntity(world, shooter);
                ((PotionEntity) projectileEntity).setItem(projectile);
            } else {
                projectileEntity = createArrow(world, shooter, crossbow, projectile);
            }
            if (creative || simulated != 0.0f) {
                if (projectileEntity instanceof PersistentProjectileEntity persistentProjectileEntity) {
                    persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                }
            }
        }
        if (shooter instanceof CrossbowUser crossbowUser) {
            crossbowUser.shoot(crossbowUser.getTarget(), crossbow, projectileEntity, simulated);
        } else {
            Vec3d vec3d = shooter.getOppositeRotationVector(1.0f);
            Quaternion quaternion = new Quaternion(new Vec3f(vec3d), simulated, true);
            Vec3d vec3d2 = shooter.getRotationVec(1.0f);
            Vec3f vec3f = new Vec3f(vec3d2);
            vec3f.rotate(quaternion);

            //if (projectileEntity instanceof PotionEntity) {
            //    projectileEntity.setVelocity(vec3f.getX(), vec3f.getY(), vec3f.getZ(), speed * 0.4f, divergence);
            //} else {
                projectileEntity.setVelocity(vec3f.getX(), vec3f.getY(), vec3f.getZ(), speed, divergence);
            //}
        }
        crossbow.damage(bl ? 3 : 1, shooter, e -> e.sendToolBreakStatus(hand));
        world.spawnEntity(projectileEntity);
        world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, soundPitch);
    }

    private static PersistentProjectileEntity createArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack projectile) {
        ArrowItem arrowItem = (ArrowItem) (projectile.getItem() instanceof ArrowItem ? projectile.getItem() : Items.ARROW);
        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, projectile, entity);
        if (entity instanceof PlayerEntity) {
            persistentProjectileEntity.setCritical(true);
        }
        persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
        persistentProjectileEntity.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, crossbow);
        if (i > 0) {
            persistentProjectileEntity.setPierceLevel((byte) i);
        }
        return persistentProjectileEntity;
    }

    public static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence) {
        List<ItemStack> list = getProjectiles(stack);
        float[] fs = getSoundPitches(entity.getRandom());
        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemStack = list.get(i);
            boolean bl = entity instanceof PlayerEntity && ((PlayerEntity) entity).getAbilities().creativeMode;
            if (itemStack.isEmpty())
                continue;
            if (i == 0) {
                shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 0.0f);
                continue;
            }
            if (i == 1) {
                shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, -10.0f);
                continue;
            }
            if (i != 2)
                continue;
            shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 10.0f);
        }
        postShoot(world, entity, stack);
    }

    private static float[] getSoundPitches(Random random) {
        boolean bl = random.nextBoolean();
        return new float[]{1.0f, getSoundPitch(bl, random), getSoundPitch(!bl, random)};
    }

    private static float getSoundPitch(boolean flag, Random random) {
        float f = flag ? 0.63f : 0.43f;
        return 1.0f / (random.nextFloat() * 0.5f + 1.8f) + f;
    }

    private static void postShoot(World world, LivingEntity entity, ItemStack stack) {
        if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            if (!world.isClient()) {
                Criteria.SHOT_CROSSBOW.trigger(serverPlayerEntity, stack);
            }
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        }
        clearProjectiles(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient()) {
            int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
            SoundEvent soundEvent = this.getQuickChargeSound(i);
            SoundEvent soundEvent2 = i == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
            float f = (float) (stack.getMaxUseTime() - remainingUseTicks) / (float) getPullTime(stack);
            if (f < 0.2f) {
                this.charged = false;
                this.loaded = false;
            }
            if (f >= 0.2f && !this.charged) {
                this.charged = true;
                world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
            if (f >= 0.5f && soundEvent2 != null && !this.loaded) {
                this.loaded = true;
                world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent2, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return getPullTime(stack) + 3;
    }

    public static int getPullTime(ItemStack stack) {
        int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
        return i == 0 ? 25 : 25 - 5 * i;
    }

    private static ItemStack getProjectileType(LivingEntity user, ItemStack crossbow) {
        if (!(crossbow.getItem() instanceof SelectableArrows selectableArrows)) {
            return ItemStack.EMPTY;
        }
        Inventory inventory = null;
        if (user instanceof PlayerEntity player) {
            inventory = player.getInventory();
        } else if (user instanceof InventoryOwner inventoryOwner) {
            inventory = inventoryOwner.getInventory();
        }
        if (inventory != null) {
            for (int i = 0; i < inventory.size(); ++i) {
                ItemStack itemStack = inventory.getStack(i);
                if (!selectableArrows.titanFabric$supportedArrows().contains(itemStack.getItem())) {
                    continue;
                }
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    private SoundEvent getQuickChargeSound(int stage) {
        switch (stage) {
            case 1 -> {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1;
            }
            case 2 -> {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2;
            }
            case 3 -> {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3;
            }
        }
        return SoundEvents.ITEM_CROSSBOW_LOADING_START;
    }

    private static float getPullProgress(int useTicks, ItemStack stack) {
        float f = (float) useTicks / (float) getPullTime(stack);
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        List<ItemStack> list = getProjectiles(stack);
        if (!isCharged(stack) || list.isEmpty()) return;
        ItemStack itemStack = list.get(0);
        tooltip.add(new TranslatableText("item.minecraft.crossbow.projectile").append(" ").append(itemStack.toHoverableText()));
        if (context.isAdvanced() && itemStack.isOf(Items.FIREWORK_ROCKET)) {
            ArrayList<Text> list2 = Lists.newArrayList();
            Items.FIREWORK_ROCKET.appendTooltip(itemStack, world, list2, context);
            if (!list2.isEmpty()) {
                list2.replaceAll(text -> new LiteralText("  ").append((Text) text).formatted(Formatting.GRAY));
                tooltip.addAll(list2);
            }
        }
    }

    @Override
    public int getRange() {
        return RANGE;
    }
}
