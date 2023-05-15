package net.llevasse.fantasbee;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;

import net.llevasse.fantasbee.block.ModBlocks;
import net.llevasse.fantasbee.block.custom.suspecious_beehive_block;
import net.llevasse.fantasbee.entities.ModEntities;
import net.llevasse.fantasbee.entities.MysteriousBee;
import net.llevasse.fantasbee.entities.block_entities.ModBlockEntities;
import net.llevasse.fantasbee.entities.block_entities.MysteriousBeehiveBlockEntity;
import net.llevasse.fantasbee.item.ModCreativeModeTabs;
import net.llevasse.fantasbee.item.ModItems;
import net.llevasse.fantasbee.poi.ModPoiTypes;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@SuppressWarnings("unused")
@Mod(FantasBee.MOD_ID)
public class FantasBee {
    public static final String MOD_ID = "fantasbee";
    private static final Logger LOGGER = LogUtils.getLogger();

    public FantasBee() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        // ModPoiTypes.POI_TYPES.register(modEventBus);
        ModPoiTypes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MysteriousBee.registerPoi();
        });
    }

    private void addCreative(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == ModCreativeModeTabs.FANTASBEE_TAB) {
            event.accept(ModBlocks.SUSPECISOUS_BEEHIVE_BLOCK);
            event.accept(ModBlocks.DEAD_SUSPECISOUS_BEEHIVE_BLOCK);
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods
    // in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
