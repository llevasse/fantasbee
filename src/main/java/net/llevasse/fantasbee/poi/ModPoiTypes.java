package net.llevasse.fantasbee.poi;

import net.minecraft.core.registries.Registries;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.llevasse.fantasbee.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPoiTypes extends PoiTypes{
	public static final ResourceKey<PoiType> MYSTERIOUS_BEEHIVE = createKey("mysterious_beehive");
	
	
	private static ResourceKey<PoiType> createKey(String p_218091_) {
		return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, new ResourceLocation(p_218091_));
	}
	
	private static Set<BlockState> getBlockStates(Block block) {
		return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
	}
	
	private static PoiType register(Registry<PoiType> p_218085_, ResourceKey<PoiType> p_218086_,
			Set<BlockState> p_218087_, int p_218088_, int p_218089_) {
		PoiType poitype = new PoiType(p_218087_, p_218088_, p_218089_);
		Registry.register(p_218085_, p_218086_, poitype);
		registerBlockStates(p_218085_.getHolderOrThrow(p_218086_), p_218087_);
		return poitype;
	}
	
	private static void registerBlockStates(Holder<PoiType> p_250815_, Set<BlockState> p_250679_) {
	}
	
	public static PoiType bootstrap(Registry<PoiType> registry){
		return register(registry, MYSTERIOUS_BEEHIVE, getBlockStates(ModBlocks.SUSPECISOUS_BEEHIVE_BLOCK), 0, 0)
		
	}
}
