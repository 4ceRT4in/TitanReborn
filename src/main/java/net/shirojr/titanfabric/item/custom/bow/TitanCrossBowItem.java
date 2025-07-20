package net.shirojr.titanfabric.item.custom.bow;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.shirojr.titanfabric.util.items.Anvilable;
import net.shirojr.titanfabric.util.items.SelectableArrow;

import java.util.List;
import java.util.function.Predicate;

public class TitanCrossBowItem extends CrossbowItem implements SelectableArrow, Anvilable {
    public static final int RANGE = 8;

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

    private static float getSpeed(ChargedProjectilesComponent stack) {
        return stack.contains(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
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

    private static boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        List<ItemStack> list = load(crossbow, shooter.getProjectileType(crossbow), shooter);
        if (!list.isEmpty()) {
            crossbow.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));
            return true;
        }
        return false;
    }

    public static List<ItemStack> getLoadedProjectiles(ItemStack stack) {
        ChargedProjectilesComponent component = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (component == null) return List.of();
        return component.getProjectiles();
    }

    @Override
    public int getRange() {
        return RANGE;
    }
}
