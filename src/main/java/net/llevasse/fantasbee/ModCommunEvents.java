package net.llevasse.fantasbee;

import net.llevasse.fantasbee.entities.ModEntities;
import net.llevasse.fantasbee.entities.MysteriousBee;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FantasBee.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCommunEvents {
	
	@SubscribeEvent
	public static void entityAttributes(EntityAttributeCreationEvent event) {
		event.put(ModEntities.MYSTERIOUS_BEE.get(), MysteriousBee.getMysteriousBeeAttributes().build());
	}
}
