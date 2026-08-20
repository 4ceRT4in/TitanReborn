package net.shirojr.titanfabric.item.custom.spear;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.TridentItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.shirojr.titanfabric.entity.SpearEntity;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.entity.attribute.ExtendedEntityAttributes;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.init.TitanFabricItems;
import net.shirojr.titanfabric.util.effects.EffectHelper;
import net.shirojr.titanfabric.util.effects.WeaponEffect;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;
import net.shirojr.titanfabric.util.effects.WeaponEffectType;
import net.shirojr.titanfabric.util.items.Anvilable;
import net.shirojr.titanfabric.util.items.WeaponEffectCrafting;
import net.shirojr.titanfabric.util.items.ToolTipHelper;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class TitanFabricSpearItem extends TridentItem implements WeaponEffectCrafting, Anvilable {
    public static final int MIN_THROW_CHARGE = 10;
    private static final Identifier BASE_CRIT_MODIFIER_ID = TitanFabric.getId("base_crit_modifier");
    private final SpearTier tier;

    public TitanFabricSpearItem(SpearTier tier, Item.Settings settings) {
        super(settings(tier, settings));
        this.tier = tier;
    }

    private static Item.Settings settings(SpearTier tier, Item.Settings settings) {
        settings.maxCount(1)
                .maxDamage(Math.max(1, tier.durability()))
                .component(DataComponentTypes.TOOL, TridentItem.createToolComponent())
                .attributeModifiers(attributes(tier.material(), tier.damage()));
        if (tier.durability() <= 0) {
            settings.component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true));
        }
        if (tier.innateEffect() != null) {
            settings.component(TitanFabricDataComponents.WEAPON_EFFECTS, new HashSet<>(Set.of(
                    new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, tier.innateEffect(), 2)
            )));
        }
        return settings;
    }

    private static AttributeModifiersComponent attributes(ToolMaterial material, int damage) {
        return AttributeModifiersComponent.builder()
                // Attribute tooltips include the player's intrinsic one attack-damage point.
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, damage - 1.0 + material.getAttackDamage(), EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -3.2, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(ExtendedEntityAttributes.GENERIC_CRIT_MODIFIER, new EntityAttributeModifier(BASE_CRIT_MODIFIER_ID, 1.2, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .build();
    }

    @Override public int getEnchantability() { return tier.material().getEnchantability(); }
    @Override public UseAction getUseAction(ItemStack stack) { return UseAction.SPEAR; }
    @Override public int getMaxUseTime(ItemStack stack, LivingEntity user) { return 72000; }
    public SpearTier getTier() { return tier; }

    public static boolean isThrowLocked(PlayerEntity player, ItemStack stack) {
        return stack.getItem() instanceof TitanFabricSpearItem && hasSpearCooldown(player);
    }

    public static boolean hasSpearCooldown(PlayerEntity player) {
        for (Item item : TitanFabricItems.ALL_ITEMS) {
            if (item instanceof TitanFabricSpearItem && player.getItemCooldownManager().isCoolingDown(item)) return true;
        }
        return false;
    }

    public static void setSpearCooldown(PlayerEntity player, int ticks) {
        for (Item item : TitanFabricItems.ALL_ITEMS) {
            if (item instanceof TitanFabricSpearItem) player.getItemCooldownManager().set(item, ticks);
        }
    }

    public static void clearSpearCooldown(PlayerEntity player) {
        for (Item item : TitanFabricItems.ALL_ITEMS) {
            if (item instanceof TitanFabricSpearItem) player.getItemCooldownManager().remove(item);
        }
    }

    @Override public List<WeaponEffect> supportedEffects() { return tier.supportedEffects(); }
    @Override public WeaponEffectData getBaseEffect() { return tier.innateEffect() == null ? null : new WeaponEffectData(WeaponEffectType.INNATE_EFFECT, tier.innateEffect(), 2); }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        if (tier.innateEffect() != null) EffectHelper.applyEffectToStack(stack, getBaseEffect(), false);
        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (hasSpearCooldown(user) || isAboutToBreak(stack)) return TypedActionResult.fail(stack);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player) || getMaxUseTime(stack, user) - remainingUseTicks < MIN_THROW_CHARGE
                || world.isClient || hasSpearCooldown(player) || isAboutToBreak(stack)) return;
        ItemStack thrown = stack.copy();
        thrown.setCount(1);
        stack.damage(1, player, LivingEntity.getSlotForHand(player.getActiveHand()));
        SpearEntity entity = new SpearEntity(world, player, thrown, tier);
        // Calculate the direction directly from the player's rotation. The generic
        // trident helper is unreliable at the lower end of the pitch range because
        // it also mixes in the owner's movement before the projectile is spawned.
        Vec3d throwVelocity = player.getRotationVec(1.0f).normalize().multiply(2.5f);
        if (!player.isOnGround()) {
            throwVelocity = throwVelocity.add(player.getVelocity());
        }
        entity.setVelocity(throwVelocity);
        world.spawnEntity(entity);
        world.playSoundFromEntity(null, entity, SoundEvents.ITEM_TRIDENT_THROW.value(), SoundCategory.PLAYERS, 1.0f, 1.0f);
        setSpearCooldown(player, tier.throwCooldown());
    }

    private static boolean isAboutToBreak(ItemStack stack) {
        return stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage() - 1;
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy().formatted(tier.nameColor());
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        EffectHelper.applyWeaponEffectsOnTarget(target.getWorld(), stack, target);
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (tier.innateEffect() != null) {
            tooltip.add(Text.translatable("tooltip.titanfabric.spear.innate." + tier.innateEffect().getId()));
        } else if (tier == SpearTier.DIAMOND) {
            tooltip.add(Text.translatable("tooltip.titanfabric.spear.innate.crit"));
        }
        ToolTipHelper.appendSwordToolTip(tooltip, stack);
        tooltip.add(Text.empty());
        tooltip.add(Text.translatable(
                "tooltip.titanfabric.spear.cooldown",
                Text.literal(Integer.toString(tier.throwCooldown() / 20)).formatted(Formatting.WHITE)
        ).formatted(Formatting.GRAY));
        tooltip.add(Text.empty());
        super.appendTooltip(stack, context, tooltip, type);
    }

    public boolean isLegendary() {
        return this.tier == SpearTier.LEGEND;
    }
}
