package com.elevasse.fantasbee.blockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MysteriousBeehiveEntity extends BlockEntity {
    public MysteriousBeehiveEntity(BlockPos pos, BlockState state) {
        super(RefBlockEntity.MYSTERIOUS_BEEHIVE.get(), pos, state);
    }
}
