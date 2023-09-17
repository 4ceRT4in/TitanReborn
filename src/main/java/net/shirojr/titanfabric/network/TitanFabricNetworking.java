package net.shirojr.titanfabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.util.items.MultiBowHelper;

public class TitanFabricNetworking {
    public static final Identifier BOW_SCREEN_CHANNEL = new Identifier(TitanFabric.MODID, "bow_screen");
    public static final Identifier MULTI_BOW_ARROWS_CHANNEL = new Identifier(TitanFabric.MODID, "shoot_multi_bow");


    private static void handleBowScreenPacket(MinecraftServer server, ServerPlayerEntity player,
                                             ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        ItemStack selectedStack = buf.readItemStack();

        server.execute(() -> {
            player.sendMessage(new LiteralText("Arrow Packet arrived on server side"), false);
            player.sendMessage(new LiteralText("Selected Arrow: " + selectedStack), false);
        });
    }

    private static void handleMultiBowShotPacket(MinecraftServer server, ServerPlayerEntity player,
                                             ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        ItemStack arrowStack = buf.readItemStack();
        double pullProgress = buf.readDouble();

        server.execute(() -> {
            World world = player.getWorld();
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
            int powerEnchantLevel = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
            int punchEnchantLevel = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
            int flameEnchantLevel = EnchantmentHelper.getLevel(Enchantments.FLAME, stack);

            if (!MultiBowHelper.handleArrowConsumption(player, stack, arrowStack)) return;

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                    1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + (float) pullProgress * 0.5f);

            PersistentProjectileEntity projectile = MultiBowHelper.prepareArrow(world, player, arrowStack,
                    player.getPitch(), player.getYaw(), pullProgress, powerEnchantLevel, punchEnchantLevel, flameEnchantLevel);

            world.spawnEntity(projectile);

            MultiBowHelper.setArrowsLeftNbt(stack, MultiBowHelper.getArrowsLeftNbt(stack) - 1);
        });
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(BOW_SCREEN_CHANNEL, TitanFabricNetworking::handleBowScreenPacket);
        ServerPlayNetworking.registerGlobalReceiver(MULTI_BOW_ARROWS_CHANNEL, TitanFabricNetworking::handleMultiBowShotPacket);
    }
}
