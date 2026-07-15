package net.shirojr.titanfabric.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricEntities;
import net.shirojr.titanfabric.item.custom.spear.SpearTier;
import net.shirojr.titanfabric.item.custom.spear.TitanFabricSpearItem;
import net.shirojr.titanfabric.util.effects.EffectHelper;

import java.util.Locale;
import java.util.UUID;

/** Visual projectile for a spear that stays safely locked in its owner's inventory. */
public class SpearEntity extends PersistentProjectileEntity implements FlyingItemEntity {
    private static final double THROW_DAMAGE_DIVISOR = 5.0;
    /** Separate tracked stack used by the item renderer on the client. */
    private static final TrackedData<ItemStack> RENDER_STACK =
            DataTracker.registerData(SpearEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private ItemStack spearStack = ItemStack.EMPTY;
    private SpearTier tier = SpearTier.CITRIN;
    private UUID ownerUuid;
    private Vec3d throwStart = Vec3d.ZERO;
    private int returnTicks;
    private boolean returned;
    private boolean dealtDamage;

    public SpearEntity(EntityType<? extends SpearEntity> type, World world) { super(type, world); }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(RENDER_STACK, ItemStack.EMPTY);
    }

    public SpearEntity(World world, LivingEntity owner, ItemStack stack, SpearTier tier) {
        super(TitanFabricEntities.SPEAR, owner, world, stack, stack);
        this.spearStack = stack.copy();
        setStack(this.spearStack);
        dataTracker.set(RENDER_STACK, this.spearStack.copy());
        this.tier = tier;
        this.ownerUuid = owner.getUuid();
        this.throwStart = owner.getEyePos();
        this.returnTicks = tier.throwCooldown();
        this.pickupType = PickupPermission.DISALLOWED;
        setDamage(tier.damage());
    }

    /**
     * PersistentProjectileEntity asks for the default stack from its superclass
     * constructor. At that point this class's fields have not been initialized yet.
     */
    @Override
    protected ItemStack getDefaultItemStack() {
        return spearStack == null || spearStack.isEmpty() ? ItemStack.EMPTY : spearStack.copy();
    }

    @Override
    public ItemStack getStack() {
        return dataTracker.get(RENDER_STACK);
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        dealtDamage = true;
        Entity entity = hit.getEntity();
        double damage = getDamage();
        double travelled = 0.0;
        double effectiveTravelled = 0.0;
        int rangeBonus = 0;
        boolean critical = false;
        double rangeScaledDamage = damage;
        if (entity instanceof LivingEntity target) {
            travelled = throwStart.distanceTo(hit.getPos());
            effectiveTravelled = Math.min(travelled, tier.range());
            rangeBonus = (int) Math.floor((effectiveTravelled * tier.rangeModifier()) / THROW_DAMAGE_DIVISOR);
            damage = tier.damage() + rangeBonus;
            rangeScaledDamage = damage;
            if (tier == SpearTier.DIAMOND && getRandom().nextFloat() < 0.5f) {
                damage *= 1.2;
                critical = true;
            }
            setDamage(damage);
        }
        Entity owner = getOwner();
        DamageSource source = getDamageSources().trident(this, owner == null ? this : owner);
        if (getWorld() instanceof ServerWorld serverWorld) {
            damage = EnchantmentHelper.getDamage(serverWorld, activeSpearStack(), entity, source, (float) damage);
        }
        int finalDamage = MathHelper.ceil(MathHelper.clamp(damage, 0.0, Integer.MAX_VALUE));
        boolean damageAccepted = entity.damage(source, finalDamage);
        if (owner instanceof PlayerEntity player) {
            sendHitDebugMessage(player, entity, travelled, effectiveTravelled, rangeBonus, rangeScaledDamage, damage,
                    critical, finalDamage, damageAccepted);
        }
        if (damageAccepted && entity instanceof LivingEntity target) {
            EffectHelper.applyWeaponEffectsOnTarget(getWorld(), activeSpearStack(), target);
            if (getWorld() instanceof ServerWorld serverWorld) {
                EnchantmentHelper.onTargetDamaged(serverWorld, target, source, activeSpearStack());
            }
        }
        setVelocity(getVelocity().multiply(-0.01, -0.1, -0.01));
        playSound(SoundEvents.ITEM_TRIDENT_HIT, 1.0f, 1.0f);
    }

