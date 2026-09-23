package net.shirojr.titanfabric.compat.emi;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NetheriteAnvilEnchantedBookRecipe implements EmiRecipe {
    private final EmiStack input;
    private final EmiStack output;
    private final Identifier id;

    public NetheriteAnvilEnchantedBookRecipe(RegistryEntry<Enchantment> enchantment, int inputLevel, int outputLevel, Identifier id) {
        this.input = EmiStack.of(createBook(enchantment, inputLevel));
        this.output = EmiStack.of(createBook(enchantment, outputLevel));
        this.id = id;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return TitanRebornEmiPlugin.NETHERITE_ANVIL_CATEGORY;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input, input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public int getDisplayWidth() {
        return 125;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.PLUS, 27, 3);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
        widgets.addText(Text.translatable("-50%"), 75, 0, -1, true);
        widgets.addSlot(input, 0, 0);
        widgets.addSlot(input, 49, 0);
        widgets.addSlot(output, 107, 0).recipeContext(this);

        AtomicInteger animation = new AtomicInteger();
        widgets.addDrawable(83, 9, 9, 9, (raw, mouseX, mouseY, delta) -> {
            EmiDrawContext draw = EmiDrawContext.wrap(raw);
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.98f);
            if (animation.get() % 81 == 0) {
                animation.set(0);
            }
            draw.drawTexture(TitanFabric.getId("textures/gui/sprites/container/experience_orb.png"), 0, 0, 9, 9, 0, (int) (animation.get() / 9 * 9), 9, 9, 9, 81);
            RenderSystem.disableBlend();
            animation.addAndGet(1);
        });
    }

    private static ItemStack createBook(RegistryEntry<Enchantment> enchantment, int level) {
        return EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, level));
    }
}
