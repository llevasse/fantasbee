package net.llevasse.fantasbee.entities.model;

import net.llevasse.fantasbee.entities.MysteriousBees;
import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.geom.ModelPart;

public class MysteriousBeeModel<T extends MysteriousBees> extends BeeModel<T> {

	public MysteriousBeeModel(ModelPart ModelP) {
		super(ModelP);
	}
}
