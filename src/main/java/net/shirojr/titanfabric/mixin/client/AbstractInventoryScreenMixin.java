package net.shirojr.titanfabric.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
//import net.shirojr.titanfabric.item.custom.armor.NetheriteArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
@Mixin(AbstractInventoryScreen.class)
public abstract class AbstractInventoryScreenMixin<T extends ScreenHandler> extends HandledScreen<T> {
    public AbstractInventoryScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    // @Redirect(method = "drawStatusEffectDescriptions", at = @At(value = "INVOKE",
    //         target = "Lnet/minecraft/entity/effect/StatusEffectUtil;durationToString(Lnet/minecraft/entity/effect/StatusEffectInstance;F)Ljava/lang/String;"))
    // private String drawStatusEffectDescriptions(StatusEffectInstance effect, float multiplier) {
    //     if (client == null || client.player == null) return null;
    //     PlayerEntity player = client.player;

    //     List<Item> armorSet = IntStream.rangeClosed(0, 3)
    //             .mapToObj(player.getInventory()::getArmorStack)
    //             .map(ItemStack::getItem).toList();

    //     if (armorSet.stream().allMatch(item -> item instanceof NetheriteArmorItem) &&
    //             effect.getEffectType() == StatusEffects.RESISTANCE) {

    //         //TODO: implement as translatable text!!!
    //         // new TranslatableText("overlay.titanfabric.netherite.resistance").asString()
    //         return "Active";
    //     }

    //     return StatusEffectUtil.durationToString(effect, multiplier);
    // }
}
