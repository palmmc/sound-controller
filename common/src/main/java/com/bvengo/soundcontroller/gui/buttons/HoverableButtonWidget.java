package com.bvengo.soundcontroller.gui.buttons;

import com.bvengo.soundcontroller.SoundController;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

/**
 * Custom button widget that is used as a trigger rather than a toggle.
 * i.e. it is only active while the button is being pressed.
 */
public class HoverableButtonWidget extends Button {
    protected boolean isPressed = false;

    protected final ResourceLocation ON_TEXTURE;
    protected final ResourceLocation OFF_TEXTURE;

    String buttonId;

    public HoverableButtonWidget(String buttonId, int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);

        this.buttonId = buttonId;

        ON_TEXTURE = ResourceLocation.fromNamespaceAndPath(SoundController.MOD_ID, buttonId + "_button_on");
        OFF_TEXTURE = ResourceLocation.fromNamespaceAndPath(SoundController.MOD_ID, buttonId + "_button_off");
    }

    protected ResourceLocation getTextureIdentifier() {
        return isPressed ? ON_TEXTURE : OFF_TEXTURE;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        ResourceLocation texture = getTextureIdentifier();
        if (isHovered) {
            context.setColor(0.7f, 0.7f, 0.7f, 1.0f);
        }
        context.blitSprite(texture, getX(), getY(), width, height);
        context.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
