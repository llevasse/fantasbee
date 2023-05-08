package net.llevasse.fantasbee.client;

import net.llevasse.fantasbee.mysterious_bees;
import net.llevasse.fantasbee.renderer.MysteriousBeeRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
	@SubscribeEvent
	public static void doSetup(FMLClientSetupEvent event) {
		EntityRenderers.register(mysterious_bees.MYSTERIOUS_BEES.get(), MysteriousBeeRenderer::new);
	}
}
