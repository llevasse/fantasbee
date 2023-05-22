package net.llevasse.fantasbee.entities.block_entities;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.llevasse.fantasbee.entities.MysteriousBee;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class MysteriousBeehiveBlockEntity extends BlockEntity {
	protected ItemStack product = ItemStack.EMPTY;
	private final List<MysteriousBeehiveBlockEntity.BeeData> stored = Lists.newArrayList();
	public static final String TAG_FLOWER_POS = "FlowerPos";
	public static final String TAG_PRODUCT = "Product";
	public static final String MIN_OCCUPATION_TICKS = "MinOccupationTicks";
	public static final String ENTITY_DATA = "EntityData";
	public static final String TICKS_IN_HIVE = "TicksInHive";
	public static final String HAS_NECTAR = "HasNectar";
	public static final String BEES = "Bees";
	private static final List<String> IGNORED_BEE_TAGS = Arrays.asList("Air", "ArmorDropChances", "ArmorItems", "Brain",
			"CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems",
			"HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos",
			"Rotation", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "HivePos",
			"Passengers", "Leash", "UUID");
	public static final int MAX_OCCUPANTS = 3;
	public static final int MIN_OCCUPATION_TICKS_NECTARLESS = 20;
	public int ItemProduced;
	@Nullable
	private BlockPos savedFlowerPos;

	public MysteriousBeehiveBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.BEEHIVE, pos, state);
		product = new ItemStack(Items.AIR);
		ItemProduced = 0;
	}

	
	public void setProduct(MysteriousBee bee) {
		this.product = new ItemStack(bee.getProduct());
		System.out.printf("\n\nfantasbee : new product : %s\n\n",
		this.product.getItem().getName(this.product));
	}
	
	public ItemLike getProduct() {
		return this.product.getItem();
	}

	public static boolean CanHiveAcceptBee(Level lvl, BlockPos pos, Item BeeProduct) {
		BlockEntity entity = lvl.getBlockEntity(pos);
		if (entity instanceof MysteriousBeehiveBlockEntity beehiveBlockEntity) {
			if (!beehiveBlockEntity.product.getItem().equals(Items.AIR))
				return false;
			if (beehiveBlockEntity.product.getItem().equals(Items.AIR))
				return true;
			if (beehiveBlockEntity.stored.isEmpty() || beehiveBlockEntity.stored.size() < 3)
				return true;
		}
		return false;
	}

	public ListTag writeBees() {
		ListTag listtag = new ListTag();

		for (MysteriousBeehiveBlockEntity.BeeData beehiveblockentity$beedata : this.stored) {
			CompoundTag compoundtag = beehiveblockentity$beedata.entityData.copy();
			compoundtag.remove("UUID");
			CompoundTag compoundtag1 = new CompoundTag();
			compoundtag1.put("EntityData", compoundtag);
			compoundtag1.putInt("TicksInHive", beehiveblockentity$beedata.ticksInHive);
			compoundtag1.putInt("MinOccupationTicks", beehiveblockentity$beedata.minOccupationTicks);
			listtag.add(compoundtag1);
		}

		return listtag;
	}

	static class BeeData {
		final CompoundTag entityData;
		int ticksInHive;
		final int minOccupationTicks;
		public int MaxOccupationTicks;

		BeeData(CompoundTag tag, int ticksInHive, int MinOccupationTick, int MaxOccupationTicks) {
			MysteriousBeehiveBlockEntity.removeIgnoredBeeTags(tag);
			this.entityData = tag;
			this.ticksInHive = ticksInHive;
			this.minOccupationTicks = MinOccupationTick;
			this.MaxOccupationTicks = MaxOccupationTicks;
		}
	}

	public void setChanged() {
		if (this.isFireNearby()) {
			this.emptyAllLivingFromHive((Player) null, this.level.getBlockState(this.getBlockPos()),
					MysteriousBeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
		}

		super.setChanged();
	}

	public boolean isFireNearby() {
		if (this.level == null) {
			return false;
		} else {
			for (BlockPos blockpos : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1),
					this.worldPosition.offset(1, 1, 1))) {
				if (this.level.getBlockState(blockpos).getBlock() instanceof FireBlock) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean isEmpty() {
		return this.stored.isEmpty();
	}

	public boolean isFull() {
		return this.stored.size() == 3;
	}

	public void emptyAllLivingFromHive(@Nullable Player player, BlockState state,
			MysteriousBeehiveBlockEntity.BeeReleaseStatus ReleaseStatus) {
		List<Entity> list = this.releaseAllOccupants(state, ReleaseStatus);
		if (player != null) {
			for (Entity entity : list) {
				if (entity instanceof MysteriousBee) {
					MysteriousBee bee = (MysteriousBee) entity;
					if (player.position().distanceToSqr(entity.position()) <= 16.0D) {
						if (!this.isSedated()) {
							bee.setTarget(player);
						} else {
							bee.setStayOutOfHiveCountdown(400);
						}
					}
				}
			}
		}

	}

	
	public void addOccupant(Entity entity, boolean hasNectar) {
		// if (this.product.get(0).getItem().equals(Items.AIR))
		// 	this.setProduct((MysteriousBee) entity);
		if (this.product.getItem().equals(Items.AIR))
			this.setProduct((MysteriousBee) entity);
		this.addOccupantWithPresetTicks(entity, hasNectar, 0);
	}

	@VisibleForDebug
	public int getOccupantCount() {
		return this.stored.size();
	}
	
	public static int getHoneyLevel(BlockState p_58753_) {
		return p_58753_.getValue(BeehiveBlock.HONEY_LEVEL);
	}

	@VisibleForDebug
	public boolean isSedated() {
		return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
	}

	public void addOccupantWithPresetTicks(Entity entity, boolean hasNectar, int ticksInHive) {
		if (this.stored.size() < 3) {
			entity.stopRiding();
			entity.ejectPassengers();
			CompoundTag compoundtag = new CompoundTag();
			entity.save(compoundtag);
			this.storeBee(compoundtag, ticksInHive, hasNectar, hasNectar);
			if (this.level != null) {
				if (entity instanceof MysteriousBee) {
					MysteriousBee bee = (MysteriousBee) entity;
					if (bee.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
						this.savedFlowerPos = bee.getSavedFlowerPos();
					}
				}

				BlockPos blockpos = this.getBlockPos();
				this.level.playSound((Player) null, (double) blockpos.getX(), (double) blockpos.getY(),
				(double) blockpos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
				this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos,
						GameEvent.Context.of(entity, this.getBlockState()));
			}

			entity.discard();
			super.setChanged();
		}
	}
	
	public void storeBee(CompoundTag tag, int ticksInHive, boolean MinOccupationTick, boolean MaxOccupationTick) {
		this.stored.add(new MysteriousBeehiveBlockEntity.BeeData(tag, ticksInHive, MinOccupationTick ? 40 : 20,
				MaxOccupationTick ? 60 : 40));
	}
	
	private List<Entity> releaseAllOccupants(BlockState state, MysteriousBeehiveBlockEntity.BeeReleaseStatus status) {
		List<Entity> list = Lists.newArrayList();
		this.stored.removeIf((beedata) -> {
			return releaseOccupant(this.level, this.worldPosition, state, beedata, list, status,
					this.savedFlowerPos);
		});
		if (!list.isEmpty()) {
			super.setChanged();
		}

		return list;
	}
	
	private static boolean releaseOccupant(Level lvl, BlockPos pos, BlockState state,
			MysteriousBeehiveBlockEntity.BeeData ThisBeeData, @Nullable List<Entity> BeeList,
			MysteriousBeehiveBlockEntity.BeeReleaseStatus status, @Nullable BlockPos flowerPos) {
		if ((lvl.isNight() || lvl.isRaining())
				&& status != MysteriousBeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
					return false;
		} else {
			CompoundTag compoundtag = ThisBeeData.entityData.copy();
			removeIgnoredBeeTags(compoundtag);
			compoundtag.put("HivePos", NbtUtils.writeBlockPos(pos));
			compoundtag.putBoolean("NoGravity", true);
			Direction direction = state.getValue(BeehiveBlock.FACING);
			BlockPos blockpos = pos.relative(direction);
			boolean flag = !lvl.getBlockState(blockpos).getCollisionShape(lvl, blockpos).isEmpty();
			if (flag && status != MysteriousBeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
				return false;
			} else {
				Entity entity = EntityType.loadEntityRecursive(compoundtag, lvl, (p_58740_) -> {
					return p_58740_;
				});
				if (entity != null) {
					if (!entity.getType().is(EntityTypeTags.BEEHIVE_INHABITORS)) {
						return false;
					} else {
						if (entity instanceof MysteriousBee) {
							MysteriousBee bee = (MysteriousBee) entity;
							if (flowerPos != null && !bee.hasSavedFlowerPos() && lvl.random.nextFloat() < 0.9F) {
								bee.setSavedFlowerPos(flowerPos);
							}

							if (status == MysteriousBeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {
								bee.dropOffNectar();
								if (state.is(BlockTags.BEEHIVES, (p_202037_) -> {
									return p_202037_.hasProperty(BeehiveBlock.HONEY_LEVEL);
								})) {
									int i = getHoneyLevel(state);
									if (i < 5) {
										int j = lvl.random.nextInt(100) == 0 ? 2 : 1;
										if (i + j > 5) {
											--j;
										}

										lvl.setBlockAndUpdate(pos,
												state.setValue(BeehiveBlock.HONEY_LEVEL, Integer.valueOf(i + j)));
									}
								}
							}

							setBeeReleaseData(ThisBeeData.ticksInHive, bee);
							if (BeeList != null) {
								BeeList.add(bee);
							}

							float f = entity.getBbWidth();
							double d3 = flag ? 0.0D : 0.55D + (double) (f / 2.0F);
							double d0 = (double) pos.getX() + 0.5D + d3 * (double) direction.getStepX();
							double d1 = (double) pos.getY() + 0.5D - (double) (entity.getBbHeight() / 2.0F);
							double d2 = (double) pos.getZ() + 0.5D + d3 * (double) direction.getStepZ();
							entity.moveTo(d0, d1, d2, entity.getYRot(), entity.getXRot());
						}

						lvl.playSound((Player) null, pos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS,
								1.0F, 1.0F);
						lvl.gameEvent(GameEvent.BLOCK_CHANGE, pos,
								GameEvent.Context.of(entity, lvl.getBlockState(pos)));
						return lvl.addFreshEntity(entity);
					}
				} else {
					return false;
				}
			}
		}
	}

	static void removeIgnoredBeeTags(CompoundTag p_155162_) {
		for (String s : IGNORED_BEE_TAGS) {
			p_155162_.remove(s);
		}

	}

	private static void setBeeReleaseData(int ticksInHive, MysteriousBee bee) {
		int i = bee.getAge();
		if (i < 0) {
			bee.setAge(Math.min(0, i + ticksInHive));
		} else if (i > 0) {
			bee.setAge(Math.max(0, i - ticksInHive));
		}

		bee.setInLoveTime(Math.max(0, bee.getInLoveTime() - ticksInHive));
	}

	private boolean hasSavedFlowerPos() {
		return this.savedFlowerPos != null;
	}

	private static void tickOccupants(Level lvl, BlockPos pos, BlockState state,
			List<MysteriousBeehiveBlockEntity.BeeData> BeeList, @Nullable BlockPos flowerPos) {
		boolean flag = false;

		if (!BeeList.isEmpty()) {
			int ticksInBeeOne = BeeList.get(0).ticksInHive;
			int maxTickBeeOne = BeeList.get(0).MaxOccupationTicks;
			System.out.printf("\n\nfantasbee : bee 1 ticks in\n\n", ticksInBeeOne);
			System.out.printf("\n\nfantasbee : bee 1 ticks before MaxTick\n\n", maxTickBeeOne - ticksInBeeOne);
		}
		MysteriousBeehiveBlockEntity.BeeData MysteriousBeehiveBlockEntity$beedata;
		for (Iterator<MysteriousBeehiveBlockEntity.BeeData> iterator = BeeList.iterator(); iterator
				.hasNext(); ++MysteriousBeehiveBlockEntity$beedata.ticksInHive) {
			MysteriousBeehiveBlockEntity$beedata = iterator.next();
			if (MysteriousBeehiveBlockEntity$beedata.ticksInHive > MysteriousBeehiveBlockEntity$beedata.minOccupationTicks
					|| MysteriousBeehiveBlockEntity$beedata.ticksInHive > MysteriousBeehiveBlockEntity$beedata.MaxOccupationTicks) {
				System.out.print("\n\nfantasbee : I want to leave this hive\n\n");
				MysteriousBeehiveBlockEntity.BeeReleaseStatus MysteriousBeehiveBlockEntity$beereleasestatus = MysteriousBeehiveBlockEntity$beedata.entityData
						.getBoolean("HasNectar") ? MysteriousBeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED
								: MysteriousBeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
				if (releaseOccupant(lvl, pos, state, MysteriousBeehiveBlockEntity$beedata,
						(List<Entity>) null,
						MysteriousBeehiveBlockEntity$beereleasestatus, flowerPos)) {
					flag = true;
					iterator.remove();
				}
			}
		}

		if (flag) {
			setChanged(lvl, pos, state);
		}

	}

	public static void serverTick(Level lvl, BlockPos pos, BlockState state,
			MysteriousBeehiveBlockEntity BlockEntity) {
		tickOccupants(lvl, pos, state, BlockEntity.stored, BlockEntity.savedFlowerPos);
		if (!BlockEntity.stored.isEmpty() && lvl.getRandom().nextDouble() < 0.005D) {
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = (double) pos.getY();
			double d2 = (double) pos.getZ() + 0.5D;
			lvl.playSound((Player) null, d0, d1, d2, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
		}

		//DebugPackets.sendHiveInfo(lvl, pos, state, BlockEntity);
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("Bees", this.writeBees());
		nbt.putInt("NbProduced", ItemProduced);
		if (this.hasSavedFlowerPos()) {
			nbt.put("FlowerPos", NbtUtils.writeBlockPos(this.savedFlowerPos));
		}
		if (!this.product.getItem().equals(Items.AIR)) {
			System.out.print("\n\nfantasbee : saving beehive\n\n");
			nbt.put("Product", this.product.save(new CompoundTag()));
		}
		System.out.print("\n\nfantasbee.beehive : done saving beehive\n\n");
	}

	@Override
	public void load(CompoundTag nbt) {
		System.out.print("\n\nfantasbee : Loading beehive\n\n");
		
		this.product = ItemStack.EMPTY;
		this.savedFlowerPos = null;
		this.ItemProduced = nbt.getInt("NbProduced");
		this.stored.clear();
		ListTag listtag = nbt.getList("Bees", 10);

		for (int i = 0; i < listtag.size(); ++i) {
			CompoundTag compoundtag = listtag.getCompound(i);
			MysteriousBeehiveBlockEntity.BeeData beedata = new MysteriousBeehiveBlockEntity.BeeData(
				compoundtag.getCompound("EntityData"), compoundtag.getInt("TicksInHive"),
				compoundtag.getInt("MinOccupationTicks"), compoundtag.getInt("MaxOccupationTicks"));
			this.stored.add(beedata);
		}
		if (nbt.contains("FlowerPos")) {
			this.savedFlowerPos = NbtUtils.readBlockPos(nbt.getCompound("FlowerPos"));
		}
		if (nbt.contains("Product")) {
			CompoundTag tag = nbt.getCompound("Product");
			ItemStack item = ItemStack.of(tag);
			this.product = item;
		}
		super.load(nbt);
	}

	public enum BeeReleaseStatus {
		HONEY_DELIVERED,
		BEE_RELEASED,
		EMERGENCY;
	}

}
