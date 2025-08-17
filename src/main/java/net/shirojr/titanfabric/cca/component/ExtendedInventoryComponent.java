package net.shirojr.titanfabric.cca.component;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.TitanFabricComponents;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.function.Consumer;

public interface ExtendedInventoryComponent extends Component {
    Identifier IDENTIFIER_GLOBAL = TitanFabric.getId("extended_inventory_global");
    Identifier IDENTIFIER_TEAM = TitanFabric.getId("extended_inventory_team");
    Identifier IDENTIFIER_ENTITY = TitanFabric.getId("extended_inventory_entity");

    /**
     * Provides access to the player Team's Extended Inventory or their private Extended Inventory, if they don't belong
     * to any Team. For safety reasons, this method should be preferred over the others (excluding the global option),
     * but for more specific access use {@link ExtendedInventoryComponent#getTeam(Team)}
     * or {@link ExtendedInventoryComponent#getEntity(LivingEntity)}
     */
    static ExtendedInventoryComponent getTeamOrEntity(LivingEntity entity) {
        if (entity.getScoreboardTeam() != null) {
            return getTeam(entity.getScoreboardTeam());
        }
        return getEntity(entity);
    }

    static ExtendedInventoryComponent getEntity(LivingEntity entity) {
        return TitanFabricComponents.EXTENDED_INVENTORY_ENTITY.get(entity);
    }

    static ExtendedInventoryComponent getTeam(Team team) {
        return TitanFabricComponents.EXTENDED_INVENTORY_TEAM.get(team);
    }

    static ExtendedInventoryComponent getGlobal(World world) {
        return TitanFabricComponents.EXTENDED_INVENTORY_GLOBAL.get(world.getScoreboard());
    }

    InventoryType getType();

    Text getHeaderText();

    /**
     * @return Current instance of the Inventory.
     * @implNote For modifications call {@link ExtendedInventoryComponent#modifyInventory(Consumer, boolean) modifyInventory} instead,
     * or call {@link ExtendedInventoryComponent#sync()} manually,
     * to sync and store the inventory content persistently
     */
    Inventory getInventory();

    void modifyInventory(Consumer<SimpleInventory> consumer, boolean shouldSync);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean shouldDropInventory();

    void setDropInventory(boolean shouldDropInventory, boolean shouldSync);

    void sync();


    enum InventoryType {
        ENTITY(8),
        TEAM(8),
        GLOBAL(8);

        private final int size;

        InventoryType(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }
}
