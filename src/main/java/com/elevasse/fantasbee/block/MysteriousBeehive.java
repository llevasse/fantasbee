package com.elevasse.fantasbee.block;

import com.elevasse.fantasbee.blockEntity.MysteriousBeehiveEntity;
import com.elevasse.fantasbee.blockEntity.RefBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MysteriousBeehive extends Block implements EntityBlock {
    public static final IntegerProperty HONEY_LEVEL = IntegerProperty.create("honey_level", 0, 25);
    public static final IntegerProperty HIVE_LEVEL = IntegerProperty.create("hive_level", 0, 4);
    public static final BooleanProperty HAS_HONEY = BooleanProperty.create("has_honey");
    public static Property<Direction> FACING = DirectionProperty.create("facing");

    public MysteriousBeehive(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HONEY_LEVEL, 0));
        this.registerDefaultState(this.stateDefinition.any().setValue(HIVE_LEVEL, 0));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_HONEY, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!level.isClientSide()) {
            ItemStack held = player.getItemInHand(hand);
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof MysteriousBeehiveEntity) {
                if (hand == InteractionHand.MAIN_HAND) {
                    if (held.is(Items.AIR)) {
                        System.out.printf("Current honey level : %d\n", MysteriousBeehiveEntity.getHoneyLevel(blockstate));
                        System.out.printf("Current production : %s\n", (((MysteriousBeehiveEntity) entity).getCurrentProduction().getDisplayName().getString()));
                        System.out.printf("Current hive level : %d\n", blockstate.getValue(MysteriousBeehive.HIVE_LEVEL));
                    }
                    else if (held.is(Items.SHEARS)){
                        if (blockstate.getValue(HAS_HONEY)) {
                            int hLvl = ((MysteriousBeehiveEntity) entity).getMaxHoneyLevel();
                            for (int i = hLvl / 5; i > 0; i--) {
                                popResource(level, pos, new ItemStack(((MysteriousBeehiveEntity) entity).getCurrentProduction().getItem()));
                                hLvl -= 5;
                            }
                            level.setBlockAndUpdate(pos, blockstate.setValue(MysteriousBeehive.HONEY_LEVEL, hLvl));
                            level.setBlockAndUpdate(pos, blockstate.setValue(MysteriousBeehive.HAS_HONEY, false));
                        }
                    }
                    else if (held.is(Items.COPPER_INGOT) && blockstate.getValue(MysteriousBeehive.HIVE_LEVEL) == 0){
                        level.setBlockAndUpdate(pos, blockstate.setValue(MysteriousBeehive.HIVE_LEVEL, 1));
                        ((MysteriousBeehiveEntity) entity).setMaxHoneyLevel(10);
                    }
                    else if (held.is(Items.IRON_INGOT) && blockstate.getValue(MysteriousBeehive.HIVE_LEVEL) == 1){
                        level.setBlockAndUpdate(pos, blockstate.setValue(MysteriousBeehive.HIVE_LEVEL, 2));
                        ((MysteriousBeehiveEntity) entity).setMaxHoneyLevel(15);
                        ((MysteriousBeehiveEntity) entity).setMaxOccupants(6);
                    }
                    else if (held.is(Items.GOLD_INGOT) && blockstate.getValue(MysteriousBeehive.HIVE_LEVEL) == 2){
                        level.setBlockAndUpdate(pos, blockstate.setValue(MysteriousBeehive.HIVE_LEVEL, 3));
                        ((MysteriousBeehiveEntity) entity).setMaxHoneyLevel(20);
                    }
                    else if (held.is(Items.DIAMOND) && blockstate.getValue(MysteriousBeehive.HIVE_LEVEL) == 3){
                        level.setBlockAndUpdate(pos, blockstate.setValue(MysteriousBeehive.HIVE_LEVEL, 4));
                        ((MysteriousBeehiveEntity) entity).setMaxHoneyLevel(25);
                        ((MysteriousBeehiveEntity) entity).setMaxOccupants(7);
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
        return RefBlockEntity.MYSTERIOUS_BEEHIVE.get().create(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(HONEY_LEVEL);
        blockStateBuilder.add(HIVE_LEVEL);
        blockStateBuilder.add(FACING);
        blockStateBuilder.add(HAS_HONEY);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == RefBlockEntity.MYSTERIOUS_BEEHIVE.get() ? MysteriousBeehiveEntity::serverTick : null;
    }
}
