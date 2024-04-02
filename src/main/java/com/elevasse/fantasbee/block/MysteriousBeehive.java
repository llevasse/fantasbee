package com.elevasse.fantasbee.block;

import com.elevasse.fantasbee.blockEntity.MysteriousBeehiveEntity;
import com.elevasse.fantasbee.blockEntity.RefBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MysteriousBeehive extends Block implements EntityBlock {
    public MysteriousBeehive(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        System.out.println("CC from use");
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof MysteriousBeehiveEntity) {
                if (hand == InteractionHand.MAIN_HAND) {
                    ((MysteriousBeehiveEntity) entity).increase();
                    System.out.println(((MysteriousBeehiveEntity) entity).getTestNbt());
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.use(blockstate, level, pos, player, hand, result);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return RefBlockEntity.MYSTERIOUS_BEEHIVE.get().create(blockPos, blockState);
    }
}
