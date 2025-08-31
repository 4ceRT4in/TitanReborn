package net.shirojr.titanfabric.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.util.LoggerUtil;
import net.shirojr.titanfabric.util.items.ArrowSelectionHelper;
import net.shirojr.titanfabric.util.items.ProjectileStackSorter;
import net.shirojr.titanfabric.util.items.SelectableArrow;

import java.util.List;
import java.util.Optional;

public record ArrowSelectionPacket(int weaponStackIndex) implements CustomPayload {
    public static final Id<ArrowSelectionPacket> IDENTIFIER =
            new Id<>(TitanFabric.getId("arrow_selection"));

    public static final PacketCodec<RegistryByteBuf, ArrowSelectionPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, ArrowSelectionPacket::weaponStackIndex,
            ArrowSelectionPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        PlayerInventory inventory = player.getInventory();
        ItemStack weaponStack = inventory.getStack(weaponStackIndex);
        if (!(weaponStack.getItem() instanceof SelectableArrow selectionHandler)) return;
        List<ItemStack> arrowStacks = ArrowSelectionHelper.findAllSupportedArrowStacks(player, selectionHandler);
        List<ItemStack> processedStacks = new ProjectileStackSorter(player, arrowStacks)
                .keepUniqueBestStacks()
                .sortItemType()
                .sortStackCount()
                .sortIsInBag()
                .getResult();

        if (processedStacks.isEmpty()) return;
        ItemStack newSelectedArrowStack;

        ItemStack selectedArrowStack = SelectableArrow.getSelectedArrowStack(weaponStack, player);
        if (selectedArrowStack == null) {
            newSelectedArrowStack = processedStacks.get(0);
        } else {
            if (processedStacks.contains(selectedArrowStack)) {
                int newIndexInArrowList = processedStacks.indexOf(selectedArrowStack) + 1;
                if (newIndexInArrowList > processedStacks.size() - 1) newIndexInArrowList = 0;
                newSelectedArrowStack = processedStacks.get(newIndexInArrowList);
            } else {
                newSelectedArrowStack = processedStacks.get(0);
            }
        }

        Text arrowStackName = newSelectedArrowStack.getItem().getName(newSelectedArrowStack);
        player.sendMessage(Text.translatable("actionbar.titanfabric.arrow_selection").append(arrowStackName), true);

        Optional<SelectableArrow.Index> newStackIndex = SelectableArrow.Index.get(player, newSelectedArrowStack);

        boolean changedComponent = false;
        if (newStackIndex.isPresent()) {
            changedComponent = SelectableArrow.applySelectedArrowStack(weaponStack, newStackIndex.orElse(null));
        }

        LoggerUtil.devLogger("SelectedStack: %s | Changed final Stack Component: %s".formatted(newSelectedArrowStack.getName(), changedComponent));
    }
}
