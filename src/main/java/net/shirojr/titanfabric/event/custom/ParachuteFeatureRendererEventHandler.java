package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.shirojr.titanfabric.render.renderer.ParachuteFeatureRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParachuteFeatureRendererEventHandler {
    private boolean isEquiped = false;
    private @Nullable PlayerEntity player = null;

    //region getter & setter
    public boolean getEquiped() {
        return this.isEquiped;
    }

    public void setEquiped(boolean isEquiped) {
        this.isEquiped = isEquiped;
    }

    public @Nullable PlayerEntity getPlayer() {
        return this.player;
    }

    public void setPlayer(@NotNull PlayerEntity player) {
        this.player = player;
    }

    //endregion

    public static void register() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerEntityRenderer) {
                registrationHelper.register(new ParachuteFeatureRenderer(entityRenderer, context.getModelLoader()));
            }
        });
    }
}
