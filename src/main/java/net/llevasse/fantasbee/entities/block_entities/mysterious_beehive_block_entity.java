package net.llevasse.fantasbee.entities.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class mysterious_beehive_block_entity extends BlockEntity {

	public mysterious_beehive_block_entity(BlockPos pos, BlockState state) {
  		super(BlockEntities.MYSTERIOUS_BEEHIVE_BLOCK_ENTITY.get(), pos, state);
	}
}
