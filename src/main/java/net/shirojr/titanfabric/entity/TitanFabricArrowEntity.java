package net.shirojr.titanfabric.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricEntities;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class TitanFabricArrowEntity extends ArrowEntity {
    @Nullable
    private ItemStack itemStack;

    public TitanFabricArrowEntity(EntityType<? extends TitanFabricArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public TitanFabricArrowEntity(World world, LivingEntity owner, @Nullable ItemStack itemStack) {
        this(TitanFabricEntities.ARROW_ITEM, world);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1F, owner.getZ());
        this.setOwner(owner);
        if (owner instanceof PlayerEntity) {
            this.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        }
        this.itemStack = itemStack;
        if (itemStack == null) {
            throw new IllegalArgumentException("Custom Effect arrow was initialized without an ItemStack");
        }
        if (!itemStack.contains(TitanFabricDataComponents.WEAPON_EFFECTS)) {
            throw new IllegalArgumentException("Custom Effect arrow was initialized without a WeaponEffect");
        }
    }

    @Nullable
    public WeaponEffectData getInateWeaponEffectData() {
        if (this.getItemStack() == null) return null;
        HashSet<WeaponEffectData> data = this.getItemStack().get(TitanFabricDataComponents.WEAPON_EFFECTS);
        if (data == null) return null;
        for (WeaponEffectData entry : data) {
            if (entry.type().equals(WeaponEffectType.INNATE_EFFECT)) return entry;
        }
        return null;
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
        if (this.getInateWeaponEffectData() == null) return super.getColor();
        return EffectHelper.getColor(this.getInateWeaponEffectData().weaponEffect());
    }

    private void spawnParticles(int amount) {
        if (this.getInateWeaponEffectData() == null || !(getWorld() instanceof ServerWorld serverWorld)) return;
        int i = EffectHelper.getColor(this.getInateWeaponEffectData().weaponEffect());
        if (i == -1 || amount <= 0) return;

        float d = (float) ((i >> 16 & 0xFF) / 255.0);
        float e = (float) ((i >> 8 & 0xFF) / 255.0);
        float f = (float) ((i & 0xFF) / 255.0);
        serverWorld.spawnParticles(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, d, e, f), this.getParticleX(0.5),
                this.getRandomBodyY(), this.getParticleZ(0.5), amount, 0, 0, 0, 1);
    }

    @Nullable
    public Identifier getTexture() {
        if (this.getInateWeaponEffectData() == null) return null;
        return switch (this.getInateWeaponEffectData().weaponEffect()) {
            case BLIND -> TitanFabric.getId("textures/items/projectiles/blindness_arrow.png");
            case POISON -> TitanFabric.getId("textures/items/projectiles/poison_arrow.png");
            case WEAK -> TitanFabric.getId("textures/items/projectiles/weakness_arrow.png");
            case WITHER -> TitanFabric.getId("textures/items/projectiles/wither_arrow.png");
            default -> Identifier.ofVanilla("textures/entity/projectiles/arrow.png");
        };
    }

    @Override
    protected ItemStack asItemStack() {
        if (this.getInateWeaponEffectData() != null) {
            return EffectHelper.applyEffectToStack(new ItemStack(TitanFabricItems.EFFECT_ARROW), this.getInateWeaponEffectData(), false);
        }
        return super.asItemStack();
    }

    @Override
    protected void onHit(LivingEntity target) {
        if (this.getOwner() instanceof LivingEntity && this.itemStack != null) {
            EffectHelper.applyWeaponEffectsOnTarget(target.getWorld(), this.itemStack, target);
        }
        super.onHit(target);
    }
}
