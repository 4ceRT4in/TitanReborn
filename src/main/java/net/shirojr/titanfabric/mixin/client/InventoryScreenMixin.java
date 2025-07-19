package net.shirojr.titanfabric.mixin.client;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.network.packet.ExtendedInventoryOpenPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
    @Shadow
    @Final
    private RecipeBookWidget recipeBook;

    @Unique
    private ButtonWidget buttonWidget;

    @Unique
    private static final ButtonWidget.NarrationSupplier DEFAULT_NARRATION_SUPPLIER = Supplier::get;


    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "method_19891", at = @At("TAIL"))
    private void titanfabric$moveButton(ButtonWidget button, CallbackInfo ci) {
        buttonWidget.setX(this.x + 134);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void titanfabric$addInventoryScreenElements(CallbackInfo ci) {
        if (this.client == null || this.client.player == null) return;
        int buttonX = recipeBook.findLeftEdge(this.width, this.backgroundWidth);
        var builder = ButtonWidget.builder(Text.literal(">>"), button -> {
            if (this.client.mouse != null) {
                this.client.mouse.unlockCursor();
            }
            ClientPlayerEntity player = this.client.player;
            new ExtendedInventoryOpenPacket(player.getId(), player.getId()).sendPacket();
        });
        builder.dimensions(buttonX + 2, this.height / 2 - 106, 20, 20);
        builder.narrationSupplier(DEFAULT_NARRATION_SUPPLIER);
        this.buttonWidget = builder.build();
        this.addDrawableChild(buttonWidget);
    }
}
