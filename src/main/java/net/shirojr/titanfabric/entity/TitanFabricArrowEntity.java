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
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import org.jetbrains.annotations.Nullable;

public class TitanFabricArrowEntity extends ArrowEntity {
    @Nullable
    private WeaponEffects effect;
    @Nullable
    private ItemStack itemStack;

    public TitanFabricArrowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public TitanFabricArrowEntity(World world, LivingEntity owner, @Nullable WeaponEffects effect, @Nullable ItemStack itemStack) {
        super(world, owner);
        this.effect = effect;
        this.itemStack = itemStack;
    }

    public @Nullable WeaponEffects getEffect() {
        return effect;
    }

    public @Nullable ItemStack getItemStack() {
        if (this.itemStack == null) {
            this.itemStack = asItemStack();
        }
        return itemStack;
    }

    @Nullable
    public Identifier getTexture() {
        if (this.effect == null) {
            return null;
        }
        return switch (this.effect) {
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
            return EffectHelper.getStackWithEffect(new ItemStack(TitanFabricItems.ARROW), this.effect);
        }
        return super.asItemStack();
    }

    @Override
    protected void onHit(LivingEntity target) {
        if (this.getOwner() instanceof LivingEntity owner && this.itemStack != null) {
            EffectHelper.applyWeaponEffectOnTarget(this.effect, EffectHelper.getEffectStrength(this.itemStack), target.getWorld(), this.itemStack, owner, target);
        }
        super.onHit(target);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString(EffectHelper.EFFECTS_NBT_KEY, this.effect.getId());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains(EffectHelper.EFFECTS_NBT_KEY)) {
            this.effect = WeaponEffects.getEffect(nbt.getString(EffectHelper.EFFECTS_NBT_KEY));
        }
    }
}
