package net.shirojr.titanfabric.screen.custom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.titanfabric.TitanFabric;
import net.shirojr.titanfabric.screen.handler.ExtendedInventoryScreenHandler;

import java.util.List;

public class ExtendedInventoryScreen extends AbstractInventoryScreen<ExtendedInventoryScreenHandler> implements RecipeBookProvider {
    public static final Identifier TEXTURE = TitanFabric.getId("textures/gui/extended_inventory.png");

    private static class FakeRecipeBookWidget extends RecipeBookWidget {
        private boolean open;
        private boolean narrow;
        private int parentWidth;
        private int parentHeight;
        private final int panelWidth = 147;
        private int panelTop;
        private int panelLeft;
        @Override
        public void initialize(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow, AbstractRecipeScreenHandler<?, ?> handler) {
            this.parentWidth = parentWidth;
            this.parentHeight = parentHeight;
            this.narrow = narrow;
            this.open = false;
            this.recomputePanelPos();
        }
        private void recomputePanelPos() {
            this.panelTop = (this.parentHeight - 166) / 2;
            this.panelLeft = this.narrow ? 0 : (this.parentWidth - 176) / 2 - this.panelWidth;
        }
        @Override
        public void toggleOpen() {
            this.open = !this.open;
        }
        @Override
        public boolean isOpen() {
            return this.open;
        }
        @Override
        public int findLeftEdge(int parentWidth, int backgroundWidth) {
            return (parentWidth - backgroundWidth) / 2 + ((this.open && !this.narrow) ? 0 : 0);
        }
        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        }
        @Override
        public void drawGhostSlots(DrawContext context, int left, int top, boolean bl, float delta) {
        }
        @Override
        public void drawTooltip(DrawContext context, int left, int top, int mouseX, int mouseY) {
        }
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }
        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return false;
        }
        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return false;
        }
        @Override
        public boolean charTyped(char chr, int modifiers) {
            return false;
        }
        @Override
        public void slotClicked(Slot slot) {
        }
        @Override
        public void showGhostRecipe(RecipeEntry<?> recipe, List<Slot> slots) {
        }
        @Override
        public void refresh() {
        }
        @Override
        public boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int backgroundWidth, int backgroundHeight, int button) {
            if (!this.open) return false;
            int x0 = this.narrow ? 0 : this.panelLeft;
            int y0 = this.panelTop;
            int x1 = x0 + this.panelWidth;
            int y1 = y0 + 166;
            return mouseX < x0 || mouseX >= x1 || mouseY < y0 || mouseY >= y1;
        }
    }

    private final FakeRecipeBookWidget recipeBook = new FakeRecipeBookWidget();
    private boolean narrow;
    private boolean mouseDown;

    public ExtendedInventoryScreen(ExtendedInventoryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title == null ? Text.of("") : title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = 86;
        this.titleY = 6;
        this.playerInventoryTitleX = this.titleX;
        if (client == null || this.client.player == null || this.client.interactionManager == null) return;
        if (this.client.interactionManager.hasCreativeInventory()) {
            this.client.setScreen(new CreativeInventoryScreen(this.client.player, this.client.player.networkHandler.getEnabledFeatures(), this.client.options.getOperatorItemsTab().getValue()));
            return;
        }
        this.narrow = this.width < 379;
        this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, null);
        this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
        this.addDrawableChild(new TexturedButtonWidget(this.x + 104, this.height / 2 - 22, 20, 18, RecipeBookWidget.BUTTON_TEXTURES, button -> {
            this.recipeBook.toggleOpen();
            this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
            button.setPosition(this.x + 104, this.height / 2 - 22);
            this.mouseDown = true;
        }));
        ButtonWidget buttonWidget = ButtonWidget.builder(Text.translatable("screen.titanfabric.save_inventory_arrow2"), button -> {
            if (this.client.mouse != null) this.client.mouse.unlockCursor();
            this.client.setScreen(new InventoryScreen(this.client.player));
        }).dimensions(this.x + 2, this.height / 2 - 106, 20, 20).build();
        this.addDrawableChild(buttonWidget);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        if (this.client == null || this.client.player == null) return;
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        InventoryScreen.drawEntity(context, x + 26, y + 8, x + 75, y + 78, 30, 0.0625F, mouseX, mouseY, this.client.player);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, Text.translatable("screen.titanfabric.extended_inventory.title"), this.titleX, this.titleY, 0x404040, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.recipeBook.render(context, mouseX, mouseY, delta);
        if (!this.recipeBook.isOpen() || !this.narrow) {
            this.recipeBook.drawGhostSlots(context, this.x, this.y, false, delta);
        }
        this.drawBackground(context, delta, mouseX, mouseY);
        this.recipeBook.drawTooltip(context, this.x, this.y, mouseX, mouseY);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.recipeBook.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.recipeBook.charTyped(chr, modifiers) || super.charTyped(chr, modifiers);
    }

    @Override
    protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        return (!this.narrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.recipeBook);
            return true;
        } else {
            return (!this.narrow || !this.recipeBook.isOpen()) && super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.mouseDown) {
            this.mouseDown = false;
            return true;
        } else {
            return super.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);
        this.recipeBook.slotClicked(slot);
    }

    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }

    @Override
    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBook;
    }
}