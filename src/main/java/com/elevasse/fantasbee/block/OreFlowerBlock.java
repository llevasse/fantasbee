package com.elevasse.fantasbee.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class OreFlowerBlock extends FlowerBlock {
    private TagKey<Block> blockMayPlaceOn;

    public OreFlowerBlock(MobEffect mobEffect, int effectDuration, Properties properties, TagKey<Block> tag) {
        super(mobEffect, effectDuration, properties);
        blockMayPlaceOn = tag;
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos pos) {
        return (blockState.is(blockMayPlaceOn) || blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.DIRT));
    }
}
