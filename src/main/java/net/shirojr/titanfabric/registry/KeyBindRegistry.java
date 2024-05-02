package net.shirojr.titanfabric.registry;

import net.minecraft.client.option.KeyBinding;

import java.util.HashMap;
import java.util.Map;

public class KeyBindRegistry {
    private static KeyBindRegistry instance;
    private final Map<KeyBinding, Boolean> registry = new HashMap<>();

    private KeyBindRegistry() {
    }

    public static KeyBindRegistry getInstance() {
        if (instance == null) instance = new KeyBindRegistry();
        return instance;
    }

    public void register(KeyBinding keyBind) {
        registry.put(keyBind, false);
    }

    public boolean isRegistered(KeyBinding keyBinding) {
        return registry.containsKey(keyBinding);
    }

    public boolean wasPressed(KeyBinding keyBinding) {
        return isRegistered(keyBinding) && registry.get(keyBinding);
    }

    public void setPressed(KeyBinding keyBind, boolean wasPressed) {
        if (!isRegistered(keyBind)) return;
        registry.put(keyBind, wasPressed);
    }
}
