package com.elevasse.fantasbee.entity;

import com.elevasse.fantasbee.fantasbee;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RefEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, fantasbee.MODID);

    public static void register(IEventBus eventBus) { ENTITIES.register(eventBus);}
    public static final RegistryObject<EntityType<CommonBee>> COMMON_BEE = ENTITIES.register("common_bee", () -> EntityType.Builder.<CommonBee>of(CommonBee::new, MobCategory.CREATURE)
            .sized(0.5f,0.5f)
            .build(new ResourceLocation(fantasbee.MODID, "common_bee").toString()));

}
