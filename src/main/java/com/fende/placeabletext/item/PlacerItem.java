package com.fende.placeabletext.item;

import com.fende.placeabletext.TextStyle;
import com.fende.placeabletext.block.TextAnchorBlock;
import com.fende.placeabletext.block.entity.TextAnchorBlockEntity;
import com.fende.placeabletext.client.ClientAccess;
import com.fende.placeabletext.registry.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public final class PlacerItem extends Item {
    public enum Category {
        LETTER("ABCDEFGHIJKLMNOPQRSTUVWXYZ "),
        NUMBER("0123456789 "),
        SPECIAL(".|,|!|?|:|;|'|\"|-|_|+|=|/|\\\\|(|)|[|]|<|>|#|@|&|%|*|\u2190|\u2192|\u2191|\u2193| ");

        private final String[] values;
        Category(String characters) {
            values = characters.contains("|") ? characters.split("\\|", -1)
                    : characters.chars().mapToObj(c -> Character.toString(c)).toArray(String[]::new);
        }
        public String at(int index) { return values[Math.floorMod(index, values.length)]; }
        public int count() { return values.length; }
    }

    private final Category category;
    public PlacerItem(Category category, Properties properties) { super(properties); this.category = category; }
    public Category category() { return category; }
    public String selected(ItemStack stack) { return category.at(index(stack)); }
    public String selectedLabel(ItemStack stack) { return selected(stack).isBlank() ? "Space" : selected(stack); }

    public static int index(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("SelectedIndex");
    }

    public void cycle(ItemStack stack, int delta) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("SelectedIndex", Math.floorMod(index(stack) + delta, category.count()));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            if (level.isClientSide) ClientAccess.openEditor(TextStyle.fromItem(stack), null);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        BlockPos anchorPos = context.getClickedPos().relative(context.getClickedFace());
        if (!level.getBlockState(anchorPos).canBeReplaced()) return InteractionResult.FAIL;
        TextStyle style = TextStyle.fromItem(stack);
        BlockState state = ModBlocks.TEXT_ANCHOR.get().defaultBlockState()
                .setValue(TextAnchorBlock.ATTACHED_FACE, context.getClickedFace())
                .setValue(TextAnchorBlock.LIGHT, style.emitLight() ? style.lightLevel() : 0);
        if (!level.isClientSide) {
            level.setBlock(anchorPos, state, 3);
            if (level.getBlockEntity(anchorPos) instanceof TextAnchorBlockEntity text) {
                text.initialize(selected(stack), category, style, context.getClickedFace(),
                        context.getPlayer() == null ? null : context.getPlayer().getUUID());
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Selected: " + selectedLabel(stack)).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.placeabletext.scroll").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.placeabletext.place").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.placeabletext.customize").withStyle(ChatFormatting.GRAY));
    }
}
