package net.shirojr.titanfabric.mixin;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.entity.attribute.ExtendedEntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwordItem.class)
public abstract class SwordItemMixin extends ToolItem {
    @Unique
    private static final Identifier BASE_CRIT_MODIFIER_ID = TitanFabric.getId("base_crit_modifier");

    public SwordItemMixin(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Inject(method = "createAttributeModifiers", at = @At("RETURN"), cancellable = true)
    private static void createAttributeModifiers(ToolMaterial material, int baseAttackDamage, float attackSpeed, CallbackInfoReturnable<AttributeModifiersComponent> cir) {
        AttributeModifiersComponent original = cir.getReturnValue();
        AttributeModifiersComponent result = original.with(
                ExtendedEntityAttributes.GENERIC_CRIT_MODIFIER,
                new EntityAttributeModifier(BASE_CRIT_MODIFIER_ID, 1.2, EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.MAINHAND
        );
        cir.setReturnValue(result);

    }
}
