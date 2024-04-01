package com.elevasse.fantasbee.events;

import com.elevasse.fantasbee.entity.CommonBee;
import com.elevasse.fantasbee.entity.RefEntities;
import com.elevasse.fantasbee.fantasbee;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = fantasbee.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CommonModEvents {
    @SubscribeEvent
    public static void entityAttributes(EntityAttributeCreationEvent event){
        event.put(RefEntities.COMMON_BEE.get(), CommonBee.createAttributes().build());
    }

    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event){
        event.register(RefEntities.COMMON_BEE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, CommonBee::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
    }
}
