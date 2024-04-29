package com.elevasse.fantasbee.blockEntity;

import com.elevasse.fantasbee.block.CommonBeehive;
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

    public static final RegistryObject<BlockEntityType<CommonBeehiveEntity>> COMMON_BEEHIVE = BLOCKS_ENTITIES.register("common_beehive",
            () -> BlockEntityType.Builder.of(CommonBeehiveEntity::new, RefBlocks.COMMON_BEEHIVE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CommonBeehiveEntity>> COPPER_BEEHIVE = BLOCKS_ENTITIES.register("copper_beehive",
            () -> BlockEntityType.Builder.of(CommonBeehiveEntity::new, RefBlocks.COPPER_BEEHIVE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CommonBeehiveEntity>> IRON_BEEHIVE = BLOCKS_ENTITIES.register("iron_beehive",
            () -> BlockEntityType.Builder.of(CommonBeehiveEntity::new, RefBlocks.IRON_BEEHIVE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CommonBeehiveEntity>> GOLD_BEEHIVE = BLOCKS_ENTITIES.register("gold_beehive",
            () -> BlockEntityType.Builder.of(CommonBeehiveEntity::new, RefBlocks.GOLD_BEEHIVE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CommonBeehiveEntity>> DIAMOND_BEEHIVE = BLOCKS_ENTITIES.register("diamond_beehive",
            () -> BlockEntityType.Builder.of(CommonBeehiveEntity::new, RefBlocks.DIAMOND_BEEHIVE.get()).build(null));

    public static void register(IEventBus eventBus) { BLOCKS_ENTITIES.register(eventBus);}
}
