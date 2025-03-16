package net.shirojr.titanfabric.entity;

import net.minecraft.entity.Entity;
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
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import org.jetbrains.annotations.Nullable;

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
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(EFFECT_TYPE, new NbtCompound()); // Default value, e.g., -1 for no effect
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
        // FIXME: doesn't execute on client side?
        super.tick();
        if (this.world.isClient) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnParticles(1);
                }
            } else {
                this.spawnParticles(2);
            }
        }
    }

    @Override
    public int getColor() {
        if (this.effect == null) return super.getColor();
        return EffectHelper.getColor(this.effect.weaponEffect());
    }

    private void spawnParticles(int amount) {
        if (this.effect == null) return;
        int i = EffectHelper.getColor(this.effect.weaponEffect());
        if (i == -1 || amount <= 0) return;

        double d = (double)(i >> 16 & 0xFF) / 255.0;
        double e = (double)(i >> 8 & 0xFF) / 255.0;
        double f = (double)(i & 0xFF) / 255.0;
        for (int j = 0; j < amount; ++j) {
            this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5),
                    this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
        }
    }

    public Optional<WeaponEffectData> getEffect() {
        if (this.effect == null && this.world.isClient) {
            NbtCompound effectId = this.dataTracker.get(EFFECT_TYPE);
            if (effectId != null) {
                this.effect = WeaponEffectData.fromNbt(effectId, WeaponEffectType.INNATE_EFFECT).orElse(null);
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
            case BLIND -> new Identifier(TitanFabric.MODID, "textures/items/projectiles/blindness_arrow.png");
            case POISON -> new Identifier(TitanFabric.MODID, "textures/items/projectiles/poison_arrow.png");
            case WEAK -> new Identifier(TitanFabric.MODID, "textures/items/projectiles/weakness_arrow.png");
            case WITHER -> new Identifier(TitanFabric.MODID, "textures/items/projectiles/wither_arrow.png");
            case FIRE -> new Identifier(TitanFabric.MODID, "textures/items/projectiles/fire_arrow.png");
            default -> new Identifier("textures/entity/projectiles/arrow.png");
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
        NbtCompound compound = effect.toNbt();  //FIXME: NullPointerException ? But whyyyy
        nbt.put(EFFECTS_COMPOUND_NBT_KEY, compound);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (!nbt.contains(EFFECTS_COMPOUND_NBT_KEY)) return;
        Optional<WeaponEffectData> effectData = WeaponEffectData.fromNbt(nbt.getCompound(EFFECTS_COMPOUND_NBT_KEY), WeaponEffectType.INNATE_EFFECT);
        if(effectData.isPresent()) {
            this.effect = effectData.get();
            this.dataTracker.set(EFFECT_TYPE, this.effect.toNbt());
        }
    }
}
