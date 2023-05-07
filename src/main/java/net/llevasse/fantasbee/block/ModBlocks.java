package net.llevasse.fantasbee.block;

import com.google.common.base.Supplier;
import net.llevasse.fantasbee.FantasBee;
import net.llevasse.fantasbee.block.custom.dead_suspecious_beehive_block;
import net.llevasse.fantasbee.block.custom.suspecious_beehive_block;
import net.llevasse.fantasbee.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			FantasBee.MOD_ID);

	public static final RegistryObject<Block> DEAD_SUSPECISOUS_BEEHIVE_BLOCK = registerBlock("dead_suspecious_beehive_block", 
	() -> new dead_suspecious_beehive_block(BlockBehaviour.Properties.copy(Blocks.BEEHIVE)));
	
	public static final RegistryObject<Block> SUSPECISOUS_BEEHIVE_BLOCK = registerBlock(
			"suspecious_beehive_block",
			() -> new suspecious_beehive_block(
					BlockBehaviour.Properties.copy(Blocks.BEEHIVE).lightLevel((Properties) -> {
						return 14;
					})));

	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn);
		return toReturn;
	}

	private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
		return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
				new Item.Properties()));
	}

	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}
}
