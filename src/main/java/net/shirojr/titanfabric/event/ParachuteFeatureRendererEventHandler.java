package net.shirojr.titanfabric.event;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.shirojr.titanfabric.feature.TitanFabricParachuteFeatureRenderer;
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
            if (!(entityRenderer instanceof PlayerEntityRenderer)) return; //TODO: could be PlayerEntityModel according to docs?

            //registrationHelper.register(new TitanFabricParachuteFeatureRenderer<>(entityRenderer, context.getModelLoader()));
        });
    }
}