    /** Sends the throw distance and the exact projectile-damage formula to the thrower. */
    private void sendHitDebugMessage(PlayerEntity player, Entity target, double travelled, double effectiveTravelled,
                                     int rangeBonus, double rangeScaledDamage, double modifiedDamage, boolean critical,
                                     int finalDamage, boolean damageAccepted) {
        String distance = formatDecimal(travelled);
        String range = formatDecimal(tier.range());
        String modifier = formatDecimal(tier.rangeModifier());
        String effectiveDistance = formatDecimal(effectiveTravelled);
        String projectileDamage = formatDecimal(rangeScaledDamage);
        String modifiedProjectileDamage = formatDecimal(modifiedDamage);

        player.sendMessage(Text.literal("[Speer] Distanz: " + distance + " / " + range + " Blöcke")
                .formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("[Speer] " + target.getName().getString() + ": "
                        + tier.damage() + " Grundschaden + floor(" + effectiveDistance + " * " + modifier + " / "
                        + formatDecimal(THROW_DAMAGE_DIVISOR) + ") = +" + rangeBonus + " -> " + projectileDamage)
                .formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("[Speer] ceil(" + modifiedProjectileDamage + ")"
                        + (critical ? " (Krit)" : "")
                        + " = " + finalDamage + " Schaden" + (damageAccepted ? "" : " (nicht angenommen)"))
                .formatted(damageAccepted ? Formatting.GREEN : Formatting.RED), false);
    }

    private static String formatDecimal(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    @Override
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return dealtDamage ? null : super.getEntityCollision(currentPosition, nextPosition);
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient || returned) return;
        if (--returnTicks <= 0) returnToOwner();
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!getWorld().isClient && ownerUuid != null && ownerUuid.equals(player.getUuid())) {
            returnToOwner();
        }
    }

    private void returnToOwner() {
        if (returned) return;
        Entity owner = getOwner();
        PlayerEntity player = owner instanceof PlayerEntity ownerPlayer ? ownerPlayer : null;
        if (player == null && getWorld() instanceof ServerWorld serverWorld && ownerUuid != null) {
            player = serverWorld.getServer().getPlayerManager().getPlayer(ownerUuid);
        }
        if (player == null) {
            discard();
            return;
        }
        returned = true;
        TitanFabricSpearItem.clearSpearCooldown(player);
        getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP,
                SoundCategory.PLAYERS, 0.2f, 0.9f + getRandom().nextFloat() * 0.2f);
        // The original stack remains in the owner's inventory throughout the throw.
        // Returning only unlocks that stack; adding another one would duplicate it.
        discard();
    }

    private ItemStack activeSpearStack() {
        ItemStack trackedStack = dataTracker.get(RENDER_STACK);
        return trackedStack.isEmpty() ? spearStack : trackedStack;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("SpearStack", spearStack.encode(getRegistryManager()));
        if (ownerUuid != null) nbt.putUuid("SpearOwner", ownerUuid);
        nbt.putString("SpearTier", tier.name());
        nbt.putDouble("SpearStartX", throwStart.x); nbt.putDouble("SpearStartY", throwStart.y); nbt.putDouble("SpearStartZ", throwStart.z);
        nbt.putInt("SpearReturn", returnTicks);
        nbt.putBoolean("SpearDealtDamage", dealtDamage);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        ItemStack.CODEC.parse(getRegistryManager().getOps(net.minecraft.nbt.NbtOps.INSTANCE), nbt.get("SpearStack")).result().ifPresent(stack -> spearStack = stack);
        setStack(spearStack);
        dataTracker.set(RENDER_STACK, spearStack.copy());
        if (nbt.containsUuid("SpearOwner")) ownerUuid = nbt.getUuid("SpearOwner");
        String savedTier = nbt.getString("SpearTier");
        if ("TITAN".equals(savedTier)) savedTier = "LEGEND";
        try { tier = SpearTier.valueOf(savedTier); } catch (IllegalArgumentException ignored) { tier = SpearTier.CITRIN; }
        throwStart = new Vec3d(nbt.getDouble("SpearStartX"), nbt.getDouble("SpearStartY"), nbt.getDouble("SpearStartZ"));
        returnTicks = nbt.getInt("SpearReturn");
        dealtDamage = nbt.getBoolean("SpearDealtDamage");
        pickupType = PickupPermission.DISALLOWED;
    }
}
