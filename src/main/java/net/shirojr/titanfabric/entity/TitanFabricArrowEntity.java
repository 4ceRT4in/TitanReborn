package net.shirojr.titanfabric.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffects;
import org.jetbrains.annotations.Nullable;

public class TitanFabricArrowEntity extends ArrowEntity {
    @Nullable private WeaponEffects effect;
    @Nullable private ItemStack itemStack;
    public TitanFabricArrowEntity(EntityType<? extends ArrowEntity> entityType,
                                  Entity owner, World world, @Nullable WeaponEffects effect, @Nullable ItemStack itemStack) {
        super(entityType, world);
        this.effect = effect;
        this.itemStack = itemStack;
    }

    public TitanFabricArrowEntity(World world) {
        super(TitanFabricEntities.ARROW_ITEM, world);
    }

    public @Nullable WeaponEffects getEffect() {
        return effect;
    }

    public @Nullable ItemStack getItemStack() {
        return itemStack;
    }

    @Nullable
    public Identifier getTexture() {
        if (this.effect == null) return null;
        return switch (this.effect) {
            case BLIND -> new Identifier(TitanFabric.MODID, "textures/items/projectiles/blindness_arrow.png");
            case POISON -> new Identifier(TitanFabric.MODID, "textures/items/projectiles/poison_arrow.png");
            case WEAK -> new Identifier(TitanFabric.MODID, "textures/items/projectiles/weakness_arrow.png");
            case WITHER -> new Identifier(TitanFabric.MODID, "textures/items/projectiles/wither_arrow.png");
            default -> new Identifier("textures/entity/projectiles/arrow.png");
        };
    }

    @Override
    protected void onHit(LivingEntity target) {
        //TODO: if effect is null -> no effect
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
