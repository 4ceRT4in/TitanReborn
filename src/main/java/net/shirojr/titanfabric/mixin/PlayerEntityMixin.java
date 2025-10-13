package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricShieldItem;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.item.custom.material.TitanFabricToolMaterials;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.handler.ArrowShootingHandler;
import net.shirojr.titanfabric.util.items.ArmorHelper;
import net.shirojr.titanfabric.util.items.SelectableArrow;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Map;

@Debug(export = true)
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ArrowShootingHandler {
    @Unique
    private static final TrackedData<Boolean> SHOOTING_ARROWS = DataTracker.registerData(PlayerEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);


    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract void setFireTicks(int fireTicks);

    @Shadow
    public abstract void incrementStat(Stat<?> stat);

    @Shadow
    public abstract ItemCooldownManager getItemCooldownManager();

    @Shadow
    public abstract void remove(RemovalReason reason);

    @Shadow
    public abstract PlayerInventory getInventory();

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void titanfabric$appendSelectedArrowDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(SHOOTING_ARROWS, false);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    private void titanfabric$readNbt(NbtCompound nbt, CallbackInfo ci) {
        titanfabric$shootsArrows(nbt.getBoolean("shoots_arrows"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void titanfabric$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("shoots_arrows", titanfabric$isShootingArrows());
    }

    @Override
    public boolean titanfabric$isShootingArrows() {
        return this.dataTracker.get(SHOOTING_ARROWS);
    }

    @Override
    public void titanfabric$shootsArrows(boolean shootsArrows) {
        this.dataTracker.set(SHOOTING_ARROWS, shootsArrows);
    }

    @Inject(method = "getProjectileType", at = @At("HEAD"), cancellable = true)
    private void titanfabric$handleArrowSelection(ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack selectedArrowStack = SelectableArrow.getSelectedArrowStack(stack, (PlayerEntity) (Object) this);
        if (selectedArrowStack == null) return;
        cir.setReturnValue(selectedArrowStack);
    }

    @ModifyVariable(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At("STORE"), ordinal = 2)
    private boolean attack(boolean bl3, Entity target) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (!(target instanceof LivingEntity livingEntity)) return bl3;
        if (hasFullDiamondArmor(livingEntity)) return false;
        if (livingEntity.timeUntilRegen > 10.0F) return bl3;

        ItemStack stack = self.getStackInHand(Hand.MAIN_HAND);
        if (stack == null) return bl3;
        if (Arrays.asList(Items.DIAMOND_SWORD, TitanFabricItems.DIAMOND_SWORD, TitanFabricItems.DIAMOND_GREATSWORD).contains(stack.getItem())) {
            if (self.getRandom().nextFloat() < 0.25f) {
                return true;
            }
        }
        return bl3;
    }

    @Unique
    private boolean hasFullDiamondArmor(LivingEntity entity) {
        return entity.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.DIAMOND_HELMET)
                && entity.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.DIAMOND_CHESTPLATE)
                && entity.getEquippedStack(EquipmentSlot.LEGS).isOf(Items.DIAMOND_LEGGINGS)
                && entity.getEquippedStack(EquipmentSlot.FEET).isOf(Items.DIAMOND_BOOTS);
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float damage(float amount, DamageSource source) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!player.getWorld().isClient()) {
            if (source != null && source.getSource() != null && source.getSource() instanceof LivingEntity attacker) {
                ItemStack mainIs = attacker.getMainHandStack();

                if (mainIs != null && mainIs.getItem() instanceof ToolItem) {
                    ToolMaterial toolMaterial = ((ToolItem) mainIs.getItem()).getMaterial();

                    Map<ToolMaterial, ArmorPlateType> armorPlateTypes = Map.of(
                            ToolMaterials.DIAMOND, ArmorPlateType.DIAMOND,
                            ToolMaterials.NETHERITE, ArmorPlateType.NETHERITE,
                            TitanFabricToolMaterials.CITRIN, ArmorPlateType.CITRIN,
                            TitanFabricToolMaterials.EMBER, ArmorPlateType.EMBER,
                            TitanFabricToolMaterials.LEGEND, ArmorPlateType.LEGEND
                    );
                    ArmorPlateType plateType = armorPlateTypes.get(toolMaterial);
                    if (plateType != null) {
                        int armorProb = 0;
                        if (armorProb > 0) {
                            float damageReduction = 0.025f * armorProb;
                            amount = amount * (1.0f - damageReduction);
                        }
                    }
                }
            }
        }

        return amount;
    }

    @Inject(method = "damage", at = @At(value = "TAIL", shift = At.Shift.BEFORE) /* leave that in order for ember armor to work */, cancellable = true)
    private void titanfabric$damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (amount > 0.00001f && (this.timeUntilRegen <= 10 || amount > this.lastDamageTaken)) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            if ((source.isOf(DamageTypes.HOT_FLOOR) || source.isOf(DamageTypes.LAVA) || source.isIn(DamageTypeTags.IS_FIRE))) {
                int netherArmorCount = ArmorHelper.getEmberArmorCount(player);
                if (netherArmorCount > 0) {

                    if (netherArmorCount == 4) {
                        player.setFireTicks(0);
                        cir.setReturnValue(false);
                    } else {
                        if (EffectHelper.shouldEffectApply(player.getWorld().getRandom(), netherArmorCount)) {
                            this.timeUntilRegen = 20;
                            this.lastDamageTaken = amount;
                            cir.setReturnValue(false);
                        }
                    }
                }
            } else if (source.isOf(DamageTypes.WITHER) || source.isOf(DamageTypes.MAGIC)) {
                int citrinArmorCount = Math.min(4, ArmorHelper.getCitrinArmorCount(player));
                if (citrinArmorCount > 0) {
                    if (citrinArmorCount == 4) {
                        cir.setReturnValue(false);
                    } else {
                        if (EffectHelper.shouldEffectApply(player.getWorld().getRandom(), citrinArmorCount)) {
                            this.timeUntilRegen = 20;
                            this.lastDamageTaken = amount;
                            cir.setReturnValue(false);
                        }
                    }
                }
            }
        }
    }

    @ModifyConstant(method = "attack",
            constant = @Constant(floatValue = 1.5f),
            slice = @Slice(
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMovementSpeed()F")
            )
    )
    private float titanfabric$critChanges(float constant, Entity target) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();
        float crit;
        if (stack.getItem() instanceof TitanFabricSwordItem titanFabricSwordItem) {
            crit = titanFabricSwordItem.getCritMultiplier();
        } else if (stack.getItem() instanceof SwordItem) {
            crit = 1.2f;
        } else {
            crit = constant;
        }
        int pieces = 0;
        if (target instanceof LivingEntity living) {
            if (living.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.DIAMOND_HELMET)) pieces++;
            if (living.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.DIAMOND_CHESTPLATE)) pieces++;
            if (living.getEquippedStack(EquipmentSlot.LEGS).isOf(Items.DIAMOND_LEGGINGS)) pieces++;
            if (living.getEquippedStack(EquipmentSlot.FEET).isOf(Items.DIAMOND_BOOTS)) pieces++;
        }
        float reduction = Math.max(0f, 1f - 0.25f * pieces);
        float endCrit = Math.max(0f, crit - 1f);
        return 1f + endCrit * reduction;
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void titanfabric$coolDownChanges(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();

        int cooldown = 0;
        if (stack.getItem() instanceof TitanFabricSwordItem titanFabricSwordItem) {
            if (this.getItemCooldownManager().isCoolingDown(titanFabricSwordItem)) {
                ci.cancel();
                return;
            }
            if (!target.getWorld().isClient() && target.getWorld().getGameRules().getBoolean(TitanFabricGamerules.GREATSWORD_COOLDOWN)) {
                cooldown = titanFabricSwordItem.getCooldownTicks();
            }
        }
        if (cooldown <= 0) return;
        this.getItemCooldownManager().set(stack.getItem(), cooldown);
    }

    @Inject(method = "getAttackCooldownProgressPerTick", at = @At("HEAD"), cancellable = true)
    private void titanfabric$getAttackCooldownProgressPerTickMixin(CallbackInfoReturnable<Float> cir) {
        if (this.getWorld().getGameRules().getBoolean(TitanFabricGamerules.LEGACY_COMBAT)) {
            cir.setReturnValue(0.00001f);
        }
    }

    @Inject(method = "damageShield", at = @At("HEAD"))
    private void titanfabric$damageShield(float amount, CallbackInfo ci) {
        if (!(this.activeItemStack.getItem() instanceof TitanFabricShieldItem)) return;
        if (amount < 3.0f) return;
        if (!getWorld().isClient()) this.incrementStat(Stats.USED.getOrCreateStat(this.activeItemStack.getItem()));
        int i = 1 + MathHelper.floor(amount);
        Hand hand = this.getActiveHand();
        this.activeItemStack.damage(i, this, LivingEntity.getSlotForHand(hand));
        if (!this.activeItemStack.isEmpty()) return;

        if (hand == Hand.MAIN_HAND) this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        else this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        this.activeItemStack = ItemStack.EMPTY;
        this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + getWorld().random.nextFloat() * 0.4F);
    }

    @Inject(method = "disableShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V"))
    public void titanfabric$disableNeMuelchShield(CallbackInfo ci) {
        this.getItemCooldownManager().set(TitanFabricItems.DIAMOND_SHIELD.asItem(), 80);
        this.getItemCooldownManager().set(TitanFabricItems.LEGEND_SHIELD.asItem(), 60);
        this.getItemCooldownManager().set(TitanFabricItems.NETHERITE_SHIELD.asItem(), 40);
    }
}
