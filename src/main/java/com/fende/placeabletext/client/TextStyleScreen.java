package com.fende.placeabletext.client;

import com.fende.placeabletext.TextStyle;
import com.fende.placeabletext.network.UpdateStylePayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.neoforged.neoforge.network.PacketDistributor;

public final class TextStyleScreen extends Screen {
    private static final String[] COLOR_NAMES = {
            "Black", "Dark Gray", "Gray", "White", "Dark Red", "Red", "Gold", "Yellow",
            "Dark Green", "Green", "Aqua", "Dark Aqua", "Dark Blue", "Blue", "Purple", "Pink"
    };
    private static final String[] COLOR_SHORT = {
            "BK", "DG", "GY", "WH", "DR", "RD", "GD", "YL",
            "DN", "GN", "AQ", "DA", "DB", "BL", "PU", "PK"
    };
    private static final int[] COLORS = {
            0x000000, 0x555555, 0xAAAAAA, 0xFFFFFF, 0xAA0000, 0xFF5555, 0xFFAA00, 0xFFFF55,
            0x00AA00, 0x55FF55, 0x55FFFF, 0x00AAAA, 0x0000AA, 0x5555FF, 0xAA00AA, 0xFF55FF
    };

    private final BlockPos target;
    private int size, textColor, backgroundColor, outlineColor, lightLevel, rotation;
    private int backgroundMode;
    private boolean outline, shadow, phosphor, emitLight;
    private float horizontalOffset, verticalOffset, depthOffset;
    private int colorTarget;
    private Button textColorButton, backgroundColorButton, outlineColorButton;

    public TextStyleScreen(TextStyle style, BlockPos target) {
        super(Component.translatable("gui.placeabletext.title"));
        this.target = target;
        size = style.size(); textColor = style.textColor(); backgroundMode = style.backgroundMode();
        backgroundColor = style.backgroundColor(); outline = style.outline(); outlineColor = style.outlineColor();
        shadow = style.shadow(); phosphor = style.phosphor(); emitLight = style.emitLight();
        lightLevel = style.lightLevel(); rotation = style.rotation();
        horizontalOffset = style.horizontalOffset(); verticalOffset = style.verticalOffset(); depthOffset = style.depthOffset();
    }

