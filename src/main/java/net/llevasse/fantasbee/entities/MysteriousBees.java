package net.llevasse.fantasbee.entities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;

public class MysteriousBees extends Bee {

	public MysteriousBees(EntityType<? extends Bee> p_27717_, Level p_27718_) {
		super(p_27717_, p_27718_);
		//TODO Auto-generated constructor stub
	}
	
	@Override
	public Bee getBreedOffspring(ServerLevel p_148760_, AgeableMob p_148761_) {
		// TODO Auto-generated method stub
		return super.getBreedOffspring(p_148760_, p_148761_);
	}
	
	
	//need to copy and Override every methods in bee.class that use the blocktag "BEEHIVES"
	
}
