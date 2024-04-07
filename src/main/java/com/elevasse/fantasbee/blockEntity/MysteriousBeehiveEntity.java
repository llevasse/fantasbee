package com.elevasse.fantasbee.blockEntity;

import com.elevasse.fantasbee.entity.CommonBee;
import com.elevasse.fantasbee.block.MysteriousBeehive;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MysteriousBeehiveEntity extends BlockEntity {
    private ItemStack currentProduction;

    public static final String TAG_FLOWER_POS = "FlowerPos";
    public static final String MIN_OCCUPATION_TICKS = "MinOccupationTicks";
    public static final String ENTITY_DATA = "EntityData";
    public static final String TICKS_IN_HIVE = "TicksInHive";
    public static final String HAS_NECTAR = "HasNectar";
    public static final String BEES = "Bees";
    private static final List<String> IGNORED_BEE_TAGS = Arrays.asList("Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "HivePos", "Passengers", "Leash", "UUID");
    public static final int MAX_OCCUPANTS = 3;
    private static final int MIN_TICKS_BEFORE_REENTERING_HIVE = 400;
    private static final int MIN_OCCUPATION_TICKS_NECTAR = 2400;
    public static final int MIN_OCCUPATION_TICKS_NECTARLESS = 600;
    private final List<BeeData> stored = Lists.newArrayList();
    @Nullable
    private BlockPos savedFlowerPos;

    public MysteriousBeehiveEntity(BlockPos pos, BlockState state) {
        super(RefBlockEntity.MYSTERIOUS_BEEHIVE.get(), pos, state);
    }

    public void setCurrentProduction( ItemStack item ){
        currentProduction = item;
    }

    public ItemStack getCurrentProduction( ){
        return currentProduction;
    }

   public void setChanged() {
      if (this.isFireNearby() && this.level != null) {
              this.emptyAllLivingFromHive((Player)null, this.level.getBlockState(this.getBlockPos()), BeeReleaseStatus.EMERGENCY);
      }
      super.setChanged();
   }

   public boolean isFireNearby() {
       if (this.level != null) {
           for (BlockPos blockpos : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
               if (this.level.getBlockState(blockpos).getBlock() instanceof FireBlock) {
                   return true;
               }
           }

       }
       return false;
   }

   public boolean isEmpty() {
      return this.stored.isEmpty();
   }

   public boolean isFull() {
      return this.stored.size() == 3;
   }

   public void emptyAllLivingFromHive(@Nullable Player p_58749_, BlockState p_58750_, MysteriousBeehiveEntity.BeeReleaseStatus p_58751_) {
      List<Entity> list = this.releaseAllOccupants(p_58750_, p_58751_);
      if (p_58749_ != null) {
         for(Entity entity : list) {
            if (entity instanceof CommonBee) {
               CommonBee bee = (CommonBee)entity;
               if (p_58749_.position().distanceToSqr(entity.position()) <= 16.0D) {
                  if (!this.isSedated()) {
                     bee.setTarget(p_58749_);
                  } else {
                     bee.setStayOutOfHiveCountdown(400);
                  }
               }
            }
         }
      }

   }

   private List<Entity> releaseAllOccupants(BlockState p_58760_, MysteriousBeehiveEntity.BeeReleaseStatus p_58761_) {
      List<Entity> list = Lists.newArrayList();
      this.stored.removeIf((p_272556_) -> {
          if (this.level != null) {
              return releaseOccupant(this.level, this.worldPosition, p_58760_, p_272556_, list, p_58761_, this.savedFlowerPos);
          }
          return false;
      });
      if (!list.isEmpty()) {
         super.setChanged();
      }

      return list;
   }

   public void addOccupant(Entity commonBee, boolean p_58743_) {
      this.addOccupantWithPresetTicks(commonBee, p_58743_, 0);
   }

   public int getOccupantCount() {
      return this.stored.size();
   }

   public static int getHoneyLevel(BlockState p_58753_) {
      return p_58753_.getValue(MysteriousBeehive.HONEY_LEVEL);
   }

   public boolean isSedated() {
       if (this.level != null) {
           return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
       }
       return false;
   }

   public void addOccupantWithPresetTicks(Entity commonBee, boolean p_58746_, int tickInHive) {
      if (this.stored.size() < 3) {
         commonBee.stopRiding();
         commonBee.ejectPassengers();
         CompoundTag compoundtag = new CompoundTag();
         commonBee.save(compoundtag);
         this.storeBee(compoundtag, tickInHive, p_58746_);
         if (this.level != null) {
            if (commonBee instanceof CommonBee) {
               CommonBee bee = (CommonBee)commonBee;
               if (bee.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                  this.savedFlowerPos = bee.getSavedFlowerPos();
               }
            }

            BlockPos blockpos = this.getBlockPos();
            this.level.playSound((Player)null, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(commonBee, this.getBlockState()));
         }

         commonBee.discard();
         super.setChanged();
      }
   }

   public void storeBee(CompoundTag tag, int ticksInHive, boolean minTickOccupation) {
      this.stored.add(new MysteriousBeehiveEntity.BeeData(tag, ticksInHive, minTickOccupation ? 3 : 1)); //default 2400 : 600
   }

   private static boolean releaseOccupant(Level level, BlockPos blockPos, BlockState blockState, MysteriousBeehiveEntity.BeeData beeData, @Nullable List<Entity> entityList, MysteriousBeehiveEntity.BeeReleaseStatus p_155142_, @Nullable BlockPos flowerPos) {
      if ((level.isNight() || level.isRaining()) && p_155142_ != MysteriousBeehiveEntity.BeeReleaseStatus.EMERGENCY) {
         return false;
      } else {
         CompoundTag compoundtag = beeData.entityData.copy();
         removeIgnoredBeeTags(compoundtag);
         compoundtag.put("HivePos", NbtUtils.writeBlockPos(blockPos));
         compoundtag.putBoolean("NoGravity", true);
         Direction direction = blockState.getValue(MysteriousBeehive.FACING);
         BlockPos blockpos = blockPos.relative(direction);
         boolean flag = !level.getBlockState(blockpos).getCollisionShape(level, blockpos).isEmpty();
         if (flag && p_155142_ != MysteriousBeehiveEntity.BeeReleaseStatus.EMERGENCY) {
            return false;
         } else {
            Entity entity = EntityType.loadEntityRecursive(compoundtag, level, (p_58740_) -> {
               return p_58740_;
            });
            if (entity instanceof CommonBee bee) {
               if (flowerPos != null && !bee.hasSavedFlowerPos() && level.random.nextFloat() < 0.9F) {
                  bee.setSavedFlowerPos(flowerPos);
               }
               if (p_155142_ == MysteriousBeehiveEntity.BeeReleaseStatus.HONEY_DELIVERED) {
                  bee.dropOffNectar();
                  int i = getHoneyLevel(blockState);
                  if (i < 5) {
                      int j = level.random.nextInt(100) == 0 ? 2 : 1;
                      if (i + j > 5)
                          --j;
                      level.setBlockAndUpdate(blockPos, blockState.setValue(MysteriousBeehive.HONEY_LEVEL, Integer.valueOf(i + j)));
                  }
               }

               setBeeReleaseData(beeData.ticksInHive, bee);
               if (entityList != null) {
                  entityList.add(bee);
               }

               float f = entity.getBbWidth();
               double d3 = flag ? 0.0D : 0.55D + (double)(f / 2.0F);
               double d0 = (double)blockPos.getX() + 0.5D + d3 * (double)direction.getStepX();
               double d1 = (double)blockPos.getY() + 0.5D - (double)(entity.getBbHeight() / 2.0F);
               double d2 = (double)blockPos.getZ() + 0.5D + d3 * (double)direction.getStepZ();
               entity.moveTo(d0, d1, d2, entity.getYRot(), entity.getXRot());

               level.playSound((Player)null, blockPos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
               level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(entity, level.getBlockState(blockPos)));
               return level.addFreshEntity(entity);
            } else {
               return false;
            }
         }
      }
   }

   static void removeIgnoredBeeTags(CompoundTag p_155162_) {
      for(String s : IGNORED_BEE_TAGS) {
         p_155162_.remove(s);
      }

   }

   private static void setBeeReleaseData(int p_58737_, CommonBee p_58738_) {
      int i = p_58738_.getAge();
      if (i < 0) {
         p_58738_.setAge(Math.min(0, i + p_58737_));
      } else if (i > 0) {
         p_58738_.setAge(Math.max(0, i - p_58737_));
      }

      p_58738_.setInLoveTime(Math.max(0, p_58738_.getInLoveTime() - p_58737_));
   }

   private boolean hasSavedFlowerPos() {
      return this.savedFlowerPos != null;
   }

   private static void tickOccupants(Level level, BlockPos blockPos, BlockState blockState, List<MysteriousBeehiveEntity.BeeData> beeDataList, @Nullable BlockPos flowerPos) {
      boolean flag = false;

      MysteriousBeehiveEntity.BeeData MysteriousBeehiveEntity$beedata;
      for(Iterator<BeeData> iterator = beeDataList.iterator(); iterator.hasNext(); ++MysteriousBeehiveEntity$beedata.ticksInHive) {
         MysteriousBeehiveEntity$beedata = iterator.next();
          System.out.printf("Ticks in hive : %d\n", MysteriousBeehiveEntity$beedata.ticksInHive);
         if (MysteriousBeehiveEntity$beedata.ticksInHive > MysteriousBeehiveEntity$beedata.minOccupationTicks) {
            MysteriousBeehiveEntity.BeeReleaseStatus MysteriousBeehiveEntity$beereleasestatus = MysteriousBeehiveEntity$beedata.entityData.getBoolean("HasNectar") ? MysteriousBeehiveEntity.BeeReleaseStatus.HONEY_DELIVERED : MysteriousBeehiveEntity.BeeReleaseStatus.BEE_RELEASED;
            if (releaseOccupant(level, blockPos, blockState, MysteriousBeehiveEntity$beedata, (List<Entity>)null, MysteriousBeehiveEntity$beereleasestatus, flowerPos)) {
               flag = true;
               iterator.remove();
            }
         }
      }

      if (flag) {
         setChanged(level, blockPos, blockState);
      }

   }

   public static <T extends BlockEntity> void serverTick(Level level, BlockPos pos, BlockState state, T t) {
       MysteriousBeehiveEntity entity = (MysteriousBeehiveEntity) t;
      tickOccupants(level, pos, state, entity.stored, entity.savedFlowerPos);
      if (!entity.stored.isEmpty() && level.getRandom().nextDouble() < 0.005D) {
         double d0 = (double)pos.getX() + 0.5D;
         double d1 = (double)pos.getY();
         double d2 = (double)pos.getZ() + 0.5D;
         level.playSound((Player)null, d0, d1, d2, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

    @Override
   public void load(CompoundTag tag) {
        super.load(tag);
        this.stored.clear();
        ListTag listtag = tag.getList("Bees", 10);

        this.currentProduction = ItemStack.of(tag.getCompound("Production"));
        for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            MysteriousBeehiveEntity.BeeData MysteriousBeehiveEntity$beedata = new MysteriousBeehiveEntity.BeeData(compoundtag.getCompound("EntityData"), compoundtag.getInt("TicksInHive"), compoundtag.getInt("MinOccupationTicks"));
            this.stored.add(MysteriousBeehiveEntity$beedata);
        }

        this.savedFlowerPos = null;
        if (tag.contains("FlowerPos")) {
            this.savedFlowerPos = NbtUtils.readBlockPos(tag.getCompound("FlowerPos"));
        }
    }

    @Override
   public void saveAdditional(CompoundTag tag) {
      tag.put("Bees", this.writeBees());
      if (this.hasSavedFlowerPos()) {
         tag.put("FlowerPos", NbtUtils.writeBlockPos(this.savedFlowerPos));
      }
      if (!this.currentProduction.isEmpty())
          tag.put("Production", this.currentProduction.serializeNBT());
      super.saveAdditional(tag);
   }

   public ListTag writeBees() {
      ListTag listtag = new ListTag();

      for(MysteriousBeehiveEntity.BeeData MysteriousBeehiveEntity$beedata : this.stored) {
         CompoundTag compoundtag = MysteriousBeehiveEntity$beedata.entityData.copy();
         compoundtag.remove("UUID");
         CompoundTag compoundtag1 = new CompoundTag();
         compoundtag1.put("EntityData", compoundtag);
         compoundtag1.putInt("TicksInHive", MysteriousBeehiveEntity$beedata.ticksInHive);
         compoundtag1.putInt("MinOccupationTicks", MysteriousBeehiveEntity$beedata.minOccupationTicks);
         listtag.add(compoundtag1);
      }

      return listtag;
   }

    static class BeeData {
      final CompoundTag entityData;
      int ticksInHive;
      final int minOccupationTicks;

      BeeData(CompoundTag tag, int ticksInHive, int minTicksOccupation) {
         MysteriousBeehiveEntity.removeIgnoredBeeTags(tag);
         this.entityData = tag;
         this.ticksInHive = ticksInHive;
         this.minOccupationTicks = minTicksOccupation;
      }
   }

   public static enum BeeReleaseStatus {
      HONEY_DELIVERED,
      BEE_RELEASED,
      EMERGENCY;
   }
}

