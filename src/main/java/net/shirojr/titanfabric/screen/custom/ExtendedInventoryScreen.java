package net.shirojr.titanfabric.screen.custom;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class ExtendedInventoryScreen extends InventoryScreen {
    private ButtonWidget buttonWidget;
    private String buttonText;

    public ExtendedInventoryScreen(PlayerEntity player, String buttonText) {
        super(player);
        this.buttonText = buttonText;
    }

    @Override
    protected void init() {
        if (client == null || this.client.player == null || this.client.interactionManager == null) return;
        if (this.client.interactionManager.hasCreativeInventory()) {
            this.client.setScreen(new CreativeInventoryScreen(this.client.player));
            return;
        }
        buttonWidget = new ButtonWidget(this.x, this.height / 2 - 104, 20, 20,
                new LiteralText(buttonText).formatted(Formatting.BOLD), button -> {
            if (buttonText.equals("O")) buttonText = "X";
            else buttonText = "O";
            buttonWidget.setMessage(new LiteralText(buttonText).formatted(Formatting.BOLD));
            this.client.setScreen(new ExtendedInventoryScreen(client.player, buttonText));
        });
        this.addDrawableChild(buttonWidget);
    }
}
