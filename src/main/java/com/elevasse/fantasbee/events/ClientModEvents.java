package com.elevasse.fantasbee.events;

import com.elevasse.fantasbee.client.model.CommonBeeModel;
import com.elevasse.fantasbee.client.renderer.CommonBeeRenderer;
import com.elevasse.fantasbee.entity.RefEntities;
import com.elevasse.fantasbee.fantasbee;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = fantasbee.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(RefEntities.COMMON_BEE.get(), CommonBeeRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event){
        //event.registerLayerDefinition(ModelLayers.BEE, CommonBeeModel.createBodyLayer());
    }
}
