package net.llevasse.fantasbee.entities;

import net.llevasse.fantasbee.FantasBee;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
			FantasBee.MOD_ID);

	public static final RegistryObject<EntityType<MysteriousBee>> MYSTERIOUS_BEE = ENTITIES
			.register("mysterious_bee", () -> EntityType.Builder.of(MysteriousBee::new, MobCategory.CREATURE)
					.sized(0.7F, 0.6F).clientTrackingRange(8).build(FantasBee.MOD_ID + ":mysterious_bee"));

	public static void register(IEventBus modEventBus) {
		ENTITIES.register(modEventBus);
	}

}
