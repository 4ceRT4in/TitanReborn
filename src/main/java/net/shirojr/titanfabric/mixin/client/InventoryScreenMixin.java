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

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
    @Shadow
    @Final
    private RecipeBookWidget recipeBook;

    @Unique
    private static final ButtonWidget.NarrationSupplier DEFAULT_NARRATION_SUPPLIER = Supplier::get;


    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void titanfabric$addInventoryScreenElements(CallbackInfo ci) {
        if (this.client == null || this.client.player == null) return;
        int buttonX = (this.width - this.backgroundWidth) / 2;
        var builder = ButtonWidget.builder(Text.translatable("screen.titanfabric.save_inventory_arrow1"), button -> {
            if (this.client.mouse != null) {
                this.client.mouse.unlockCursor();
            }
            ClientPlayerEntity player = this.client.player;
            new ExtendedInventoryOpenPacket(player.getId(), Optional.of(player.getId())).sendPacket();
        });
        builder.dimensions(buttonX + 2, this.height / 2 - 106, 20, 20);
        builder.narrationSupplier(DEFAULT_NARRATION_SUPPLIER);
        ButtonWidget buttonWidget = builder.build();
        this.addDrawableChild(buttonWidget);
    }
}
