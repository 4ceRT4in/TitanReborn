package net.shirojr.titanfabric.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.item.custom.armor.NetherArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.IntStream;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract void setFireTicks(int fireTicks);

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

}
