package net.shirojr.titanfabric.item.custom;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.entity.attribute.ExtendedEntityAttributes;
import net.shirojr.titanfabric.util.SwordType;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.items.Anvilable;
import net.shirojr.titanfabric.util.items.ToolTipHelper;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TitanFabricSwordItem extends SwordItem implements WeaponEffectCrafting, Anvilable {
    protected final boolean canHaveWeaponEffects;
    private final WeaponEffect baseEffect;
    private final SwordType swordType;
    private static final Identifier BASE_CRIT_MODIFIER_ID = TitanFabric.getId("base_crit_modifier");

    public TitanFabricSwordItem(boolean canHaveWeaponEffects, ToolMaterial toolMaterial, int attackDamage,
                                float attackSpeed, SwordType swordType, WeaponEffect baseEffect, Item.Settings settings) {
        super(toolMaterial, settings.attributeModifiers(createAttributeModifiers(toolMaterial, attackDamage, attackSpeed, swordType.getCritMultiplier())));
        this.canHaveWeaponEffects = canHaveWeaponEffects;
        this.baseEffect = baseEffect;
        this.swordType = swordType;
    }

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, int baseAttackDamage, float attackSpeed, float critModifier) {
        return AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, (double)((float)baseAttackDamage + material.getAttackDamage()), EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND).add(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, (double)attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(ExtendedEntityAttributes.GENERIC_CRIT_MODIFIER,
                        new EntityAttributeModifier(BASE_CRIT_MODIFIER_ID, critModifier, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND).build();
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        if (this.getBaseEffect() != null) {
            EffectHelper.applyEffectToStack(stack, this.getBaseEffect(), false);
        }
        return stack;
    }

    public boolean canHaveWeaponEffects() {
        return this.canHaveWeaponEffects;
    }

    public float getCritMultiplier() {
        return this.swordType.getCritMultiplier();
    }

    public int getCooldownTicks() {
        return this.swordType.getCooldownTicks();
    }

    @Nullable
    @Override
    public WeaponEffectData getBaseEffect() {
        if (this.baseEffect == null) return null;
        return new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, this.baseEffect, 1);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        ToolTipHelper.appendSwordToolTip(tooltip, stack);
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        EffectHelper.applyWeaponEffectsOnTarget(target.getWorld(), stack, target);

        return super.postHit(stack, target, attacker);
    }

    public SwordType getSwordType() {
        return this.swordType;
    }
}
