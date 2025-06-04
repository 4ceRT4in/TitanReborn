package net.shirojr.titanfabric.event.custom;

import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import net.shirojr.titanfabric.data.PotionBundleContent;

public class ToolTipComponentEvent {
    public static void register() {
        TooltipComponentCallback.EVENT.register(ToolTipComponentEvent::handleComponents);
    }

    private static TooltipComponent handleComponents(TooltipData tooltipData) {
        if (tooltipData instanceof PotionBundleContent.ToolTipData potionBundleToolTipData) {
            return new PotionBundleContent.ToolTipComponent(potionBundleToolTipData.content());
        }
        return null;
    }
}
