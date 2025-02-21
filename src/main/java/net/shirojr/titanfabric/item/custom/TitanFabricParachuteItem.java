package net.shirojr.titanfabric.item.custom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Language;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shirojr.titanfabric.color.TitanFabricColorProviders;
import net.shirojr.titanfabric.item.TitanFabricItems;
import net.shirojr.titanfabric.sound.TitanFabricSoundHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TitanFabricParachuteItem extends Item {

    public TitanFabricParachuteItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (entity instanceof PlayerEntity playerEntity) {
            if (stack.getNbt() != null && stack.getNbt().contains("Activated")) {
                if (playerEntity.isOnGround()) {
                    playerEntity.getItemCooldownManager().remove(this);
                }
                if (stack.getNbt().getBoolean("Activated")) {
                    if (!selected && !playerEntity.getOffHandStack().getItem().equals(this)) {
                        removeParachute(stack, playerEntity);
                    }
                    if (playerEntity.isOnGround() ||
                            playerEntity.getVelocity().getY() > 0 ||
                            playerEntity.isFallFlying() ||
                            playerEntity.isTouchingWater() || playerEntity.isInLava()) {
                        NbtCompound nbtCompound = stack.getOrCreateNbt();
                        nbtCompound.putBoolean("Activated", false);
                        stack.setNbt(nbtCompound);
                    } else {
                        if(Math.random() <= 0.025) {
                            stack.damage(1, playerEntity, player -> player.sendToolBreakStatus(player.getActiveHand()));
                        }
                        Vec3d rotationVec3d = playerEntity.getRotationVector().multiply(0.01, 0, 0.01);
                        Vec3d newVec3d = playerEntity.getVelocity().add(rotationVec3d);
                        playerEntity.setVelocity(new Vec3d(MathHelper.clamp(newVec3d.getX(), -1.5D, 1.5D), newVec3d.getY(), MathHelper.clamp(newVec3d.getZ(), -1.5D, 1.5D)));
                    }
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        NbtCompound nbtCompound = user.getStackInHand(hand).getOrCreateNbt();
        if (!user.isOnGround()) {
            if (user.isSneaking()) {
                if (removeParachute(user.getStackInHand(hand), user)) return super.use(world, user, hand);
            }
            if (isParachuteActivated(user)) return super.use(world, user, hand);
            Vec3d velocity = new Vec3d(user.getVelocity().getX(), user.getVelocity().getY() * 0.05D, user.getVelocity().getZ());
            user.setVelocity(velocity);
            nbtCompound.putBoolean("Activated", true);
            user.getStackInHand(hand).setNbt(nbtCompound);
            if (world.isClient()) {
                user.playSound(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, SoundCategory.PLAYERS, 1.0f, 1.0f);
                TitanFabricSoundHandler.playParachuteSoundInstance((ClientPlayerEntity) user);
            }
        }
        return super.use(world, user, hand);

    }

    public boolean removeParachute(ItemStack stack, PlayerEntity user) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (nbtCompound.contains("Activated") && nbtCompound.getBoolean("Activated")) {
            nbtCompound.putBoolean("Activated", false);
            user.getItemCooldownManager().set(this, 80);
            stack.setNbt(nbtCompound);
            if (!user.getWorld().isClient()) {
                user.getWorld().playSound(null, user.getBlockPos(), SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1f, 0.8f);
                user.getWorld().playSound(null, user.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, SoundCategory.PLAYERS, 1f, 0.8f);
            }
            return true;
        }
        return false;
    }

    public static boolean isParachuteActivated(LivingEntity entity) {
        if (entity.getMainHandStack().isOf(TitanFabricItems.PARACHUTE)) {
            if (entity.getMainHandStack().getNbt() != null && entity.getMainHandStack().getNbt().contains("Activated") && entity.getMainHandStack().getNbt().getBoolean("Activated")) {
                return true;
            }
        } else if (entity.getOffHandStack().isOf(TitanFabricItems.PARACHUTE)) {
            if (entity.getOffHandStack().getNbt() != null && entity.getOffHandStack().getNbt().contains("Activated") && entity.getOffHandStack().getNbt().getBoolean("Activated")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText("tooltip.titanfabric.parachute_line1"));
        tooltip.add(new TranslatableText("tooltip.titanfabric.parachute_line2"));
        if (stack.hasNbt() && stack.getNbt() != null) {
            if (stack.getNbt().contains("red")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorRed"));
            } else if (stack.getNbt().contains("orange")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorOrange"));
            } else if (stack.getNbt().contains("blue")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorBlue"));
            } else if (stack.getNbt().contains("gray")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorGray"));
            } else if (stack.getNbt().contains("lime")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorLime"));
            } else if (stack.getNbt().contains("pink")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorPink"));
            } else if (stack.getNbt().contains("purple")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorPurple"));
            } else if (stack.getNbt().contains("light_blue")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorLightBlue"));
            } else if (stack.getNbt().contains("light_gray")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorLightGray"));
            } else if (stack.getNbt().contains("yellow")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorYellow"));
            } else if (stack.getNbt().contains("magenta")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorMagenta"));
            } else if (stack.getNbt().contains("cyan")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorCyan"));
            } else if (stack.getNbt().contains("brown")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorBrown"));
            } else if (stack.getNbt().contains("green")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorGreen"));
            } else if (stack.getNbt().contains("black")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorBlack"));
            } else if (stack.getNbt().contains("white")) {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorWhite"));
            } else {
                tooltip.add(new TranslatableText("tooltip.titanfabric.colorWhite"));
            }
        } else {
            tooltip.add(new TranslatableText("tooltip.titanfabric.colorWhite"));
        }
    }
}
