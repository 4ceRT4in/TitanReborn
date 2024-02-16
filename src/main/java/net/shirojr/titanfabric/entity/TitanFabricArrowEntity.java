package net.shirojr.titanfabric.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
    @Nullable private WeaponEffectData effect;
    @Nullable private ItemStack itemStack;

    public TitanFabricArrowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public TitanFabricArrowEntity(World world, LivingEntity owner, @Nullable WeaponEffectData effectData, @Nullable ItemStack itemStack) {
        super(world, owner);
        this.effect = effectData;
        this.itemStack = itemStack;
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
            EffectHelper.applyWeaponEffectsOnTarget(target.getWorld(), this.itemStack, owner, target);
        }
        super.onHit(target);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (effect == null) return;
        NbtCompound compound = effect.toNbt();
        nbt.put(EFFECTS_COMPOUND_NBT_KEY, compound);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (effect == null || !nbt.contains(EFFECTS_COMPOUND_NBT_KEY)) return;
        WeaponEffectData.fromNbt(nbt, WeaponEffectType.INNATE_EFFECT).ifPresent(effectData -> this.effect = effectData);
    }
}
