package com.elevasse.fantasbee.blockEntity;

import com.elevasse.fantasbee.block.MysteriousBeehive;
import com.elevasse.fantasbee.block.RefBlocks;
import com.elevasse.fantasbee.fantasbee;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RefBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCKS_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, fantasbee.MODID);

    public static final RegistryObject<BlockEntityType<MysteriousBeehiveEntity>> MYSTERIOUS_BEEHIVE = BLOCKS_ENTITIES.register("mysterious_beehive",
            () -> BlockEntityType.Builder.of(MysteriousBeehiveEntity::new, RefBlocks.MYSTERIOUS_BEEHIVE.get()).build(null));
    public static void register(IEventBus eventBus) { BLOCKS_ENTITIES.register(eventBus);}
}
