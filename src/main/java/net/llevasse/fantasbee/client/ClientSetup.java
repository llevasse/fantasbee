package net.llevasse.fantasbee.client;

import net.llevasse.fantasbee.FantasBee;
import net.llevasse.fantasbee.entities.ModEntities;
import net.llevasse.fantasbee.entities.model.MysteriousBeeModel;
import net.llevasse.fantasbee.renderer.MysteriousBeeRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = FantasBee.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
	@SubscribeEvent
	public static void EntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		EntityRenderers.register(ModEntities.MYSTERIOUS_BEES.get(), MysteriousBeeRenderer::new);
	}
	
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event){
		event.registerLayerDefinition(MysteriousBeeModel.LAYER_LOCATION, MysteriousBeeModel::createBodyLayer);
	}
}
