package net.shirojr.titanfabric.init;

import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.cca.component.FrostburnComponent;

@SuppressWarnings("unused")
public class TitanFabricCommandArgumentTypes {
    static {
        register(
                "frostburn_phase",
                FrostburnComponent.Phase.PhaseArgumentType.class,
                ConstantArgumentSerializer.of(FrostburnComponent.Phase.PhaseArgumentType::phase)
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void register(
            String name,
            Class<? extends A> clazz,
            ArgumentSerializer<A, T> serializer) {
        ArgumentTypeRegistry.registerArgumentType(TitanFabric.getId(name), clazz, serializer);
    }

    public static void initialize() {
        // static initialisation
    }
}
