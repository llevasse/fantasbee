package net.llevasse.fantasbee.item;

import net.llevasse.fantasbee.FantasBee;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FantasBee.MOD_ID);

	public static final RegistryObject<Item> SUSPECIOUS_BEEHIVE = ITEMS.register("suspecious_beehive",
			() -> new Item(new Item.Properties()));
			
	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus);
	}
}

