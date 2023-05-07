package net.llevasse.fantasbee;

import com.mojang.logging.LogUtils;

import net.llevasse.fantasbee.block.ModBlocks;
import net.llevasse.fantasbee.entities.block_entities.BlockEntities;
import net.llevasse.fantasbee.item.ModCreativeModeTabs;
import net.llevasse.fantasbee.item.ModItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FantasBee.MOD_ID)
public class FantasBee
{
    public static final String MOD_ID = "fantasbee";
    private static final Logger LOGGER = LogUtils.getLogger();
    public FantasBee()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        BlockEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
     }

    private void addCreative(CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == ModCreativeModeTabs.FANTASBEE_TAB){
            event.accept(ModBlocks.SUSPECISOUS_BEEHIVE_BLOCK);
            event.accept(ModBlocks.DEAD_SUSPECISOUS_BEEHIVE_BLOCK);
        }
     }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }
}
