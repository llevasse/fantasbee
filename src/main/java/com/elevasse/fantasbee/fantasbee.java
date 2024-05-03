package com.elevasse.fantasbee;

import com.elevasse.fantasbee.block.RefBlocks;
import com.elevasse.fantasbee.blockEntity.RefBlockEntity;
import com.elevasse.fantasbee.entity.RefEntities;
import com.elevasse.fantasbee.item.RefItems;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(fantasbee.MODID)
public class fantasbee
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "fantasbee";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public fantasbee()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        RefBlocks.register(modEventBus);
        RefItems.register(modEventBus);
        RefEntities.register(modEventBus);
        RefBlockEntity.register(modEventBus);
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so items get registered

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

    private void addCreative(CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == CreativeModeTabs.SEARCH || event.getTab() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(RefBlocks.COMMON_BEEHIVE);
            event.accept(RefBlocks.COPPER_BEEHIVE);
            event.accept(RefBlocks.IRON_BEEHIVE);
            event.accept(RefBlocks.GOLD_BEEHIVE);
            event.accept(RefBlocks.DIAMOND_BEEHIVE);
            event.accept(RefBlocks.IRON_FLOWER);
            event.accept(RefBlocks.COAL_FLOWER);
            event.accept(RefBlocks.COPPER_FLOWER);
            event.accept(RefBlocks.GOLD_FLOWER);
            event.accept(RefBlocks.DIAMOND_FLOWER);
            event.accept(RefItems.COPPER_UPGRADE);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
