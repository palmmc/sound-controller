package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.config.VolumeConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class CategoryFilterScreen extends Screen {
    private final Screen parent;
    private final VolumeConfig config = VolumeConfig.getInstance();
    private CategoryListWidget categoryListWidget;

    public CategoryFilterScreen(Screen parent) {
        super(Component.translatable("soundcontroller.options.display_categories"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.categoryListWidget = new CategoryListWidget(this.minecraft, this.width, this.height - 76, 40);
        this.addRenderableWidget(this.categoryListWidget);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 + 55, this.height - 27, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("soundcontroller.options.toggle_all"), button -> {
            toggleAllCategories();
        }).bounds(this.width / 2 - 155, this.height - 27, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("soundcontroller.options.reset_all"), button -> {
            resetCategories();
        }).bounds(this.width / 2 - 50, this.height - 27, 100, 20).build());
    }

    private void toggleAllCategories() {
        Set<String> disabled = config.getDisabledCategories();
        List<String> allCats = getAllCategories();
        if (disabled.size() == allCats.size()) {
            disabled.clear();
        } else {
            disabled.addAll(allCats);
        }
        categoryListWidget.reload();
    }

    private void resetCategories() {
        config.getDisabledCategories().clear();
        categoryListWidget.reload();
    }

    private List<String> getAllCategories() {
        return config.getVolumes().values().stream()
                .map(v -> VolumeConfig.getCategory(v.getId()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public void removed() {
        config.save();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
        if (this.parent instanceof AllSoundOptionsScreen) {
            ((AllSoundOptionsScreen) this.parent).rebuildWidgetsList();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);
    }

    class CategoryListWidget extends ContainerObjectSelectionList<CategoryEntry> {
        public CategoryListWidget(Minecraft client, int width, int height, int y) {
            super(client, width, height, y, 24);
            this.centerListVertically = false;
            reload();
        }

        public void reload() {
            this.clearEntries();
            List<String> allCats = getAllCategories();
            for (String cat : allCats) {
                this.addEntry(new CategoryEntry(cat));
            }
        }

        @Override
        public int getRowWidth() {
            return 220;
        }
    }

    class CategoryEntry extends ContainerObjectSelectionList.Entry<CategoryEntry> {
        private final Checkbox checkbox;

        public CategoryEntry(String categoryName) {
            boolean isChecked = !config.getDisabledCategories().contains(categoryName);
            this.checkbox = Checkbox.builder(Component.literal(categoryName), CategoryFilterScreen.this.font)
                    .selected(isChecked)
                    .onValueChange((cb, checked) -> {
                        if (checked) {
                            config.getDisabledCategories().remove(categoryName);
                        } else {
                            config.getDisabledCategories().add(categoryName);
                        }
                    })
                    .build();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int left = (CategoryFilterScreen.this.width - 220) / 2;
            this.checkbox.setPosition(left + 10, getY());
            this.checkbox.extractRenderState(context, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(checkbox);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(checkbox);
        }
    }
}
