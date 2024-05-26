package com.elevasse.fantasbee.block;

import com.elevasse.fantasbee.fantasbee;
import com.elevasse.fantasbee.item.RefItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RefBlocks {
    // Create a Deferred Register to hold Blocks which will all be registered under the "fantasbee" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, fantasbee.MODID);
    public static final DeferredRegister<Block> VANILLA_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");


    public static final RegistryObject<Block> COMMON_BEEHIVE = registerVanillaBlock("beehive", () -> new CommonBeehive(BlockBehaviour.Properties.of(Material.WOOD).strength(6f)));
    public static final RegistryObject<Block> COPPER_BEEHIVE = registerBlock("copper_beehive", () -> new CopperBeehive(BlockBehaviour.Properties.of(Material.WOOD).strength(6f)));
    public static final RegistryObject<Block> IRON_BEEHIVE = registerBlock("iron_beehive", () -> new IronBeehive(BlockBehaviour.Properties.of(Material.WOOD).strength(6f)));
    public static final RegistryObject<Block> GOLD_BEEHIVE = registerBlock("gold_beehive", () -> new GoldBeehive(BlockBehaviour.Properties.of(Material.WOOD).strength(6f)));
    public static final RegistryObject<Block> DIAMOND_BEEHIVE = registerBlock("diamond_beehive", () -> new DiamondBeehive(BlockBehaviour.Properties.of(Material.WOOD).strength(6f)));

    //ores
    public static final RegistryObject<Block> IRON_FLOWER = registerBlock("iron_flower", () -> new OreFlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION), BlockTags.IRON_ORES));
    public static final RegistryObject<Block> COAL_FLOWER = registerBlock("coal_flower", () -> new OreFlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION), BlockTags.COAL_ORES));
    public static final RegistryObject<Block> COPPER_FLOWER = registerBlock("copper_flower", () -> new OreFlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION), BlockTags.COPPER_ORES));
    public static final RegistryObject<Block> GOLD_FLOWER = registerBlock("gold_flower", () -> new OreFlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION), BlockTags.GOLD_ORES));
    public static final RegistryObject<Block> DIAMOND_FLOWER = registerBlock("diamond_flower", () -> new OreFlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION), BlockTags.DIAMOND_ORES));

    //woods
    public static final RegistryObject<Block> OAK_FLOWER = registerBlock("oak_flower", () -> new OreFlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION), BlockTags.OAK_LOGS));
    public static final RegistryObject<Block> SPRUCE_FLOWER = registerBlock("spruce_flower", () -> new OreFlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION), BlockTags.SPRUCE_LOGS));
    public static final RegistryObject<Block> BIRCH_FLOWER = registerBlock("birch_flower", () -> new OreFlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION), BlockTags.BIRCH_LOGS));
    public static final RegistryObject<Block> JUNGLE_FLOWER = registerBlock("jungle_flower", () -> new OreFlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION), BlockTags.JUNGLE_LOGS));
    public static final RegistryObject<Block> DARK_OAK_FLOWER = registerBlock("dark_oak_flower", () -> new OreFlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION), BlockTags.DARK_OAK_LOGS));
    public static final RegistryObject<Block> ACACIA_FLOWER = registerBlock("acacia_flower", () -> new OreFlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION), BlockTags.ACACIA_LOGS));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn);
        return (toReturn);
    }

    private static <T extends Block> RegistryObject<T> registerVanillaBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = VANILLA_BLOCKS.register(name,block);
        //registerBlockItem(name, toReturn);
        return (toReturn);
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, Supplier<T> block) {
       return RefItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) { BLOCKS.register(eventBus); VANILLA_BLOCKS.register(eventBus);}
}
