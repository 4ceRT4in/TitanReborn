package net.shirojr.titanfabric.item.custom.bow;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.shirojr.titanfabric.data.BackPackContent;
import net.shirojr.titanfabric.init.TitanFabricDataComponents;
import net.shirojr.titanfabric.item.custom.TitanFabricBowItem;
import net.shirojr.titanfabric.item.custom.misc.BackPackItem;
import net.shirojr.titanfabric.util.items.FireEnchantmentBanHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class NetheriteBowItem extends TitanFabricBowItem {
    public NetheriteBowItem(Settings settings) {
        super(settings);
    }

    @Override
    public List<Item> titanFabric$supportedArrows() {
        return List.of(Items.ARROW, Items.SPECTRAL_ARROW);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> stack.isOf(Items.ARROW) || stack.isOf(Items.SPECTRAL_ARROW);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient() && user instanceof PlayerEntity player) {
            Integer bagSlot = getSelectedIndexOfBag(stack);
            if (bagSlot != null) {
                ItemStack bag = bagSlot == -1 ? player.getOffHandStack()
                        : bagSlot >= 0 && bagSlot < player.getInventory().main.size()
                        ? player.getInventory().main.get(bagSlot) : ItemStack.EMPTY;
                BackPackContent contents = bag.get(TitanFabricDataComponents.BACKPACK_CONTENT);
                if (bag.getItem() instanceof BackPackItem && contents != null) {
                    // Vanilla consumes the selected stack in place. Detach the contents first,
                    // so inventory sync snapshots keep the old count and detect the change.
                    bag.set(TitanFabricDataComponents.BACKPACK_CONTENT,
                            new BackPackContent(contents.items().stream().map(ItemStack::copy).toList()));
                }
            }
        }
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack,
                                                 ItemStack projectileStack, boolean critical) {
        // Sanitize only the projectile's weapon copy; durability must affect the real bow.
        return super.createArrowEntity(world, shooter,
                FireEnchantmentBanHelper.getSanitizedCombatStackForEffects(world, weaponStack), projectileStack, critical);
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed,
                         float divergence, float yaw, @Nullable LivingEntity target) {
        // BowItem supplies the vanilla charge curve and RangedWeaponItem handles enchantments,
        // consumption, critical hits and durability. Only the launch speed changes.
        super.shoot(shooter, projectile, index, speed * 1.25F, divergence, yaw, target);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("tooltip.titanfabric.MultiBowKeybind"));
    }

    public static boolean isNetheriteBowArrow(DamageSource source) {
        if (!(source.getSource() instanceof PersistentProjectileEntity arrow)) return false;
        // Vanilla copies the firing weapon into the projectile and saves/loads it as NBT.
        ItemStack weapon = arrow.getWeaponStack();
        return weapon != null && weapon.getItem() instanceof NetheriteBowItem;
    }
}
