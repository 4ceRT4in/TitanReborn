package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.item.custom.armor.NetherArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.IntStream;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "setFireTicks", at = @At("HEAD"))
    private void reducedTicks(int fireTicks, CallbackInfo ci) {
        //TODO: test for nether amror
        PlayerEntity player = (PlayerEntity) (Object) this;

        List<Item> armorSet = IntStream.rangeClosed(0, 3)
                .mapToObj(player.getInventory()::getArmorStack)
                .map(ItemStack::getItem).toList();

        if (armorSet.stream().allMatch(item -> item instanceof NetherArmorItem)) {
            fireTicks = 0;
        }

        for (Item armorItem : armorSet) {
            if (armorItem instanceof NetherArmorItem) {
                fireTicks /= 4;
            }

        }
    }
}
