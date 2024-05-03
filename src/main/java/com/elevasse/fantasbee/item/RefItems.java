package com.elevasse.fantasbee.item;

import com.elevasse.fantasbee.block.IronBeehive;
import com.elevasse.fantasbee.fantasbee;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RefItems {
    // Create a Deferred Register to hold Items which will all be registered under the "fantasbee" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, fantasbee.MODID);

    public static final RegistryObject<Item> COPPER_UPGRADE = registerItem("copper_upgrade", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IRON_UPGRADE = registerItem("iron_upgrade", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GOLD_UPGRADE = registerItem("gold_upgrade", () -> new Item(new Item.Properties()));

    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item){
        RegistryObject<T> toReturn = ITEMS.register(name, item);
        return (toReturn);
    }

    public static void register(IEventBus eventBus) { ITEMS.register(eventBus);}

}
