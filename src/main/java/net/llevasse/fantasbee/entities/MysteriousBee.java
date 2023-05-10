package net.llevasse.fantasbee.entities;

import net.llevasse.fantasbee.entities.block_entities.ModBlockEntities;
import net.llevasse.fantasbee.entities.block_entities.MysteriousBeehiveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MysteriousBee extends Bee {

	BlockPos hivePos;

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

	boolean isHiveValid() {
		if (!this.hasHive()) {
			return false;
		} else if (this.isTooFarAway(this.hivePos)) {
			return false;
		} else {
			ModBlockEntities blockentity = this.level.getBlockEntity(this.hivePos);
			return blockentity instanceof MysteriousBeehiveBlockEntity;
		}
	}

	boolean isTooFarAway(BlockPos pos) {
		return !this.closerThan(pos, 32);
	}

	boolean closerThan(BlockPos pos, int p_27818_) {
		return pos.closerThan(this.blockPosition(), (double) p_27818_);
	}

	// need to copy and Override every methods in bee.class that use the blocktag
	// "BEEHIVES"
}
