package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricShieldItem;
import net.shirojr.titanfabric.item.custom.armor.NetherArmorItem;
import net.shirojr.titanfabric.util.items.SelectableArrows;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract void setFireTicks(int fireTicks);

    @Shadow public abstract void incrementStat(Stat<?> stat);

    @Shadow public abstract ItemCooldownManager getItemCooldownManager();

    @Shadow @Final private PlayerInventory inventory;

    @ModifyArg(method = "setFireTicks", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setFireTicks(I)V"))
    private int titanfabric$modifyFireTicks(int fireTicks) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        List<Item> armorSet = IntStream.rangeClosed(0, 3)
                .mapToObj(player.getInventory()::getArmorStack)
                .map(ItemStack::getItem).toList();

        boolean allMatch = armorSet.stream().allMatch(item -> item instanceof NetherArmorItem);
        if (allMatch) return 0;

        if (player.getFireTicks() > fireTicks) return fireTicks;

        int itemCounter = Math.min(4, (int) armorSet.stream().filter(item -> item instanceof NetherArmorItem).count());

        if (fireTicks > 1 && itemCounter > 0) {
            float chance = (float) itemCounter / armorSet.size();
            fireTicks = (int) (fireTicks - (fireTicks * chance));
        }


        return fireTicks;
    }

    @Inject(method = "damageShield", at = @At("HEAD"))
    private void titanfabric$damageNeMuelchShield(float amount, CallbackInfo info) {
        if (this.activeItemStack.getItem() instanceof TitanFabricShieldItem) {
            if (!this.world.isClient) {
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
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        if (!(playerEntity instanceof ServerPlayerEntity serverPlayerEntity)) return;
        if (serverPlayerEntity.getAbilities().creativeMode) return;
        if (!(stack.getItem() instanceof SelectableArrows weaponWithSelectableArrows)) return;

        Predicate<ItemStack> predicate = possibleArrowStack -> weaponWithSelectableArrows.supportedArrows().contains(possibleArrowStack.getItem());
        ItemStack itemStack = RangedWeaponItem.getHeldProjectile(serverPlayerEntity, predicate);
        if (!itemStack.isEmpty()) {
            cir.setReturnValue(itemStack);
            return;
        }

        for (int i = 0; i < inventory.size(); i++) {

        }
    }
}
