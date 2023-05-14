package net.llevasse.fantasbee.entities.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MysteriousBeehiveBlockEntity extends BeehiveBlockEntity {

	protected NonNullList<ItemStack> product = NonNullList.withSize(1, ItemStack.EMPTY);

	public MysteriousBeehiveBlockEntity(BlockPos pos, BlockState state) {
  		super(pos, state);
		product.set(0, new ItemStack(Items.DIAMOND));
	}
	
	public void load(CompoundTag nbt){
		super.load(nbt);
		this.product = NonNullList.withSize(1, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, product);
	}
	
	public ItemLike getProduct(){
		return this.product.get(0).getItem();
	}
	
	protected void saveAdditional(CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		ContainerHelper.saveAllItems(nbt, product);
	}
}
