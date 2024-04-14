package net.shirojr.titanfabric.item.custom.armor;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.shirojr.titanfabric.item.custom.material.TitanFabricArmorMaterials;
import net.shirojr.titanfabric.util.items.Anvilable;

import java.util.UUID;

public class LegendArmorItem extends ArmorItem implements Anvilable {
    private final float healthValue;
    protected static final UUID[] MODIFIERS = new UUID[]{
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
    };

    public LegendArmorItem(EquipmentSlot slot, Settings settings, float healthValue) {
        super(TitanFabricArmorMaterials.LEGEND, slot, settings);
        this.healthValue = healthValue;
    }

    public float getHealthValue() {
        return healthValue;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}
