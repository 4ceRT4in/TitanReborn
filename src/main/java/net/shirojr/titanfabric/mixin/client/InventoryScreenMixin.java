package net.shirojr.titanfabric.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.shirojr.titanfabric.network.NetworkingIdentifiers;
import net.shirojr.titanfabric.screen.custom.ExtendedInventoryScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
    @Shadow
    @Final
    private RecipeBookWidget recipeBook;

    @Unique
    private TexturedButtonWidget buttonWidget;

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "method_19891", at = @At("TAIL"))
    private void titanfabric$moveButton(ButtonWidget button, CallbackInfo ci) {
        buttonWidget.x = this.x + 134;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void titanfabric$addInventoryScreenElements(CallbackInfo ci) {
        if (this.client == null) return;
        int buttonX = recipeBook.findLeftEdge(this.width, this.backgroundWidth);
        this.buttonWidget = new TexturedButtonWidget(buttonX + 134, this.height / 2 - 22,
                20, 19, 178, 0, 19,
                ExtendedInventoryScreen.TEXTURE,
                button -> {
                    PacketByteBuf buf = PacketByteBufs.create();
                    ClientPlayNetworking.send(NetworkingIdentifiers.EXTENDED_INVENTORY_OPEN, buf);
                });
        this.addDrawableChild(buttonWidget);
    }
}
