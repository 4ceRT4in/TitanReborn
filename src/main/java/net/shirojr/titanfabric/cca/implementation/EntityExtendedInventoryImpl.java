package net.shirojr.titanfabric.cca.implementation;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import net.shirojr.titanfabric.TitanFabricComponents;

public class EntityExtendedInventoryImpl extends AbstractExtendedInventoryComponentImpl {
    private final LivingEntity provider;

    public EntityExtendedInventoryImpl(LivingEntity provider) {
        super();
        this.provider = provider;
    }

    @Override
    public Text getHeaderText() {
        return provider.getDisplayName();
    }

    @Override
    public InventoryType getType() {
        return InventoryType.ENTITY;
    }

    @Override
    public boolean shouldDropInventory() {
        return super.shouldDropInventory() && !this.provider.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
    }

    @Override
    public void sync() {
        TitanFabricComponents.EXTENDED_INVENTORY_ENTITY.sync(this.provider);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return super.shouldSyncWith(player) || provider.equals(player);
    }
}
