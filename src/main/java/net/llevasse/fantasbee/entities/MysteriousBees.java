package net.llevasse.fantasbee.entities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;

public class MysteriousBees extends Bee {

	public MysteriousBees(EntityType<? extends Bee> Entity, Level lvl) {
		super(Entity, lvl);
	}
	
	@Override
	public Bee getBreedOffspring(ServerLevel lvl, AgeableMob mob) {
		return super.getBreedOffspring(lvl, mob);
	}
	
	
	//need to copy and Override every methods in bee.class that use the blocktag "BEEHIVES"
	
}
