package net.shirojr.titanfabric.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectUtil.class)
public abstract class StatusEffectUtilMixin {

    @Inject(method = "durationToString", at = @At("HEAD"), cancellable = true)
    private static void modifyDurationToString(StatusEffectInstance effect, float multiplier, CallbackInfoReturnable<String> cir) {
        // Get the player instance from the Minecraft client
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Check if the player is wearing a full Netherite armor set
        if (effect.getEffectType() == StatusEffects.RESISTANCE && hasFullNetheriteArmor(client.player)) {
            // If so, set the display duration to "∞" (or "Infinite")
            cir.setReturnValue("∞");
        }
    }

    // Helper method to check if the player has a full Netherite armor set equipped
    @Unique
    private static boolean hasFullNetheriteArmor(PlayerEntity player) {
        // Check each armor slot for Netherite armor
        return player.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.NETHERITE_HELMET &&
                player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.NETHERITE_CHESTPLATE &&
                player.getEquippedStack(EquipmentSlot.LEGS).getItem() == Items.NETHERITE_LEGGINGS &&
                player.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.NETHERITE_BOOTS;
    }
}

