package net.llevasse.fantasbee.block.custom;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition.Builder;

public class base_orientable_block extends Block{
	public static final DirectionProperty DIRECTION = BlockStateProperties.HORIZONTAL_FACING;
	
	public base_orientable_block(Properties properties) {
		super(properties);
		this.registerDefaultState(
				this.stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation((Direction) state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return (BlockState) state.setValue(BlockStateProperties.HORIZONTAL_FACING,
				rotation.rotate((Direction) state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return (BlockState) this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING,
				ctx.getHorizontalDirection().getOpposite());
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(DIRECTION);
	}
	
}
