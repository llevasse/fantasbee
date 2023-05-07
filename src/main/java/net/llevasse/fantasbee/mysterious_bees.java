package net.llevasse.fantasbee;

import net.llevasse.fantasbee.entities.MysteriousBees;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class mysterious_bees {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
			FantasBee.MOD_ID);

	public static final RegistryObject<EntityType<MysteriousBees>> MYSTERIOUS_BEES = ENTITIES
			.register("mysterious_bees", () -> EntityType.Builder.of(MysteriousBees::new, MobCategory.CREATURE)
					.sized(0.7F, 0.6F).clientTrackingRange(8).build(FantasBee.MOD_ID + ":mysterious_bees"));
}
