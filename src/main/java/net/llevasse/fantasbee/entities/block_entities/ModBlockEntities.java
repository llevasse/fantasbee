package net.llevasse.fantasbee.entities.block_entities;

import net.llevasse.fantasbee.FantasBee;
import net.llevasse.fantasbee.block.ModBlocks;
import net.llevasse.fantasbee.entities.MysteriousBee;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities{
	private static final EntityDataAccessor<ItemStack> DATA_PRODUCE = SynchedEntityData.defineId(MysteriousBee.class,
			EntityDataSerializers.ITEM_STACK);
	
	public static final DeferredRegister<BlockEntityType<?>> MOD_BLOCK_ENTITY_TYPE = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FantasBee.MOD_ID);

	public static final RegistryObject<BlockEntityType<MysteriousBeehiveBlockEntity>> MYSTERIOUS_BEEHIVE_BLOCK_ENTITY = MOD_BLOCK_ENTITY_TYPE
			.register("mysterious_beehive_block",
					() -> BlockEntityType.Builder
							.of(MysteriousBeehiveBlockEntity::new, ModBlocks.SUSPECISOUS_BEEHIVE_BLOCK.get())
							.build(null));

	public static void register(IEventBus modEventBus) {
		MOD_BLOCK_ENTITY_TYPE.register(modEventBus);
	}
}