package net.llevasse.fantasbee.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;


public class suspecious_beehive_block extends Block{
	public static final DirectionProperty DIRECTION = DirectionProperty.create("Direction");
	
	public suspecious_beehive_block(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player,
			InteractionHand hand, BlockHitResult result) {
		if(!level.isClientSide() && hand == InteractionHand.MAIN_HAND){
			level.setBlock(blockPos, state.cycle(DIRECTION), 3);
		}
		return super.use(state, level, blockPos, player, hand, result);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(DIRECTION);
	}
}
