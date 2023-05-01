package net.llevasse.fantasbee.item;

import net.llevasse.fantasbee.FantasBee;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FantasBee.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTabs {
	public static CreativeModeTab FANTASBEE_TAB;

	@SubscribeEvent
	public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event) {
		FANTASBEE_TAB = event.registerCreativeModeTab(new ResourceLocation(FantasBee.MOD_ID, "fantasbee_tab"),
		builder -> builder.icon(() -> new ItemStack(Items.HONEYCOMB)).
		title(Component.translatable("creativemodetab.fantasbee_tab")));
	}
}