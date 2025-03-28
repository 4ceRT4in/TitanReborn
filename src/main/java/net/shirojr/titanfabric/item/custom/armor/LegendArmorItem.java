package net.shirojr.titanfabric.item.custom.armor;

import com.google.common.base.Suppliers;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.init.TitanFabricArmorMaterials;
import net.shirojr.titanfabric.util.items.Anvilable;

import java.util.List;
import java.util.function.Supplier;

public class LegendArmorItem extends ArmorItem implements Anvilable {
    private final float extraHealth;
    private final Supplier<AttributeModifiersComponent> attributeModifiers;

    public LegendArmorItem(Type type, Settings settings, float extraHealth) {
        super(TitanFabricArmorMaterials.LEGEND.getRegistryEntry(), type,
                settings.maxDamage(TitanFabricArmorMaterials.LEGEND.getDurability(type)));
        this.extraHealth = extraHealth;

        this.attributeModifiers = Suppliers.memoize(() -> {
            Identifier identifier = TitanFabric.getId("armor." + getSlotType().getName());
            AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
            AttributeModifierSlot slot = AttributeModifierSlot.forEquipmentSlot(this.getSlotType());

            builder.add(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(identifier, this.getExtraValue(), EntityAttributeModifier.Operation.ADD_VALUE), slot);
            builder.add(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(identifier, this.getProtection(), EntityAttributeModifier.Operation.ADD_VALUE), slot);
            builder.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(identifier, this.getToughness(), EntityAttributeModifier.Operation.ADD_VALUE), slot);
            return builder.build();
        });
    }

    public float getExtraValue() {
        return extraHealth;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers() {
        return this.attributeModifiers.get();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.titanfabric.legendArmorHealth", this.getExtraValue()));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
