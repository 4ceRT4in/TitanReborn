package net.shirojr.titanfabric.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.item.custom.TitanFabricParachuteItem;

@Environment(EnvType.CLIENT)
@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> {

    @Shadow
    @Mutable
    @Final
    public ModelPart rightArm;
    @Shadow
    @Mutable
    @Final
    public ModelPart leftArm;
    @Shadow
    @Mutable
    @Final
    public ModelPart rightLeg;
    @Shadow
    @Mutable
    @Final
    public ModelPart leftLeg;
    @Unique
    private float waveLegs = 0.0f;

    @Inject(method = "setAngles", at = @At("TAIL"))
    private void setAnglesMixin(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo info) {
        if (livingEntity instanceof PlayerEntity player) {
            if (player.getMainHandStack().isOf(TitanFabricItems.PARACHUTE) && TitanFabricParachuteItem.isParachuteActivated(player)) {
                this.leftLeg.pitch = 0.0f;
                this.rightLeg.pitch = 0.0f;

                this.waveLegs += 0.01f;
                this.leftLeg.roll = -0.2f + MathHelper.cos(waveLegs) * 0.02f;
                this.rightLeg.roll = 0.2f + MathHelper.cos(waveLegs) * 0.02f;

                this.rightArm.pitch = 3.14f;
                this.leftArm.pitch = 3.14f;
                // If arm spreading is wanted
                // this.rightArm.roll = -0.7f;
                // this.leftArm.roll = 0.7f;
            }
        }
    }

}