    @Override protected void init() {
        int left = width / 2 - 130, top = height / 2 - 105;
        addRenderableWidget(Button.builder(sizeLabel(), b -> { size = (size + 1) % 3; b.setMessage(sizeLabel()); })
                .bounds(left, top, 120, 20).build());
        addRenderableWidget(Button.builder(backgroundLabel(), b -> { backgroundMode = (backgroundMode + 1) % 3; b.setMessage(backgroundLabel()); })
                .bounds(left + 140, top, 120, 20).build());
        addRenderableWidget(Button.builder(toggle("gui.placeabletext.outline", outline), b -> { outline = !outline; b.setMessage(toggle("gui.placeabletext.outline", outline)); })
                .bounds(left, top + 24, 120, 20).build());
        addRenderableWidget(Button.builder(toggle("gui.placeabletext.shadow", shadow), b -> { shadow = !shadow; b.setMessage(toggle("gui.placeabletext.shadow", shadow)); })
                .bounds(left + 140, top + 24, 120, 20).build());
        addRenderableWidget(Button.builder(toggle("gui.placeabletext.phosphor", phosphor), b -> { phosphor = !phosphor; b.setMessage(toggle("gui.placeabletext.phosphor", phosphor)); })
                .bounds(left, top + 48, 120, 20).build());
        addRenderableWidget(Button.builder(lightLabel(), b -> { emitLight = !emitLight; b.setMessage(lightLabel()); })
                .bounds(left + 140, top + 48, 120, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Light level: " + lightLevel), b -> {
            lightLevel = lightLevel == 15 ? 1 : lightLevel + 1; b.setMessage(Component.literal("Light level: " + lightLevel));
        }).bounds(left, top + 72, 120, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.placeabletext.rotation", rotation * 90), b -> {
            rotation = (rotation + 1) % 4; b.setMessage(Component.translatable("gui.placeabletext.rotation", rotation * 90));
        }).bounds(left + 140, top + 72, 120, 20).build());

        textColorButton = addRenderableWidget(Button.builder(Component.empty(), b -> { colorTarget = 0; updateColorTargetButtons(); })
                .bounds(left, top + 100, 80, 20).build());
        backgroundColorButton = addRenderableWidget(Button.builder(Component.empty(), b -> { colorTarget = 1; updateColorTargetButtons(); })
                .bounds(left + 90, top + 100, 80, 20).build());
        outlineColorButton = addRenderableWidget(Button.builder(Component.empty(), b -> { colorTarget = 2; updateColorTargetButtons(); })
                .bounds(left + 180, top + 100, 80, 20).build());
        updateColorTargetButtons();

        for (int i = 0; i < COLORS.length; i++) {
            int index = i;
            Component swatch = Component.literal(COLOR_SHORT[i]).withStyle(Style.EMPTY.withColor(COLORS[i]));
            addRenderableWidget(Button.builder(swatch, b -> applyColor(index))
                    .bounds(left + (i % 8) * 32 + 2, top + 124 + (i / 8) * 20, 30, 18).build());
        }

        addRenderableWidget(Button.builder(Component.literal("H: " + format(horizontalOffset)), b -> {
            horizontalOffset = step(horizontalOffset); b.setMessage(Component.literal("H: " + format(horizontalOffset)));
        }).bounds(left, top + 168, 80, 20).build());
        addRenderableWidget(Button.builder(Component.literal("V: " + format(verticalOffset)), b -> {
            verticalOffset = step(verticalOffset); b.setMessage(Component.literal("V: " + format(verticalOffset)));
        }).bounds(left + 90, top + 168, 80, 20).build());
        addRenderableWidget(Button.builder(depthLabel(), b -> {
            depthOffset = nextDepth(); b.setMessage(depthLabel());
        }).bounds(left + 180, top + 168, 80, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.placeabletext.save"), b -> save())
                .bounds(width / 2 - 45, top + 192, 90, 20).build());
    }

    private void applyColor(int index) {
        if (colorTarget == 0) textColor = COLORS[index];
        else if (colorTarget == 1) backgroundColor = COLORS[index];
        else outlineColor = COLORS[index];
        updateColorTargetButtons();
    }

    private void updateColorTargetButtons() {
        textColorButton.setMessage(colorTargetLabel("Text", textColor, colorTarget == 0));
        backgroundColorButton.setMessage(colorTargetLabel("Background", backgroundColor, colorTarget == 1));
        outlineColorButton.setMessage(colorTargetLabel("Outline", outlineColor, colorTarget == 2));
    }

    private Component colorTargetLabel(String label, int color, boolean selected) {
        String name = "Custom";
        for (int i = 0; i < COLORS.length; i++) if (COLORS[i] == color) name = COLOR_NAMES[i];
        return Component.literal((selected ? "> " : "") + label + ": " + name);
    }

    private void save() {
        TextStyle result = new TextStyle(size, textColor, backgroundMode, backgroundColor, outline,
                outlineColor, shadow, phosphor, emitLight, lightLevel, rotation,
                horizontalOffset, verticalOffset, depthOffset);
        PacketDistributor.sendToServer(new UpdateStylePayload(target == null ? Long.MIN_VALUE : target.asLong(), result));
        onClose();
    }

    private float step(float value) { return value >= .4f ? -.4f : value + .1f; }
    private String format(float value) { return String.format("%.2f", value); }
    private float nextDepth() {
        if (depthOffset > 0.75f) return 0.5f;
        if (depthOffset > 0.25f) return 0.0f;
        return 1.0f;
    }
    private Component depthLabel() {
        String name = depthOffset > 0.75f ? "Flat" : depthOffset > 0.25f ? "Floating" : "Full";
        return Component.literal("Depth: " + name);
    }
    private Component sizeLabel() {
        return Component.translatable("gui.placeabletext.size", switch (size) { case 0 -> "Small"; case 2 -> "Large"; default -> "Medium"; });
    }
    private Component backgroundLabel() {
        String key = switch (backgroundMode) {
            case TextStyle.BACKGROUND_SOLID -> "gui.placeabletext.solid";
            case TextStyle.BACKGROUND_SEMI -> "gui.placeabletext.semi";
            default -> "gui.placeabletext.none";
        };
        return Component.translatable("gui.placeabletext.background", Component.translatable(key));
    }
    private Component lightLabel() { return toggle("gui.placeabletext.light", emitLight); }
    private Component toggle(String key, boolean value) {
        return Component.translatable(key, Component.translatable(value ? "gui.placeabletext.on" : "gui.placeabletext.off"));
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 124, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}