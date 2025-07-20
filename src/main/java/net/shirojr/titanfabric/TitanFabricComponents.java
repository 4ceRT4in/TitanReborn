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
 * <h2>Cardinal Components API Entrypoints</h2><br>
 * To work with TitanFabric's components, check out {@link ExtendedInventoryComponent}<br><br>
 * Generally you want to use
 * <ul>
 *     <li>{@link ExtendedInventoryComponent#getGlobal(World)}</li>
 *     <li>{@link ExtendedInventoryComponent#getTeamOrEntity(LivingEntity)}</li>
 * </ul>
 * ... for most interactions.<br><br>
 * <p>
 * Extended Inventories will remember, if they should drop their content. Extended Inventories drop their items using
 * {@link net.minecraft.util.ItemScatterer ItemScatterer} appropriately if their values allow for it.
 * Use Extended Inventories Getter {@link ExtendedInventoryComponent#shouldDropInventory() shouldDropInventory()} and
 * Setter {@link ExtendedInventoryComponent#setDropInventory(boolean, boolean) setDropInventory(boolean, boolean)} methods
 * to remove their contents and spawn them when the next player dies,
 * which is linked to it. The {@link net.minecraft.world.GameRules#KEEP_INVENTORY KEEP_INVENTORY} Gamerule is already
 * being considered when checking if the Extended Inventory should be dropped. Item dropping is handled in
 * {@link net.shirojr.titanfabric.event.custom.DeathEvents DeathEvents}.<br><br>
 * For safety reasons, Global Extended Inventories can't be dropped and setting their value to be
 * droppable will result in {@link UnsupportedOperationException}
 *
 * <h3>Examples</h3>
 * <pre>
 * {@code
 *     private static Inventory getInventoryFromEntity(LivingEntity target) {
 *         return ExtendedInventoryComponent.getTeamOrEntity(target).getInventory();
 *     }
 * }
 * <pre>
 * <pre>
 * {@code
 *     private static void addTeamLives(Team team, int amount) {
 *         int newAmount = amount + 69; // just an example...
 *
 *         // ...
 *
 *         ExtendedInventoryComponent inventoryComponent = ExtendedInventoryComponent.getTeam(team);
 *
 *         inventoryComponent.setDropInventory(newAmount <= 0, true);
 *     }
 * }
 * <pre>
 * @see <a href=https://ladysnake.org/wiki/cardinal-components-api/>Cardinal Components API</a>
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
