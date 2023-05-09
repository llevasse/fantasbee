package net.llevasse.fantasbee.entities.model;

import net.llevasse.fantasbee.FantasBee;
import net.llevasse.fantasbee.entities.MysteriousBee;
import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;

public class MysteriousBeeModel extends BeeModel<MysteriousBee> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(FantasBee.MOD_ID, "mysterious_bees"), "main");

	public MysteriousBeeModel(ModelPart ModelP) {
		super(ModelP);
	}

	public static ModelLayerLocation getLayerLocation() {
		return LAYER_LOCATION;
	}
}
