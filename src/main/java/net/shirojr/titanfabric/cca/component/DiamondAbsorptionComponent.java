package net.shirojr.titanfabric.cca.component;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.TitanFabricComponents;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public interface DiamondAbsorptionComponent extends Component, ServerTickingComponent {
    Identifier IDENTIFIER = TitanFabric.getId("diamond_absorption");

    static DiamondAbsorptionComponent get(LivingEntity entity) {
        return TitanFabricComponents.DIAMOND_ABSORPTION.get(entity);
    }

    float getDiamondAbsorptionAmount();

    void setDiamondAbsorptionAmount(float amount, boolean shouldSync);

    void sync();
}
