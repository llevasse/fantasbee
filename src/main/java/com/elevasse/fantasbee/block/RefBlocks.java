package com.elevasse.fantasbee.block;

import com.elevasse.fantasbee.fantasbee;
import com.elevasse.fantasbee.item.RefItems;
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


    public static final RegistryObject<Block> COMMON_BEEHIVE = registerBlock("common_beehive", () -> new CommonBeehive(BlockBehaviour.Properties.of(Material.WOOD).strength(6f)));
    public static final RegistryObject<Block> COPPER_BEEHIVE = registerBlock("copper_beehive", () -> new CopperBeehive(BlockBehaviour.Properties.of(Material.WOOD).strength(6f)));
    public static final RegistryObject<Block> IRON_BEEHIVE = registerBlock("iron_beehive", () -> new IronBeehive(BlockBehaviour.Properties.of(Material.WOOD).strength(6f)));
    public static final RegistryObject<Block> GOLD_BEEHIVE = registerBlock("gold_beehive", () -> new GoldBeehive(BlockBehaviour.Properties.of(Material.WOOD).strength(6f)));

    public static final RegistryObject<Block> IRON_FLOWER = registerBlock("iron_flower", () -> new FlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION)));
    public static final RegistryObject<Block> COAL_FLOWER = registerBlock("coal_flower", () -> new FlowerBlock(MobEffects.POISON, 10, BlockBehaviour.Properties.copy(Blocks.DANDELION)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn);
        return (toReturn);
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, Supplier<T> block) {
       return RefItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) { BLOCKS.register(eventBus);}
}
