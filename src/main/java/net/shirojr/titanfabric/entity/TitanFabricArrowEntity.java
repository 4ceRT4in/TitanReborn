package net.shirojr.titanfabric.entity;

import com.mojang.serialization.DataResult;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.util.TitanFabricCodecs;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;

import static net.shirojr.titanfabric.util.effects.WeaponEffectData.EFFECTS_COMPOUND_NBT_KEY;

public class TitanFabricArrowEntity extends ArrowEntity {
    @Nullable
    private WeaponEffectData effect;
    @Nullable
    private ItemStack itemStack;

    private static final TrackedData<NbtCompound> EFFECT_TYPE = DataTracker.registerData(TitanFabricArrowEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);

    public TitanFabricArrowEntity(EntityType<? extends TitanFabricArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public TitanFabricArrowEntity(World world) {
        super(TitanFabricEntities.ARROW_ITEM, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(EFFECT_TYPE, new NbtCompound()); // Default value, e.g., -1 for no effect
    }

    public TitanFabricArrowEntity(World world, LivingEntity owner, @Nullable WeaponEffectData effectData, @Nullable ItemStack itemStack) {
        super(TitanFabricEntities.ARROW_ITEM, world);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1F, owner.getZ());
        this.setOwner(owner);
        if (owner instanceof PlayerEntity) {
            this.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        }
        this.effect = effectData;
        this.dataTracker.set(EFFECT_TYPE, effectData.toNbt());
        this.itemStack = itemStack;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.inGround) {
            if (this.inGroundTime % 5 == 0) {
                this.spawnParticles(1);
            }
        } else {
            this.spawnParticles(2);
        }
    }

    @Override
    public int getColor() {
        if (this.effect == null) return super.getColor();
        return EffectHelper.getColor(this.effect.weaponEffect());
    }

    private void spawnParticles(int amount) {
        if (this.effect == null || !(getWorld() instanceof ServerWorld serverWorld)) return;
        int i = EffectHelper.getColor(this.effect.weaponEffect());
        if (i == -1 || amount <= 0) return;

        float d = (float) ((i >> 16 & 0xFF) / 255.0);
        float e = (float) ((i >> 8 & 0xFF) / 255.0);
        float f = (float) ((i & 0xFF) / 255.0);
        serverWorld.spawnParticles(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, d, e, f), this.getParticleX(0.5),
                this.getRandomBodyY(), this.getParticleZ(0.5), amount, 0, 0, 0, 1);
    }

    public Optional<WeaponEffectData> getEffect() {
        if (this.effect == null && this.getWorld().isClient) {
            NbtCompound effectId = this.dataTracker.get(EFFECT_TYPE);
            if (effectId != null) {
                this.effect = WeaponEffectData.get(asItemStack(), WeaponEffectType.INNATE_EFFECT).orElse(null);
            }
        }
        return Optional.ofNullable(this.effect);
    }

    public @Nullable ItemStack getItemStack() {
        if (this.itemStack == null) {
            this.itemStack = asItemStack();
        }
        return itemStack;
    }

    @Nullable
    public Identifier getTexture() {
        if (this.effect == null) return null;
        return switch (this.effect.weaponEffect()) {
            case BLIND -> TitanFabric.getId("textures/items/projectiles/blindness_arrow.png");
            case POISON -> TitanFabric.getId("textures/items/projectiles/poison_arrow.png");
            case WEAK -> TitanFabric.getId("textures/items/projectiles/weakness_arrow.png");
            case WITHER -> TitanFabric.getId("textures/items/projectiles/wither_arrow.png");
            default -> Identifier.ofVanilla("textures/entity/projectiles/arrow.png");
        };
    }

    @Override
    protected ItemStack asItemStack() {
        if (this.effect != null) {
            return EffectHelper.applyEffectToStack(new ItemStack(TitanFabricItems.ARROW), this.effect);
        }
        return super.asItemStack();
    }

    @Override
    protected void onHit(LivingEntity target) {
        if (this.getOwner() instanceof LivingEntity owner && this.itemStack != null) {
            EffectHelper.applyWeaponEffectsOnTarget(target.getWorld(), this.itemStack, target);
        }
        super.onHit(target);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (effect == null) return;
        DataResult<HashSet<WeaponEffectData>> preexistingData = TitanFabricCodecs.WEAPON_EFFECTS.parse(NbtOps.INSTANCE, nbt);
        HashSet<WeaponEffectData> data = preexistingData.result().orElse(new HashSet<>());
        data.add(effect);
        TitanFabricCodecs.WEAPON_EFFECTS.encodeStart(NbtOps.INSTANCE, data).result().ifPresent(nbtElement -> {
            nbt.put(EFFECTS_COMPOUND_NBT_KEY, nbtElement);
        });
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (!nbt.contains(EFFECTS_COMPOUND_NBT_KEY)) return;
        NbtCompound effectCompoundNbt = nbt.getCompound(EFFECTS_COMPOUND_NBT_KEY);
        var dataResult = TitanFabricCodecs.WEAPON_EFFECTS.parse(NbtOps.INSTANCE, effectCompoundNbt);
        dataResult.result().ifPresent(data -> {
            Optional<WeaponEffectData> effectData = WeaponEffectData.get(data, WeaponEffectType.INNATE_EFFECT);
            if (effectData.isPresent()) {
                this.effect = effectData.get();
                this.dataTracker.set(EFFECT_TYPE, this.effect.toNbt());
            }
        });
    }
}
