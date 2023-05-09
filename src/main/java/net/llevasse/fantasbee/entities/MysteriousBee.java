package net.llevasse.fantasbee.entities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;

public class MysteriousBee extends Bee {

	public MysteriousBee(EntityType<? extends Bee> Entity, Level lvl) {
		super(Entity, lvl);
	}
	
	@Override
	public Bee getBreedOffspring(ServerLevel lvl, AgeableMob mob) {
		return ModEntities.MYSTERIOUS_BEE.get().create(lvl);
	}

	public static AttributeSupplier.Builder getMysteriousBeeAttributes() {
		return Bee.createAttributes();
	}
	
	
	//need to copy and Override every methods in bee.class that use the blocktag "BEEHIVES"
	
}
