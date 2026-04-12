package net.shirojr.titanfabric.cca.component;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.TitanFabricComponents;
import net.shirojr.titanfabric.util.effects.RecoveryProfile;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public interface RecoveryBufferComponent extends Component, ServerTickingComponent {
    Identifier IDENTIFIER = TitanFabric.getId("recovery_buffer");

    static RecoveryBufferComponent get(LivingEntity entity) {
        return TitanFabricComponents.RECOVERY_BUFFER.get(entity);
    }

    float getBufferAmount();

    float getMaxBufferAmount();

    @Nullable
    RecoveryProfile getActiveProfile();

    boolean isHealingActive();

    void sync();
}
