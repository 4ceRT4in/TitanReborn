package net.shirojr.titanfabric.item.custom.armor.parts;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import net.shirojr.titanfabric.item.TitanFabricItemGroups;
import net.shirojr.titanfabric.item.custom.armor.LegendArmorItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class LegendArmorLeggingsItem extends LegendArmorItem {
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public LegendArmorLeggingsItem(float health) {
        super(EquipmentSlot.LEGS, new FabricItemSettings().group(TitanFabricItemGroups.TITAN), health);

        UUID uUID = MODIFIERS[slot.getEntitySlotId()];
        UUID GENERIC_MAX_HEALTH_ID = UUID.fromString("5D6F0BA2-1186-46AC-B896-C61C5CEE99CE");

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(GENERIC_MAX_HEALTH_ID, "Leggings Health modifier", health, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uUID, "Armor modifier", this.getProtection(), EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(uUID, "Armor toughness", this.getToughness(), EntityAttributeModifier.Operation.ADDITION));

        this.attributeModifiers = builder.build();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("tooltip.titanfabric.legendLeggingsItem"));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.LEGS) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }
}
