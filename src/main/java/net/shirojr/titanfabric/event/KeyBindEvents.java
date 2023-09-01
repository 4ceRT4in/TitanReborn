package net.shirojr.titanfabric.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.shirojr.titanfabric.item.custom.TitanFabricBowItem;
import org.lwjgl.glfw.GLFW;

public class KeyBindEvents {
    public static KeyBinding BOW_SCREEN_KEY;
    public static final String KEYBIND_GROUP = "keys.titanfabric.misc";

    public static void register() {
        BOW_SCREEN_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.titanfabric.arrow.screen.open",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, KEYBIND_GROUP
        ));

        // might relocate onClientTick to another class since it's not only used for the TitanFabricBowItem class
        // to improve readability
        ClientTickEvents.END_CLIENT_TICK.register(TitanFabricBowItem::onClientTick);
    }
}
