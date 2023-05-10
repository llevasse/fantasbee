package net.llevasse.fantasbee.renderer;

import net.llevasse.fantasbee.FantasBee;
import net.llevasse.fantasbee.entities.MysteriousBee;
import net.llevasse.fantasbee.entities.model.MysteriousBeeModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

@OnlyIn(Dist.CLIENT)
public class MysteriousBeeRenderer extends MobRenderer<MysteriousBee, MysteriousBeeModel<MysteriousBee>> {
	public MysteriousBeeRenderer(Context ctx) {
		super(ctx, new MysteriousBeeModel(ctx.bakeLayer(MysteriousBeeModel.LAYER_LOCATION)), 0.4f);
	}

	private static final ResourceLocation ANGRY_BEE_TEXTURE = new ResourceLocation(FantasBee.MOD_ID,
			"textures/entity/bee/bee_angry.png");
	private static final ResourceLocation ANGRY_NECTAR_BEE_TEXTURE = new ResourceLocation(
			FantasBee.MOD_ID, "textures/entity/bee/bee_angry_nectar.png");
	private static final ResourceLocation BEE_TEXTURE = new ResourceLocation(FantasBee.MOD_ID,
			"textures/entity/bee/bee.png");
	private static final ResourceLocation NECTAR_BEE_TEXTURE = new ResourceLocation(FantasBee.MOD_ID,"textures/entity/bee/bee_nectar.png");

	@Override
	public ResourceLocation getTextureLocation(MysteriousBee bee) {
		if (bee.isAngry()) {
			return bee.hasNectar() ? ANGRY_NECTAR_BEE_TEXTURE : ANGRY_BEE_TEXTURE;
		} else {
			return bee.hasNectar() ? NECTAR_BEE_TEXTURE : BEE_TEXTURE;
		}
	}
}
