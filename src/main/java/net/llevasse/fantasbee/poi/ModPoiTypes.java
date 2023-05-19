package net.llevasse.fantasbee.poi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;

import net.llevasse.fantasbee.FantasBee;
import net.llevasse.fantasbee.block.ModBlocks;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPoiTypes extends PoiTypes{
	public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, FantasBee.MOD_ID);
	//public static final ResourceKey<PoiType> MYSTERIOUS_BEEHIVE = createKey("mysterious_beehive");
	
	public static final RegistryObject<PoiType> MYSTERIOUS_BEEHIVE = register("mysterious_beehive", () -> {
		List<RegistryObject<Block>> blocks = new ArrayList<>();
		blocks.add(ModBlocks.SUSPECISOUS_BEEHIVE_BLOCK);
		return blocks;
	}, 1);

	
	private static RegistryObject<PoiType> register(String name, RegistryObject<Block> block, int maxFreeTickets) {
		List<RegistryObject<Block>> blocks = new ArrayList<>();
		blocks.add(block);
		return register(name, blocks, maxFreeTickets);
	}

	private static RegistryObject<PoiType> register(String name, Supplier<List<RegistryObject<Block>>> supplier,
			int maxFreeTickets) {
		return register(name, supplier.get(), maxFreeTickets);
	}

	private static RegistryObject<PoiType> register(String name, List<RegistryObject<Block>> blocks,
			int maxFreeTickets) {
		return register(name, () -> {
			Set<BlockState> blockStates = new HashSet<>();
			for (RegistryObject<Block> block : blocks) {
				blockStates.addAll(getBlockStates(block));
			}
			return new PoiType(blockStates, maxFreeTickets, 1);
		});
	}

	private static RegistryObject<PoiType> register(String name, Supplier<PoiType> supplier) {
		return POI_TYPES.register(name, supplier);
	}
	
	// private static ResourceKey<PoiType> createKey(String p_218091_) {
	// 	return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, new ResourceLocation(p_218091_));
	// }
	
	// // private static Set<BlockState> getBlockStates(ModBlocks block) {
	// // 	return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
	// // }
	
	// private static PoiType register(Registry<PoiType> registry, ResourceKey<PoiType> Key,
	// 		Set<BlockState> blockstate, int maxTicket, int range) {
	// 	PoiType poitype = new PoiType(blockstate, maxTicket, range);
	// 	Registry.register(registry, Key, poitype);
	// 	registerBlockStates(registry.getHolderOrThrow(Key), blockstate);
	// 	return poitype;
	// }
	
	// private static void registerBlockStates(Holder<PoiType> p_250815_, Set<BlockState> p_250679_) {
	// }
	
	// public static PoiType bootstrap(Registry<PoiType> registry){
	// 	return register(registry, MYSTERIOUS_BEEHIVE, getBlockStates(ModBlocks.SUSPECISOUS_BEEHIVE_BLOCK), 0, 1);
	// }

	public static Set<BlockState> getBlockStates(RegistryObject<Block> block) {
		return ImmutableSet.copyOf(block.get().getStateDefinition().getPossibleStates());
	}

	public static void register(IEventBus modEventBus) {
	}
}
