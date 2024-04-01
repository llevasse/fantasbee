package com.elevasse.fantasbee.client.renderer;

import com.elevasse.fantasbee.fantasbee;
import com.elevasse.fantasbee.entity.CommonBee;
import com.elevasse.fantasbee.client.model.CommonBeeModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class CommonBeeRenderer extends MobRenderer<CommonBee, CommonBeeModel<CommonBee>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(fantasbee.MODID, "textures/entity/common_bee.png");
    public CommonBeeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CommonBeeModel<>(ctx.bakeLayer(ModelLayers.BEE)), 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(CommonBee commonBee) {
        return TEXTURE;
    }
}
