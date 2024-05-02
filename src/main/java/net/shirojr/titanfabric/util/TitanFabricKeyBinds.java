package net.shirojr.titanfabric.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.shirojr.titanfabric.registry.KeyBindRegistry;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("SameParameterValue")
public class TitanFabricKeyBinds {
    public static KeyBinding ARROW_SELECTION_KEY = registerKeyBinding("key.titanfabric.arrow.selection",
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "keys.titanfabric.misc");

    private static KeyBinding registerKeyBinding(String translation, InputUtil.Type type, int keyCode, String group) {
        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(translation, type, keyCode, group));
        KeyBindRegistry.getInstance().register(keyBinding);
        return keyBinding;
    }

    public static void register() {
        LoggerUtil.devLogger("Initialized KeyBind registration");
    }
}
