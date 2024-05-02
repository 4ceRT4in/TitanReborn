package net.shirojr.titanfabric.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SwordItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.gamerule.TitanFabricGamerules;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricShieldItem;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.EmberArmorItem;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;
import net.shirojr.titanfabric.util.items.ArmorHelper;
import net.shirojr.titanfabric.util.items.SelectableArrows;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Debug(export = true)
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ArrowSelectionHandler {
    @Unique
    private static final TrackedData<ItemStack> SELECTED_ARROW = DataTracker.registerData(PlayerEntityMixin.class, TrackedDataHandlerRegistry.ITEM_STACK);

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
    @Final
    private PlayerInventory inventory;

    @Shadow
    public abstract void remove(RemovalReason reason);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void titanfabric$appendSelectedArrowDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(SELECTED_ARROW, ItemStack.EMPTY);
    }

    @Override
    public Optional<ItemStack> titanfabric$getSelectedArrow() {
        ItemStack selectedArrowStack = this.dataTracker.get(SELECTED_ARROW);
        if (selectedArrowStack.isEmpty()) return Optional.empty();
        return Optional.of(selectedArrowStack);
    }

    @Override
    public void titanfabric$setSelectedArrow(@Nullable ItemStack selectedArrowStack) {
        ItemStack stack = null;
        if (selectedArrowStack != null) stack = selectedArrowStack;
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!(player.getMainHandStack().getItem() instanceof SelectableArrows)) stack = null;
        this.dataTracker.set(SELECTED_ARROW, Objects.requireNonNullElse(stack, ItemStack.EMPTY));
    }

    @Inject(method = "getArrowType", at = @At("HEAD"), cancellable = true)
    private void titanfabric$handleArrowSelection(ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (!(stack.getItem() instanceof SelectableArrows bowItem)) return;
        PlayerEntity player = (PlayerEntity) (Object) this;
        ArrowSelectionHandler arrowSelection = (ArrowSelectionHandler) player;
        arrowSelection.titanfabric$getSelectedArrow().ifPresent(selectedArrowStack -> {
            // if (ArrowSelectionHelper.containsArrowStack(selectedArrowStack, player.getInventory(), bowItem)) {
            cir.setReturnValue(selectedArrowStack);
            // }
        });
    }

    @Inject(method = "damage", at = @At(value = "TAIL", shift = Shift.BEFORE), cancellable = true)
    private void titanfabric$damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (amount > 0.00001f && (this.timeUntilRegen <= 10 || amount > this.lastDamageTaken)) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            List<Item> armorItems = ArmorHelper.getArmorItems(player);
            if ((source == DamageSource.HOT_FLOOR || source == DamageSource.LAVA || source.isFire())) {
                int netherArmorCount = (int) armorItems.stream().filter(item -> item instanceof EmberArmorItem).count();
                if (netherArmorCount > 0) {

                    if (netherArmorCount == 4) {
                        cir.setReturnValue(false);
                    } else {
                        if (EffectHelper.shouldEffectApply(player.getWorld().getRandom(), netherArmorCount)) {
                            this.timeUntilRegen = 20;
                            this.lastDamageTaken = amount;
                            cir.setReturnValue(false);
                        }
                    }
                }
            } else if (source.equals(DamageSource.WITHER) || source.equals(DamageSource.MAGIC)) {
                int citrinArmorCount = Math.min(4, (int) armorItems.stream().filter(item -> item instanceof CitrinArmorItem).count());
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
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getKnockback(Lnet/minecraft/entity/LivingEntity;)I"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMovementSpeed()F")
            )
    )
    private float titanfabric$critChanges(float constant) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() instanceof TitanFabricSwordItem titanFabricSwordItem) {
            return titanFabricSwordItem.getCritMultiplier();
        } else if (stack.getItem() instanceof SwordItem) {
            return 1.2f;
        }
        return constant;
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
            cooldown = titanFabricSwordItem.getCooldownTicks();
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

    // Can be used to show the indicator for entity in range
    // @Inject(method = "getAttackCooldownProgress", at = @At("HEAD"), cancellable = true)
    // private void titanfabric$getAttackCooldownProgressMixin(float baseTime,CallbackInfoReturnable<Float> cir) {
    // if (!this.getWorld().getGameRules().getBoolean(TitanFabricGamerules.DO_HIT_COOLDOWN)) {
    // cir.setReturnValue(1.0f);
    // }
    // }

    @Override
    public void setOnFireFor(int seconds) {
        if (seconds > 0) {
            int netherArmorCount = ArmorHelper.getNetherArmorCount((PlayerEntity) (Object) this);
            if (netherArmorCount > 0) {
                seconds = netherArmorCount == 4 ? 0 : (int) (seconds * (float) netherArmorCount * 0.25f);
            }
        }
        super.setOnFireFor(seconds);
    }

    @Inject(method = "damageShield", at = @At("HEAD"))
    private void titanfabric$damageShield(float amount, CallbackInfo ci) {
        if (this.activeItemStack.getItem() instanceof TitanFabricShieldItem) {
            if (!this.world.isClient()) {
                this.incrementStat(Stats.USED.getOrCreateStat(this.activeItemStack.getItem()));
            }

            if (amount >= 3.0F) {
                int i = 1 + MathHelper.floor(amount);
                Hand hand = this.getActiveHand();
                this.activeItemStack.damage(i, this, (Consumer<LivingEntity>) ((playerEntity) -> playerEntity.sendToolBreakStatus(hand)));
                if (this.activeItemStack.isEmpty()) {
                    if (hand == Hand.MAIN_HAND) {
                        this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    this.activeItemStack = ItemStack.EMPTY;
                    this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
                }
            }
        }
    }

    @Inject(method = "disableShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V"))
    public void titanfabric$disableNeMuelchShield(boolean sprinting, CallbackInfo ci) {
        this.getItemCooldownManager().set(TitanFabricItems.DIAMOND_SHIELD.asItem(), 90);
        this.getItemCooldownManager().set(TitanFabricItems.LEGEND_SHIELD.asItem(), 90);
    }

    @Inject(method = "getArrowType", at = @At("HEAD"), cancellable = true)
    private void titanfabric$arrowSelection(ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (!((Object) this instanceof ServerPlayerEntity serverPlayerEntity))
            return;
        if (serverPlayerEntity.getAbilities().creativeMode)
            return;
        if (!(stack.getItem() instanceof SelectableArrows weaponWithSelectableArrows))
            return;

        Predicate<ItemStack> predicate = possibleArrowStack -> weaponWithSelectableArrows.supportedArrows().contains(possibleArrowStack.getItem());
        ItemStack itemStack = RangedWeaponItem.getHeldProjectile(serverPlayerEntity, predicate);
        if (!itemStack.isEmpty()) {
            cir.setReturnValue(itemStack);
            return;
        }

        for (int i = 0; i < inventory.size(); i++) {

        }
    }

    // From AdventureZ
    @Environment(EnvType.CLIENT)
    @Override
    public boolean doesRenderOnFire() {
        return super.doesRenderOnFire() && (int) ArmorHelper.getArmorItems((PlayerEntity) (Object) this).stream().filter(item -> item instanceof EmberArmorItem).count() < 4;
    }
}
