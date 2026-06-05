package com.bvengo.soundcontroller.gui;

import com.bvengo.soundcontroller.Utils;
import com.bvengo.soundcontroller.VolumeData;
import com.bvengo.soundcontroller.config.VolumeConfig;
import com.bvengo.soundcontroller.gui.buttons.ToggleButtonWidget;
import com.bvengo.soundcontroller.gui.buttons.TriggerButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import java.util.Comparator;
import java.util.List;

import static com.bvengo.soundcontroller.Translations.SOUND_SCREEN_TITLE;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_TITLE;
import static com.bvengo.soundcontroller.Translations.SEARCH_FIELD_PLACEHOLDER;
import static com.bvengo.soundcontroller.Translations.FILTER_BUTTON_TOOLTIP;
import static com.bvengo.soundcontroller.Translations.SUBTITLES_BUTTON_TOOLTIP;

/**
 * Screen that displays all sound options.
 */
public class AllSoundOptionsScreen extends OptionsSubScreen {
    VolumeConfig config = VolumeConfig.getInstance();

    protected final Screen parent;

    private VolumeListWidget volumeListWidget;
    private EditBox searchField;
    private ToggleButtonWidget filterButton;

    private boolean showModifiedOnly = false;
    private final java.util.Set<String> expandedPaths = new java.util.HashSet<>(java.util.List.of("minecraft"));
    private String lastSearchQuery = "";
    private double lastScrollPosition = 0;
    private boolean widgetRecreated = false;

    public AllSoundOptionsScreen(Screen parent, Options options) {
        super(parent, options, SOUND_SCREEN_TITLE);
        this.parent = parent;

        // Increase header height to make room for search field. Includes 8 extra padding below.
        layout.setHeaderHeight(layout.getHeaderHeight() + 28);
    }

    @Override
    protected void init() {
        addSearchField();
        addCategoryFilterButton();
        addFilterButton();
        addSubtitlesButton();
        addVolumeList();
        addDoneButton();

        this.setInitialFocus(this.searchField);
    }

    public void rebuildWidgetsList() {
        this.rebuildWidgets();
    }

    @Override
    protected void addOptions() {}

    private void addSearchField() {
        this.searchField = new EditBox(this.font, 80, 35, this.width - 172, 20, SEARCH_FIELD_PLACEHOLDER);
        this.searchField.setValue(this.lastSearchQuery);
        this.searchField.setResponder(text -> {
            this.lastSearchQuery = text;
            this.loadOptions();
        });
        this.addWidget(this.searchField);
    }

    private void addCategoryFilterButton() {
        TriggerButtonWidget categoryFilterButton = new TriggerButtonWidget("configure",
                this.searchField.getRight() + 8, 35, 20, 20,
                (button) -> {
                    this.minecraft.setScreen(new CategoryFilterScreen(this));
                }
        );
        categoryFilterButton.setTooltip(Tooltip.create(Component.translatable("soundcontroller.options.filter_categories")));
        this.addRenderableWidget(categoryFilterButton);
    }

    private void addFilterButton() {
        this.filterButton = new ToggleButtonWidget("filter",
                this.searchField.getRight() + 32, 35, 20, 20,
                (button) -> {
                    showModifiedOnly = !showModifiedOnly;
                    loadOptions();
                },
                false
        );

        this.filterButton.setTooltip(Tooltip.create(FILTER_BUTTON_TOOLTIP));
        this.addRenderableWidget(this.filterButton);
    }

    private void addSubtitlesButton() {
        ToggleButtonWidget subtitlesButton = new ToggleButtonWidget("subtitles",
                this.filterButton.getRight() + 4, 35, 20, 20,
                (button) -> {
                    config.toggleSubtitles();
                },
                config.areSubtitlesEnabled());

        subtitlesButton.setTooltip(Tooltip.create(SUBTITLES_BUTTON_TOOLTIP));
        this.addRenderableWidget(subtitlesButton);
    }

    private void addVolumeList() {
        int top = this.searchField.getBottom() + 8;
        int height = this.height - top - 36;
        if (this.volumeListWidget != null) {
            this.lastScrollPosition = this.volumeListWidget.getScrollAmount();
        }
        this.volumeListWidget = new VolumeListWidget(this.minecraft, this.width, height, top);
        this.widgetRecreated = true;
        loadOptions();
        this.widgetRecreated = false;
        this.addRenderableWidget(this.volumeListWidget);
    }

