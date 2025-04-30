package net.shirojr.titanfabric.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.gamerule.TitanFabricGamerules;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricShieldItem;
import net.shirojr.titanfabric.item.custom.TitanFabricSwordItem;
import net.shirojr.titanfabric.item.custom.armor.CitrinArmorItem;
import net.shirojr.titanfabric.item.custom.armor.EmberArmorItem;
import net.shirojr.titanfabric.item.custom.material.TitanFabricToolMaterials;
import net.shirojr.titanfabric.util.effects.ArmorPlateType;
import net.shirojr.titanfabric.util.effects.ArmorPlatingHelper;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.handler.ArrowSelectionHandler;
import net.shirojr.titanfabric.util.handler.ArrowShootingHandler;
import net.shirojr.titanfabric.util.items.ArmorHelper;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.SelectableArrows;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Debug(export = true)
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ArrowSelectionHandler, ArrowShootingHandler {
    @Unique
    private static final TrackedData<Integer> SELECTED_ARROW = DataTracker.registerData(PlayerEntityMixin.class, TrackedDataHandlerRegistry.INTEGER);
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
    @Final
    private PlayerInventory inventory;

    @Shadow
    public abstract void remove(RemovalReason reason);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void titanfabric$appendSelectedArrowDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(SELECTED_ARROW, -1);
        this.dataTracker.startTracking(SHOOTING_ARROWS, false);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    private void titanfabric$readNbt(NbtCompound nbt, CallbackInfo ci) {
        titanfabric$shootsArrows(nbt.getBoolean("shoots_arrows"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void titanfabric$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("shoots_arrows", titanfabric$isShootingArrows());
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float damage(float amount, DamageSource source) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if(!player.getWorld().isClient()) {
            if(source != null && source.getSource() != null && source.getSource() instanceof LivingEntity attacker) {
                ItemStack mainIs = attacker.getMainHandStack();

                if(mainIs != null && mainIs.getItem() instanceof ToolItem) {
                    ToolMaterial toolMaterial = ((ToolItem)mainIs.getItem()).getMaterial();

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
                        if(armorProb > 0) {
                            float damageReduction = 0.025f * armorProb; //2.5%
                            return amount * (1.0f - damageReduction);
                        }
                    }
                }
            }
        }
        return amount;
    }

    @Override
    public Optional<Integer> titanfabric$getSelectedArrowIndex() {
        int selectedArrowStackIndex = this.dataTracker.get(SELECTED_ARROW);
        if (selectedArrowStackIndex == -1) return Optional.empty();
        return Optional.of(selectedArrowStackIndex);
    }

    @Override
    public void titanfabric$setSelectedArrowIndex(@Nullable ItemStack selectedArrowStack) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = null;
        if (selectedArrowStack != null) stack = selectedArrowStack;
        boolean isInMainHand = player.getMainHandStack().getItem() instanceof SelectableArrows;
        boolean isInOffHand = player.getOffHandStack().getItem() instanceof SelectableArrows;
        if (!isInMainHand && !isInOffHand) stack = null;
        int newArrowStackIndex = getIndexOfArrowStack(player.getInventory(), stack);
        this.dataTracker.set(SELECTED_ARROW, newArrowStackIndex);
    }

    @Override
    public boolean titanfabric$isShootingArrows() {
        return this.dataTracker.get(SHOOTING_ARROWS);
    }

    @Override
    public void titanfabric$shootsArrows(boolean shootsArrows) {
        this.dataTracker.set(SHOOTING_ARROWS, shootsArrows);
    }

    @Inject(method = "getArrowType", at = @At("HEAD"), cancellable = true)
    private void titanfabric$handleArrowSelection(ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (!(stack.getItem() instanceof SelectableArrows bowItem)) return;
        PlayerEntity player = (PlayerEntity) (Object) this;
        ArrowSelectionHandler arrowSelection = (ArrowSelectionHandler) player;

        arrowSelection.titanfabric$getSelectedArrowIndex().ifPresentOrElse(itemStackIndex ->
                cir.setReturnValue(player.getInventory().getStack(itemStackIndex)), () -> {
            List<ItemStack> possibleArrowStacks = ArrowSelectionHelper.findAllSupportedArrowStacks(player.getInventory(), bowItem);
            ItemStack backupStack = ItemStack.EMPTY;
            if (!possibleArrowStacks.isEmpty()) {
                backupStack = possibleArrowStacks.get(0);
                arrowSelection.titanfabric$setSelectedArrowIndex(backupStack);
            } else {
                arrowSelection.titanfabric$setSelectedArrowIndex(null);
            }
            cir.setReturnValue(backupStack);
        });
    }

    @Unique
    private static int getIndexOfArrowStack(Inventory inventory, ItemStack stack) {
        if (stack == null) return -1;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stackInList = inventory.getStack(i);
            if (!stackInList.equals(stack)) continue;
            return i;
        }
        return -1;
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
        if (!(this.activeItemStack.getItem() instanceof TitanFabricShieldItem)) return;
        if (amount < 3.0f) return;
        if (!this.world.isClient()) this.incrementStat(Stats.USED.getOrCreateStat(this.activeItemStack.getItem()));
        int i = 1 + MathHelper.floor(amount);
        Hand hand = this.getActiveHand();
        this.activeItemStack.damage(i, this, (Consumer<LivingEntity>) ((playerEntity) -> playerEntity.sendToolBreakStatus(hand)));
        if (!this.activeItemStack.isEmpty()) return;

        if (hand == Hand.MAIN_HAND) this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        else this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        this.activeItemStack = ItemStack.EMPTY;
        this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);

    }

    @Inject(method = "disableShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V"))
    public void titanfabric$disableNeMuelchShield(boolean sprinting, CallbackInfo ci) {
        this.getItemCooldownManager().set(TitanFabricItems.DIAMOND_SHIELD.asItem(), 90);
        this.getItemCooldownManager().set(TitanFabricItems.LEGEND_SHIELD.asItem(), 90);
    }

    @Inject(method = "getArrowType", at = @At("HEAD"), cancellable = true)
    private void titanfabric$arrowSelection(ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (!((PlayerEntity) (Object) this instanceof ServerPlayerEntity serverPlayerEntity)) return;
        if (serverPlayerEntity.getAbilities().creativeMode) return;
        if (!(stack.getItem() instanceof SelectableArrows weaponWithSelectableArrows)) return;

        Predicate<ItemStack> predicate = possibleArrowStack -> weaponWithSelectableArrows.titanFabric$supportedArrows().contains(possibleArrowStack.getItem());
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
