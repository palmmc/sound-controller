package com.bvengo.soundcontroller.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class FolderWidgetEntry extends VolumeListEntry {
    private final String id;
    private final String name;
    private final int depth;
    private final boolean isExpanded;
    private final Runnable toggleAction;
    private final Font font;

    public FolderWidgetEntry(String id, String name, int depth, boolean isExpanded, Runnable toggleAction) {
        this.id = id;
        this.name = name;
        this.depth = depth;
        this.isExpanded = isExpanded;
        this.toggleAction = toggleAction;
        this.font = Minecraft.getInstance().font;
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float partialTick) {
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int totalWidth = VolumeWidgetEntry.totalWidth;
        int left = (screenWidth - totalWidth) / 2;
        int indent = depth * 12;

        int backgroundColor = hovered ? 0x22FFFFFF : 0x0CFFFFFF;
        context.fill(left + indent, y, left + totalWidth, y + 20, backgroundColor);

        String prefix = isExpanded ? "▼ " : "▶ ";
        context.drawString(font, Component.literal(prefix + name), left + indent + 6, y + 6, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(
                    isExpanded ? SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF : SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, 1.0F));
            toggleAction.run();
            return true;
        }
        return false;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of();
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of();
    }
}