    private void addDoneButton() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - 125, this.height - 27, 120, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("soundcontroller.options.reset_all"), button -> {
            config.resetAllToDefault();
            loadOptions();
        }).bounds(this.width / 2 + 5, this.height - 27, 120, 20).build());
    }

    public void loadOptions() {
        double scroll = this.lastScrollPosition;
        if (this.volumeListWidget != null && !this.widgetRecreated) {
            scroll = this.volumeListWidget.getScrollAmount();
        }
        this.volumeListWidget.clearEntries();

        String search = this.searchField.getValue();
        if (!search.equalsIgnoreCase(lastSearchQuery)) {
            scroll = 0;
            this.lastScrollPosition = 0;
            lastSearchQuery = search;
        }
        String searchLower = search.toLowerCase();

        Node root = new Node("root", "Root", true, -1, null);

        // Build tree
        config.getVolumes().values().stream()
            .filter(volumeData -> {
                String category = VolumeConfig.getCategory(volumeData.getId());
                return !config.getDisabledCategories().contains(category);
            })
            .forEach(volumeData -> {
                net.minecraft.resources.ResourceLocation id = volumeData.getId();
                String namespace = id.getNamespace();
                String path = id.getPath();
                String[] segments = path.split("\\.");

                Node nsNode = findOrCreateChild(root, namespace, com.bvengo.soundcontroller.platform.Services.PLATFORM.getModName(namespace), true, 0);

                Node parent = nsNode;
                for (int i = 0; i < segments.length - 1; i++) {
                    String segment = segments[i];
                    String folderId = parent.id + "." + segment;
                    parent = findOrCreateChild(parent, folderId, Utils.capitalize(segment), true, i + 1);
                }

                String leafId = id.toString();
                Node leafNode = new Node(leafId, segments[segments.length - 1], false, segments.length, volumeData);
                parent.children.add(leafNode);
            });

        for (Node nsNode : root.children) {
            updateVisibilityAndSearch(nsNode, searchLower, showModifiedOnly);
        }

        // Collect visible nodes
        List<Node> visibleNodes = new java.util.ArrayList<>();
        for (Node nsNode : root.children) {
            addVisibleNodesToList(nsNode, visibleNodes);
        }

        // Add to list widget
        for (Node node : visibleNodes) {
            if (node.isFolder) {
                boolean isExpanded = node.forceExpand || expandedPaths.contains(node.id);
                FolderWidgetEntry folderEntry = new FolderWidgetEntry(node.id, node.name, node.depth, isExpanded, () -> {
                    if (expandedPaths.contains(node.id)) {
                        expandedPaths.remove(node.id);
                    } else {
                        expandedPaths.add(node.id);
                    }
                    loadOptions();
                });
                this.volumeListWidget.addWidgetEntry(folderEntry);
            } else {
                VolumeWidgetEntry volumeEntry = new VolumeWidgetEntry(node.volumeData, this, this.options, node.depth);
                this.volumeListWidget.addWidgetEntry(volumeEntry);
            }
        }
        this.lastScrollPosition = scroll;
        this.volumeListWidget.setScrollAmount(scroll);
    }

    private Node findOrCreateChild(Node parent, String id, String displayName, boolean isFolder, int depth) {
        for (Node child : parent.children) {
            if (child.id.equals(id)) {
                return child;
            }
        }
        Node newChild = new Node(id, displayName, isFolder, depth, null);
        parent.children.add(newChild);
        return newChild;
    }

    private boolean updateVisibilityAndSearch(Node node, String search, boolean showModifiedOnly) {
        if (!node.isFolder) {
            boolean matches = node.volumeData.inFilter(search, showModifiedOnly);
            node.visible = matches;
            return matches;
        }

        boolean anyChildVisible = false;
        for (Node child : node.children) {
            if (updateVisibilityAndSearch(child, search, showModifiedOnly)) {
                anyChildVisible = true;
            }
        }

        node.visible = anyChildVisible;
        if (!search.isEmpty() && anyChildVisible) {
            node.forceExpand = true;
        } else {
            node.forceExpand = false;
        }
        return anyChildVisible;
    }

    private void addVisibleNodesToList(Node node, List<Node> list) {
        if (!node.visible) {
            return;
        }

        list.add(node);

        if (node.isFolder) {
            boolean isExpanded = node.forceExpand || expandedPaths.contains(node.id);
            if (isExpanded) {
                node.children.sort((a, b) -> {
                    if (a.isFolder != b.isFolder) {
                        return a.isFolder ? -1 : 1;
                    }
                    return a.name.compareToIgnoreCase(b.name);
                });

                for (Node child : node.children) {
                    addVisibleNodesToList(child, list);
                }
            }
        }
    }

    @Override
    public void removed() {
        config.save();
        if (this.volumeListWidget != null) {
            this.lastScrollPosition = this.volumeListWidget.getScrollAmount();
        }
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        // Cache search before clearing
        String search = this.searchField.getValue();

        this.width = width;
        this.height = height;

        this.clearWidgets();
        this.clearFocus();
        this.init();

        this.searchField.setValue(search);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);
        context.drawString(this.font, SEARCH_FIELD_TITLE, 80, 24, 0xFFA0A0A0);
        this.searchField.render(context, mouseX, mouseY, delta);
    }

    public static class Node {
        public final String id;
        public final String name;
        public final boolean isFolder;
        public final int depth;
        public final VolumeData volumeData;
        public final List<Node> children = new java.util.ArrayList<>();

        public boolean visible = false;
        public boolean forceExpand = false;

        public Node(String id, String name, boolean isFolder, int depth, VolumeData volumeData) {
            this.id = id;
            this.name = name;
            this.isFolder = isFolder;
            this.depth = depth;
            this.volumeData = volumeData;
        }
    }
}
