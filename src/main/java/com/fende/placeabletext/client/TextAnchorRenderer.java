package com.fende.placeabletext.client;

import com.fende.placeabletext.TextStyle;
import com.fende.placeabletext.block.entity.TextAnchorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public final class TextAnchorRenderer implements BlockEntityRenderer<TextAnchorBlockEntity> {
    private static final String PANEL_TEXT = "        ";
    private static final float FULL_DEPTH_FORWARD_NUDGE = -0.01f;
    private final Font font;
    public TextAnchorRenderer(BlockEntityRendererProvider.Context context) { font = context.getFont(); }

    @Override public void render(TextAnchorBlockEntity text, float partialTick, PoseStack pose,
                                 MultiBufferSource buffers, int packedLight, int packedOverlay) {
        String value = text.character();
        if (value == null || value.isEmpty()) return;
        TextStyle style = text.style();
        pose.pushPose();
        pose.translate(0.5, 0.5, 0.5);
        orient(pose, text.surface());
        float forwardNudge = style.depthOffset() < 0.25f ? FULL_DEPTH_FORWARD_NUDGE : 0.0f;
        pose.translate(0, 0, 0.5f - style.depthOffset() - 0.002f + forwardNudge);
        if (style.backgroundMode() != TextStyle.BACKGROUND_NONE) {
            renderPanel(pose, buffers, style, packedLight);
        }
        pose.translate(style.horizontalOffset(), -style.verticalOffset(), 0.002f);
        pose.mulPose(Axis.ZP.rotationDegrees(style.rotation() * 90f));

        float scale = switch (style.size()) { case 0 -> 0.055f; case 2 -> 0.13f; default -> 0.085f; };
        pose.scale(-scale, -scale, scale);
        float x = -font.width(value) / 2f;
        float y = -font.lineHeight / 2f;
        int glyphLight = style.phosphor() ? 0x00F000F0 : packedLight;

        if (style.outline()) {
            int outline = 0xFF000000 | style.outlineColor();
            font.drawInBatch(value, x - 1, y, outline, false, pose.last().pose(), buffers, Font.DisplayMode.POLYGON_OFFSET, 0, glyphLight);
            font.drawInBatch(value, x + 1, y, outline, false, pose.last().pose(), buffers, Font.DisplayMode.POLYGON_OFFSET, 0, glyphLight);
            font.drawInBatch(value, x, y - 1, outline, false, pose.last().pose(), buffers, Font.DisplayMode.POLYGON_OFFSET, 0, glyphLight);
            font.drawInBatch(value, x, y + 1, outline, false, pose.last().pose(), buffers, Font.DisplayMode.POLYGON_OFFSET, 0, glyphLight);
        }
        font.drawInBatch(value, x, y, 0xFF000000 | style.textColor(), style.shadow(),
                pose.last().pose(), buffers, Font.DisplayMode.POLYGON_OFFSET, 0, glyphLight);
        pose.popPose();
    }

    private void renderPanel(PoseStack pose, MultiBufferSource buffers, TextStyle style, int packedLight) {
        int panelWidth = font.width(PANEL_TEXT);
        float scaleX = 1.02f / (panelWidth + 2.0f);
        float scaleY = 1.02f / (font.lineHeight + 2.0f);
        int alpha = style.backgroundMode() == TextStyle.BACKGROUND_SOLID ? 0xFF000000 : 0x66000000;
        pose.pushPose();
        pose.scale(-scaleX, -scaleY, 1.0f);
        font.drawInBatch(PANEL_TEXT, -panelWidth / 2.0f, -font.lineHeight / 2.0f,
                0x00FFFFFF, false, pose.last().pose(), buffers,
                Font.DisplayMode.POLYGON_OFFSET, alpha | style.backgroundColor(), packedLight);
        pose.popPose();
    }
    private static void orient(PoseStack pose, Direction face) {
        switch (face) {
            case NORTH -> {}
            case SOUTH -> pose.mulPose(Axis.YP.rotationDegrees(180));
            case EAST -> pose.mulPose(Axis.YP.rotationDegrees(-90));
            case WEST -> pose.mulPose(Axis.YP.rotationDegrees(90));
            case UP -> pose.mulPose(Axis.XP.rotationDegrees(90));
            case DOWN -> pose.mulPose(Axis.XP.rotationDegrees(-90));
        }
    }

    @Override public int getViewDistance() { return 96; }
}