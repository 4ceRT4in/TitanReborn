package net.shirojr.titanfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.shirojr.titanfabric.init.TitanFabricStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Consumer;

@Mixin(PotionContentsComponent.class)
public class PotionContentsComponentMixin {

    @ModifyExpressionValue(method = "buildTooltip(Ljava/lang/Iterable;Ljava/util/function/Consumer;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;getAmplifier()I", ordinal = 1))
    private static int bigAmplifierPrintingBugFix(int original, @Local StatusEffectInstance instance) {
        if (instance.getEffectType().equals(TitanFabricStatusEffects.FROSTBURN)) return 0;
        return original;
    }

    @WrapOperation(method = "buildTooltip(Ljava/lang/Iterable;Ljava/util/function/Consumer;FF)V", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private static boolean appendFrostburnHealth(List<Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>> instance, Operation<Boolean> original,
                                                 @Local(argsOnly = true) Iterable<StatusEffectInstance> effects, @Local(argsOnly = true) Consumer<Text> tooltip) {
        boolean originalEvaluation = original.call(instance);

        boolean hasEffect = false;
        double amplifier = -1;
        for (StatusEffectInstance effect : effects) {
            if (effect.getEffectType() == null || !effect.getEffectType().equals(TitanFabricStatusEffects.FROSTBURN))
                continue;
            hasEffect = true;
            amplifier = effect.getAmplifier();
            break;
        }
        if (!hasEffect) return originalEvaluation;

        tooltip.accept(ScreenTexts.EMPTY);
        tooltip.accept(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));

        tooltip.accept(Text.translatable("tooltip.titanfabric.potion.frostburn", amplifier).formatted(Formatting.RED));
        return originalEvaluation;
    }
}
