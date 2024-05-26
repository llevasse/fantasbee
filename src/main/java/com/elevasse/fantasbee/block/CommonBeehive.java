package com.elevasse.fantasbee.block;

import com.elevasse.fantasbee.blockEntity.CommonBeehiveEntity;
import com.elevasse.fantasbee.blockEntity.RefBlockEntity;
import com.elevasse.fantasbee.item.RefItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CommonBeehive extends Block implements EntityBlock {
    public static final IntegerProperty HONEY_LEVEL = IntegerProperty.create("honey_level", 0, 5);
    public static final DirectionProperty FACING;
    //public static Property<Direction> FACING = DirectionProperty.create("facing");

    public CommonBeehive(Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HONEY_LEVEL, 0)).setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return (BlockState)blockState.setValue(FACING, rotation.rotate((Direction)blockState.getValue(FACING)));
    }

    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation((Direction)blockState.getValue(FACING)));
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!level.isClientSide()) {
            ItemStack held = player.getItemInHand(hand);
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof CommonBeehiveEntity) {
                if (hand == InteractionHand.MAIN_HAND) {
                    if (held.is(Items.AIR)) {
                        System.out.printf("Current honey level : %d\n", CommonBeehiveEntity.getHoneyLevel(blockstate));
                        System.out.printf("Current production : %s\n", (((CommonBeehiveEntity) entity).getCurrentProduction().getDisplayName().getString()));
                    }
                    else if (held.is(Items.SHEARS)){
                        if (blockstate.getValue(HONEY_LEVEL) >= 5) {
                            int hLvl = ((CommonBeehiveEntity) entity).getMaxHoneyLevel();
                            for (int i = hLvl / 5; i > 0; i--) {
                                popResource(level, pos, new ItemStack(((CommonBeehiveEntity) entity).getCurrentProduction().getItem()));
                                hLvl -= 5;
                            }
                            level.setBlockAndUpdate(pos, blockstate.setValue(CommonBeehive.HONEY_LEVEL, hLvl));
                        }
                    }
                    else if (held.is(RefItems.COPPER_UPGRADE.get())){
                        level.setBlockAndUpdate(pos, RefBlocks.COPPER_BEEHIVE.get().defaultBlockState().setValue(CopperBeehive.HONEY_LEVEL, blockstate.getValue(HONEY_LEVEL)).setValue(CopperBeehive.FACING, blockstate.getValue(FACING)));
                        ((CommonBeehiveEntity) entity).setMaxHoneyLevel(10);
                    }
                    else if (held.is(RefItems.IRON_UPGRADE.get())){
                        level.setBlockAndUpdate(pos, RefBlocks.IRON_BEEHIVE.get().defaultBlockState().setValue(IronBeehive.HONEY_LEVEL, blockstate.getValue(HONEY_LEVEL)).setValue(IronBeehive.FACING, blockstate.getValue(FACING)));
                        ((CommonBeehiveEntity) entity).setMaxHoneyLevel(15);
                    }
                    else if (held.is(RefItems.GOLD_UPGRADE.get())){
                        level.setBlockAndUpdate(pos, RefBlocks.GOLD_BEEHIVE.get().defaultBlockState().setValue(GoldBeehive.HONEY_LEVEL, blockstate.getValue(HONEY_LEVEL)).setValue(GoldBeehive.FACING, blockstate.getValue(FACING)));
                        ((CommonBeehiveEntity) entity).setMaxHoneyLevel(20);
                    }
                    else if (held.is(RefItems.DIAMOND_UPGRADE.get())){
                        level.setBlockAndUpdate(pos, RefBlocks.DIAMOND_BEEHIVE.get().defaultBlockState().setValue(DiamondBeehive.HONEY_LEVEL, blockstate.getValue(HONEY_LEVEL)).setValue(DiamondBeehive.FACING, blockstate.getValue(FACING)));
                        ((CommonBeehiveEntity) entity).setMaxHoneyLevel(25);
                    }
                    else
                        return InteractionResult.FAIL;
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.use(blockstate, level, pos, player, hand, result);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return RefBlockEntity.COMMON_BEEHIVE.get().create(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(new Property[]{HONEY_LEVEL, FACING});
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == RefBlockEntity.COMMON_BEEHIVE.get() ? CommonBeehiveEntity::serverTick : null;
    }
    static {
        FACING = HorizontalDirectionalBlock.FACING;
    }
}
