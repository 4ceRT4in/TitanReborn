package net.shirojr.titanfabric.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

    public TitanFabricArrowEntity(World world) {
        super(TitanFabricEntities.ARROW_ITEM, world);
    }

    public TitanFabricArrowEntity(World world, LivingEntity owner, @Nullable WeaponEffectData effectData, @Nullable ItemStack itemStack) {
        super(world, owner);
        this.effect = effectData;
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

    public Optional<WeaponEffect> getEffect() {
        if (effect == null) return Optional.empty();
        return Optional.ofNullable(effect.weaponEffect());
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
        if (effect == null || !nbt.contains(EFFECTS_COMPOUND_NBT_KEY)) return;
        WeaponEffectData.fromNbt(nbt, WeaponEffectType.INNATE_EFFECT).ifPresent(effectData -> this.effect = effectData);
    }
}
