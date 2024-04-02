package com.elevasse.fantasbee.blockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MysteriousBeehiveEntity extends BlockEntity {

    int testNbt;
    public MysteriousBeehiveEntity(BlockPos pos, BlockState state) {
        super(RefBlockEntity.MYSTERIOUS_BEEHIVE.get(), pos, state);
    }

    public void increase(){
        testNbt++;
    }

    public int getTestNbt() {
        return testNbt;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("testNbt", this.testNbt);
        super.saveAdditional(tag);
        System.out.println("Saving");
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.testNbt = tag.getInt("testNbt");
        System.out.println("Loading");
    }
}
