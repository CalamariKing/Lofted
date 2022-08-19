package com.loftedstudios.loftedmod.blocks.custom;

import com.loftedstudios.loftedmod.blocks.LoftedBlocks;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Random;

public class BranchBlock extends ConnectingBlock {
    public static final BooleanProperty LEAVES_NORTH = BooleanProperty.of("leaves_north");
    public static final BooleanProperty LEAVES_EAST = BooleanProperty.of("leaves_east");
    public static final BooleanProperty LEAVES_SOUTH = BooleanProperty.of("leaves_south");
    public static final BooleanProperty LEAVES_WEST = BooleanProperty.of("leaves_west");
    public static final BooleanProperty LEAVES_UP = BooleanProperty.of("leaves_up");
    public static final BooleanProperty LEAVES_DOWN = BooleanProperty.of("leaves_down");
    /**
     * Access widened by fabric-transitive-access-wideners-v1 to accessible
     *
     * @param radius
     * @param settings
     */
    protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);

    public BranchBlock(float radius, Settings settings) {
        super(0.5f, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)(BlockState)(BlockState)this.stateManager.getDefaultState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(UP, false).with(DOWN, false));

    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public boolean canLeavesConnect(BlockState state, Direction dir) {
        Block block = state.getBlock();
        return this.canConnectToLeaves(state);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                return (BlockState) ((BlockState) ((BlockState) ((BlockState) state.with(NORTH, state.get(SOUTH))).with(EAST, state.get(WEST))).with(SOUTH, state.get(NORTH))).with(WEST, state.get(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState) ((BlockState) ((BlockState) ((BlockState) state.with(NORTH, state.get(EAST))).with(EAST, state.get(SOUTH))).with(SOUTH, state.get(WEST))).with(WEST, state.get(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState) ((BlockState) ((BlockState) ((BlockState) state.with(NORTH, state.get(WEST))).with(EAST, state.get(NORTH))).with(SOUTH, state.get(EAST))).with(WEST, state.get(SOUTH));
            }
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)state.with(NORTH, state.get(SOUTH))).with(SOUTH, state.get(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)state.with(EAST, state.get(WEST))).with(WEST, state.get(EAST));
            }
        }
        return super.mirror(state, mirror);
    }

    public boolean canConnect(BlockState state, Direction dir) {
        Block block = state.getBlock();
        return this.canConnectToBranch(state);
    }
    private boolean canConnectToBranch(BlockState state) {
        return state.getBlock() == LoftedBlocks.FEYWOOD_BRANCH || state.isIn(BlockTags.LEAVES) || state.isIn(BlockTags.LOGS) || state.isOf(LoftedBlocks.LOFTED_GRASS) || state.isOf(LoftedBlocks.MINSTONE);
    }

    private boolean canConnectToLeaves(BlockState state) {
        return state.isOf(Blocks.OAK_LEAVES);
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.east();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();
        BlockPos blockPos6 = blockPos.up();
        BlockPos blockPos7 = blockPos.down();
        BlockState blockState = blockView.getBlockState(blockPos2);
        BlockState blockState2 = blockView.getBlockState(blockPos3);
        BlockState blockState3 = blockView.getBlockState(blockPos4);
        BlockState blockState4 = blockView.getBlockState(blockPos5);
        BlockState blockState5 = blockView.getBlockState(blockPos6);
        BlockState blockState6 = blockView.getBlockState(blockPos7);
        return super.getPlacementState(ctx).with(NORTH, this.canConnect(blockState, Direction.SOUTH)).with(EAST, this.canConnect(blockState2, Direction.WEST)).with(SOUTH, this.canConnect(blockState3,  Direction.NORTH)).with(WEST, this.canConnect(blockState4,  Direction.EAST)).with(UP, this.canConnect(blockState5, Direction.DOWN)).with(DOWN, this.canConnect(blockState6, Direction.UP))
                .with(LEAVES_NORTH, this.canLeavesConnect(blockState, Direction.SOUTH)).with(LEAVES_EAST, this.canLeavesConnect(blockState2, Direction.WEST)).with(LEAVES_SOUTH, this.canLeavesConnect(blockState3,  Direction.NORTH)).with(LEAVES_WEST, this.canLeavesConnect(blockState4,  Direction.EAST)).with(LEAVES_UP, this.canLeavesConnect(blockState5, Direction.DOWN)).with(LEAVES_DOWN, this.canLeavesConnect(blockState6, Direction.UP));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis().getType() == Direction.Type.HORIZONTAL) {
            return (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), this.canConnect(neighborState, direction.getOpposite()));
        }
        if (direction.getAxis().getType() == Direction.Type.VERTICAL) {
            return (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), this.canConnect(neighborState, direction.getOpposite()));
        }
        if (direction.getAxis().getType() == Direction.Type.HORIZONTAL) {
            return (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), this.canLeavesConnect(neighborState, direction.getOpposite()));
        }
        if (direction.getAxis().getType() == Direction.Type.VERTICAL) {
            return (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), this.canLeavesConnect(neighborState, direction.getOpposite()));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN, LEAVES_NORTH, LEAVES_EAST, LEAVES_SOUTH, LEAVES_WEST, LEAVES_UP, LEAVES_DOWN);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
