package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import net.shirojr.titanfabric.effect.ImmunityEffect;
import net.shirojr.titanfabric.init.TitanFabricGamerules;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import net.shirojr.titanfabric.util.effects.OverpoweredEnchantmentsHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements FabricItemStack {

    @Inject(method = "getTooltip", at = @At("RETURN"))
    public void getTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        if (stack == null) return;
        Item item = stack.getItem();
        if (item == TitanFabricItems.LEGEND_SWORD || item == TitanFabricItems.LEGEND_GREATSWORD || item == TitanFabricItems.LEGEND_HELMET ||
                item == TitanFabricItems.LEGEND_CHESTPLATE || item == TitanFabricItems.LEGEND_LEGGINGS || item == TitanFabricItems.LEGEND_BOOTS ||
                item == TitanFabricItems.TITAN_CROSSBOW || item == TitanFabricItems.LEGEND_BOW || item == TitanFabricItems.LEGEND_SHIELD) {
            cir.getReturnValue().removeIf(text -> text.getString().equals(Text.translatable("item.unbreakable").formatted(Formatting.BLUE).getString()));
        }
    }

    @Inject(method = "appendAttributeModifierTooltip", at = @At("HEAD"), cancellable = true)
    private void appendAttributeModifierTooltip(Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, CallbackInfo ci) {
        String name = modifier.toString();
        if (name.contains("crit")) {
            double d = modifier.value();
            textConsumer.accept(ScreenTexts.space().append(Text.translatable(
                    "attribute.modifier.equals." + modifier.operation().getId(),
                    AttributeModifiersComponent.DECIMAL_FORMAT.format(d),
                    Text.translatable(attribute.value().getTranslationKey())
            ).formatted(Formatting.DARK_GREEN)));
            ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "damage(ILnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamageable()Z"))
    private boolean titanfabric$avoidDamage(boolean original, @Local(argsOnly = true) ServerPlayerEntity player) {
        if (player == null) return original;
        if (player.hasStatusEffect(TitanFabricStatusEffects.INDESTRUCTIBILITY)) {
            return false;
        }
        var stack = (ItemStack) (Object) this;
        if(stack.getMaxDamage() <= 0) return false;
        return original;
    }

    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void getRarity(CallbackInfoReturnable<Rarity> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if(OverpoweredEnchantmentsHelper.isOverpowered(stack)) {
            cir.setReturnValue(Rarity.EPIC);
        }
    }

    @ModifyVariable(method = "appendAttributeModifierTooltip", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private double modifyDVariable(double d, Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {
        if (!(attribute.matches(EntityAttributes.GENERIC_ATTACK_DAMAGE) && modifier.idMatches(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID))) return d;

        ItemStack stack = (ItemStack)(Object)this;

        var enchantments = stack.getEnchantments();
        var sharpnessLevel = 0;

        for (var entry : enchantments.getEnchantments()) {
            if (entry.matchesKey(Enchantments.SHARPNESS)) {
                sharpnessLevel = enchantments.getLevel(entry);
                break;
            }
        }

        if (sharpnessLevel > 0) {
            return d + sharpnessLevel;
        }

        return d;
    }

    @Inject(at = @At("HEAD"), method = "finishUsing")
    private void titanfabric$finishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> info) {
        if (((ItemStack) (Object) this).getItem() == Items.GOLDEN_APPLE && user.getEntityWorld() != null && user.getEntityWorld().getGameRules().getBoolean(TitanFabricGamerules.LEGACY_ABSORPTION)) {
            if (user.hasStatusEffect(StatusEffects.ABSORPTION)) {
                StatusEffectInstance absorptionEffect = user.getStatusEffect(StatusEffects.ABSORPTION);
                if (absorptionEffect != null) {
                    int remainingDuration = absorptionEffect.getDuration();
                    user.removeStatusEffect(StatusEffects.ABSORPTION);
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, remainingDuration, absorptionEffect.getAmplifier()));
                }
            }
        }
        if(((ItemStack) (Object) this).getItem() == Items.MILK_BUCKET) {
            ImmunityEffect.resetImmunity(user);
        }
    }
}

