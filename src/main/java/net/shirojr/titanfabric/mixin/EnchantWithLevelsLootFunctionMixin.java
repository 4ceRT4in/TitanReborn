package net.shirojr.titanfabric.mixin;


import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(EnchantWithLevelsLootFunction.class)
public abstract class EnchantWithLevelsLootFunctionMixin extends ConditionalLootFunction {

    protected EnchantWithLevelsLootFunctionMixin(List<LootCondition> conditions) {
        super(conditions);
    }
}