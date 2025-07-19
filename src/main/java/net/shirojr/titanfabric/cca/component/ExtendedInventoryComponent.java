package net.shirojr.titanfabric.cca.component;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.shirojr.titanfabric.TitanFabricComponents;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.function.Consumer;

public interface ExtendedInventoryComponent extends Component {
    static ExtendedInventoryComponent getGlobal(World world) {
        return TitanFabricComponents.EXTENDED_INVENTORY_GLOBAL.get(world.getScoreboard());
    }

    static ExtendedInventoryComponent getTeam(Team team) {
        return TitanFabricComponents.EXTENDED_INVENTORY_TEAM.get(team);
    }

    static ExtendedInventoryComponent getEntity(LivingEntity entity) {
        return TitanFabricComponents.EXTENDED_INVENTORY_ENTITY.get(entity);
    }

    static ExtendedInventoryComponent getTeamOrEntity(LivingEntity entity) {
        if (entity.getScoreboardTeam() != null) {
            return getTeam(entity.getScoreboardTeam());
        }
        return getEntity(entity);
    }

    InventoryType getType();

    Text getHeaderText();

    /**
     * @return Returns the current instance of the Inventory.
     * @implNote For modifications call {@link ExtendedInventoryComponent#modifyInventory(Consumer, boolean) modifyInventory} instead,
     * or call {@link ExtendedInventoryComponent#sync()} manually,
     * to sync and store the inventory content persistently
     */
    Inventory getInventory();

    @SuppressWarnings("unused")
    void modifyInventory(Consumer<SimpleInventory> consumer, boolean shouldSync);

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
