package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.shirojr.titanfabric.render.renderer.ArrowSelectionHudRenderer;
import net.shirojr.titanfabric.render.renderer.FrostburnHudRenderer;
import net.shirojr.titanfabric.render.renderer.RecoveryBufferHudRenderer;

public class HudEvent {
    public static void register() {
        HudRenderCallback.EVENT.register(new ArrowSelectionHudRenderer());
        HudRenderCallback.EVENT.register(FrostburnHudRenderer.getInstance());
        HudRenderCallback.EVENT.register(new RecoveryBufferHudRenderer());
    }
}
