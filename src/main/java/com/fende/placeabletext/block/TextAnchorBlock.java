package com.fende.placeabletext.block;

import com.fende.placeabletext.block.entity.TextAnchorBlockEntity;
import com.fende.placeabletext.client.ClientAccess;
import com.fende.placeabletext.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public final class TextAnchorBlock extends BaseEntityBlock {
    public static final MapCodec<TextAnchorBlock> CODEC = simpleCodec(TextAnchorBlock::new);
    public static final DirectionProperty ATTACHED_FACE = DirectionProperty.create("attached_face");
    public static final IntegerProperty LIGHT = IntegerProperty.create("light", 0, 15);

    public TextAnchorBlock() {
        this(BlockBehaviour.Properties.of().noCollission().noOcclusion().strength(0.1f)
                .lightLevel(state -> state.getValue(LIGHT)));
    }

    private TextAnchorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ATTACHED_FACE, Direction.NORTH).setValue(LIGHT, 0));
    }

    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(ATTACHED_FACE, LIGHT);
    }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(ATTACHED_FACE)) {
            case DOWN -> box(1, 0, 1, 15, 1, 15);
            case UP -> box(1, 15, 1, 15, 16, 15);
            case NORTH -> box(1, 1, 0, 15, 15, 1);
            case SOUTH -> box(1, 1, 15, 15, 15, 16);
            case WEST -> box(0, 1, 1, 1, 15, 15);
            case EAST -> box(15, 1, 1, 16, 15, 15);
        };
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new TextAnchorBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) { return null; }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                          Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof TextAnchorBlockEntity text) {
            if (level.isClientSide) ClientAccess.openEditor(text.style(), pos);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                               net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction face = state.getValue(ATTACHED_FACE);
        if (direction == face.getOpposite() && !neighborState.isFaceSturdy(level, neighborPos, face)) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return state;
    }
}

