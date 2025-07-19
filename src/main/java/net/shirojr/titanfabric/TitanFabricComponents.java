package net.shirojr.titanfabric;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.shirojr.titanfabric.cca.component.ExtendedInventoryComponent;
import net.shirojr.titanfabric.cca.implementation.EntityExtendedInventoryImpl;
import net.shirojr.titanfabric.cca.implementation.GlobalExtendedInventoryImpl;
import net.shirojr.titanfabric.cca.implementation.TeamExtendedInventoryImpl;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentInitializer;

/**
 * Cardinal Components API entrypoints.<br>
 * To work with the components, check out {@link ExtendedInventoryComponent}<br><br>
 * Generally you want to use
 * <ul>
 *     <li>{@link ExtendedInventoryComponent#getGlobal(World)}</li>
 *     <li>{@link ExtendedInventoryComponent#getTeamOrEntity(LivingEntity)}</li>
 * </ul>
 * ... for most interactions.
 */
public class TitanFabricComponents implements EntityComponentInitializer, ScoreboardComponentInitializer {

    public static final ComponentKey<ExtendedInventoryComponent> EXTENDED_INVENTORY_GLOBAL =
            ComponentRegistry.getOrCreate(TitanFabric.getId("extended_inventory_global"), ExtendedInventoryComponent.class);
    public static final ComponentKey<ExtendedInventoryComponent> EXTENDED_INVENTORY_TEAM =
            ComponentRegistry.getOrCreate(TitanFabric.getId("extended_inventory_team"), ExtendedInventoryComponent.class);
    public static final ComponentKey<ExtendedInventoryComponent> EXTENDED_INVENTORY_ENTITY =
            ComponentRegistry.getOrCreate(TitanFabric.getId("extended_inventory_entity"), ExtendedInventoryComponent.class);


    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(EXTENDED_INVENTORY_GLOBAL, GlobalExtendedInventoryImpl::new);
        registry.registerTeamComponent(EXTENDED_INVENTORY_TEAM, TeamExtendedInventoryImpl::new);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, EXTENDED_INVENTORY_ENTITY, EntityExtendedInventoryImpl::new);
    }
}
