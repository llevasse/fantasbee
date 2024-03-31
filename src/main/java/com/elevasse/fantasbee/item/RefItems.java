package com.elevasse.fantasbee.item;

import com.elevasse.fantasbee.fantasbee;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RefItems {
    // Create a Deferred Register to hold Items which will all be registered under the "fantasbee" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, fantasbee.MODID);


    public static void register(IEventBus eventBus) { ITEMS.register(eventBus);}

}
