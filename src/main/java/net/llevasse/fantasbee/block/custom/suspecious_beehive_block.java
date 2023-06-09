package net.llevasse.fantasbee.block.custom;

import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.llevasse.fantasbee.entities.block_entities.ModBlockEntities;
import net.llevasse.fantasbee.entities.block_entities.MysteriousBeehiveBlockEntity;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class suspecious_beehive_block extends Block implements EntityBlock {
	public static final IntegerProperty LEVEL_HONEY = BlockStateProperties.LEVEL_HONEY;
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public suspecious_beehive_block(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL_HONEY, Integer.valueOf(0)));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MysteriousBeehiveBlockEntity(pos, state);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState p_60550_) {
		return RenderShape.MODEL;
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	public static void dropProduct(Level lvl, BlockPos pos) {
		BlockEntity entity = lvl.getBlockEntity(pos);
		if (entity instanceof MysteriousBeehiveBlockEntity blockEntity) {
			popResource(lvl, pos, new ItemStack(blockEntity.getProduct(), 1));
			blockEntity.ItemProduced++;
		}
	}

	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level lvl, BlockState state,
			BlockEntityType<T> type) {
		return type == ModBlockEntities.MYSTERIOUS_BEEHIVE_BLOCK_ENTITY.get() ? (level, pos, blockstate, entity) -> {
			if (entity instanceof MysteriousBeehiveBlockEntity mysterious_beehive_block)
				mysterious_beehive_block.serverTick(lvl, pos, state, mysterious_beehive_block);
		} : null;
	}

	public InteractionResult use(BlockState state, Level lvl, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hitResult) {
		ItemStack handItem = player.getItemInHand(hand);
		int i = state.getValue(LEVEL_HONEY);
		Item item = handItem.getItem();
		// System.out.printf("\n\nfantasbee : %s used\n\n",
		// item.getName(handItem).toString());
		if (item == Items.DIAMOND_AXE) {
			dropProduct(lvl, pos);
			return InteractionResult.sidedSuccess(lvl.isClientSide);
		} else if (item == Items.AIR) {
			BlockEntity entity = lvl.getBlockEntity(pos);
			if (entity instanceof MysteriousBeehiveBlockEntity blockEntity) {
				blockEntity.setChanged();
				System.out.printf("\n\nfantasbee : This hive produce %s\n\n",
						blockEntity.getProduct().getClass().getName());
				System.out.printf("\n\nfantasbee : This hive countains %d bees\n\n",
						blockEntity.getOccupantCount());
				System.out.printf("\n\nfantasbee : this hive produced %d times\n\n", blockEntity.ItemProduced);
			}
			return InteractionResult.sidedSuccess(lvl.isClientSide);
		}
		return InteractionResult.FAIL;
		// else{
		// return super.use(state, lvl, pos, player, hand, hitResult);
		// }
	}

	public static boolean hiveContainsBees(Level p_49655_, BlockPos p_49656_) {
		BlockEntity blockentity = p_49655_.getBlockEntity(p_49656_);
		if (blockentity instanceof MysteriousBeehiveBlockEntity beehiveblockentity) {
			return !beehiveblockentity.isEmpty();
		} else {
			return false;
		}
	}

	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(LEVEL_HONEY);
	}

}

// All nbt data possible for an item_frame

/*
 * summon minecraft:item_frame x y z {
 * Motion: [0.0d, 0.0d, 0.0d], //3 TAG_Doubles describing the current dX, dY and
 * dZ velocity of the entity in meters per tick.
 *
 * Facing: 3b, // The direction the painting/item frame faces: 3 is south, 4 is
 * west, 2 is north, 5 is east, 1 is top, and 0 is bottom.
 *
 * ItemRotation: 0b, //The number of times the item has been rotated 45 degrees
 * clockwise.
 *
 * Invulnerable: 0b,
 *
 * Air: 300s, //How much air the entity has, in ticks. Decreases by 1 per tick
 * when unable to breathe (except suffocating in a block). Increase by 1 per
 * tick when it can breathe. If -20 while still unable to breathe, the entity
 * loses 1 health and its air is reset to 0. Most mobs can have a maximum of 300
 * in air, while dolphins can reach up to 4800, and axolotls have 6000.
 *
 * OnGround: 0b,
 *
 * PortalCooldown: 0,
 *
 * Rotation: [0.0f, 0.0f],
 *
 * FallDistance: 0.0f,
 *
 * Item: {id: "minecraft:netherite_axe", Count: 1b, tag: {Damage: 1}},
 *
 * ItemDropChance: 1.0f, // The chance for the item to drop when the item frame
 * breaks. 1.0 by default.
 *
 * CanUpdate: 1b,
 *
 * Fire: -1s,
 *
 * TileY: 116, // The Y coordinate of the block the painting/item frame is in.
 * TileX: -33, // The X coordinate of the block the painting/item frame is in.
 * TileZ: 9, // The Z coordinate of the block the painting/item frame is in.
 * 
 * Invisible: 0b, //1 or 0 (true/false) - Whether the item frame is invisible.
 * The contained item or map remains visible.
 *
 * Fixed: 0b // 1 or 0 (true/false) - true to prevent it from dropping if it has
 * no support block, being moved (e.g. by pistons), taking damage (except from
 * creative players), and placing an item in it, removing its item, or rotating
 * it.}
 */
