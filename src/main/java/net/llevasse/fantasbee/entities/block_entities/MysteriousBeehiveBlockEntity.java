package net.llevasse.fantasbee.entities.block_entities;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MysteriousBeehiveBlockEntity extends BeehiveBlockEntity {
	protected NonNullList<ItemStack> product = NonNullList.withSize(1, ItemStack.EMPTY);
	private final List<MysteriousBeehiveBlockEntity> stored = Lists.newArrayList();

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
	
	public static boolean CanHiveAcceptBee(Level lvl, BlockPos pos) {
		BlockEntity entity = lvl.getBlockEntity(pos);
		if (entity instanceof MysteriousBeehiveBlockEntity beehiveBlockEntity) {
			if (beehiveBlockEntity.stored.isEmpty() || beehiveBlockEntity.stored.size() < 3)
				return true;
		}
		return false;
	}
}
