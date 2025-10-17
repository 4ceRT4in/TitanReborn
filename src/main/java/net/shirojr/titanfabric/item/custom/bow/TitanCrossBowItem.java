package net.shirojr.titanfabric.item.custom.bow;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.shirojr.titanfabric.color.TitanFabricDyeProviders;
import net.shirojr.titanfabric.config.TitanConfig;
import net.shirojr.titanfabric.util.items.Anvilable;
import net.shirojr.titanfabric.util.items.SelectableArrow;

import java.util.List;
import java.util.function.Predicate;

public class TitanCrossBowItem extends CrossbowItem implements SelectableArrow, Anvilable {
    public TitanCrossBowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public List<Item> titanFabric$supportedArrows() {
        return List.of(Items.ARROW, Items.SPECTRAL_ARROW, Items.LINGERING_POTION, Items.SPLASH_POTION);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> stack.isIn(ItemTags.ARROWS) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.SPLASH_POTION);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float f = Math.min(i / (float) getPullTime(stack, user), 1);
        if (f >= 1.0f && !isCharged(stack) && loadProjectiles(user, stack)) {
            SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, soundCategory, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        TitanFabricDyeProviders.applyExtendedTooltip(tooltip, "tooltip.titanfabric.legendCrossBowKeybind");
    }

    private static boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        List<ItemStack> list = load(crossbow, shooter.getProjectileType(crossbow), shooter);
        if (!list.isEmpty()) {
            crossbow.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));
            return true;
        }
        return false;
    }

    public static List<ItemStack> getLoadedProjectiles(ItemStack stack) {
        ChargedProjectilesComponent component = stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
        return component.getProjectiles();
    }

    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        Item projectileItem = projectileStack.getItem();
        if (projectileItem instanceof SplashPotionItem || projectileItem instanceof LingeringPotionItem) {
            PotionEntity potionEntity = new PotionEntity(world, shooter);
            potionEntity.setItem(projectileStack);
            return potionEntity;
        }
        return super.createArrowEntity(world, shooter, weaponStack, projectileStack, critical);
    }

    public static float getPotionSpeed() {
        return TitanConfig.getCrossbowPotionProjectileSpeed();
    }
}
