package net.llevasse.fantasbee.entities.block_entities;

import net.llevasse.fantasbee.FantasBee;
import net.llevasse.fantasbee.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntities {
	// For some DeferredRegister<BlockEntityType<?>> REGISTER
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FantasBee.MOD_ID);

	public static final RegistryObject<BlockEntityType<mysterious_beehive_block_entity>> MYSTERIOUS_BEEHIVE_BLOCK_ENTITY = BLOCK_ENTITY_TYPE
			.register("mysterious_beehive_block",
					() -> BlockEntityType.Builder
							.of(mysterious_beehive_block_entity::new, ModBlocks.SUSPECISOUS_BEEHIVE_BLOCK.get())
							.build(null));

	public static void register(IEventBus modEventBus) {
		BLOCK_ENTITY_TYPE.register(modEventBus);
	}
}