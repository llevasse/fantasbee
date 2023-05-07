package net.llevasse.fantasbee.block.custom;

import net.llevasse.fantasbee.entities.block_entities.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class suspecious_beehive_block extends base_orientable_block implements EntityBlock{
	public suspecious_beehive_block(Properties properties){
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlockEntities.MYSTERIOUS_BEEHIVE_BLOCK_ENTITY.get().create(pos, state);
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
