package com.elevasse.fantasbee.blockEntity;

import com.elevasse.fantasbee.entity.CommonBee;
import com.elevasse.fantasbee.block.CommonBeehive;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CommonBeehiveEntity extends BlockEntity {
    private ItemStack currentProduction;

    public static final String TAG_FLOWER_POS = "FlowerPos";
    public static final String MIN_OCCUPATION_TICKS = "MinOccupationTicks";
    public static final String ENTITY_DATA = "EntityData";
    public static final String TICKS_IN_HIVE = "TicksInHive";
    public static final String HAS_NECTAR = "HasNectar";
    public static final String BEES = "Bees";
    private static final List<String> IGNORED_BEE_TAGS = Arrays.asList("Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "HivePos", "Passengers", "Leash", "UUID");
    private static final int MIN_TICKS_BEFORE_REENTERING_HIVE = 400;
    private static final int MIN_OCCUPATION_TICKS_NECTAR = 2400;
    public static final int MIN_OCCUPATION_TICKS_NECTARLESS = 600;
    private final List<BeeData> stored = Lists.newArrayList();
    @Nullable
    private BlockPos savedFlowerPos;
    private int MaxHoneyLevel;
    private int MaxOccupants;

    public CommonBeehiveEntity(BlockPos pos, BlockState state) {
        super(RefBlockEntity.COMMON_BEEHIVE.get(), pos, state);
        currentProduction = Items.AIR.getDefaultInstance();
        MaxHoneyLevel = 5;
        MaxOccupants = 3;
    }

    public void setMaxHoneyLevel(int newMax){ MaxHoneyLevel = newMax;}
    public void setMaxOccupants(int newMax){ MaxOccupants = newMax;}
    public void setCurrentProduction( ItemStack item ){
        currentProduction = item;
    }


    public int getMaxHoneyLevel(){ return MaxHoneyLevel;}
    public int getMaxOccupants(){ return MaxOccupants;}
    public ItemStack getCurrentProduction( ){ return currentProduction;}

   public void setChanged() {
      if (this.isFireNearby() && this.level != null) {
              this.emptyAllLivingFromHive(null, this.level.getBlockState(this.getBlockPos()), BeeReleaseStatus.EMERGENCY);
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
      return this.stored.size() == this.MaxOccupants;
   }

   public void emptyAllLivingFromHive(@Nullable Player p_58749_, BlockState p_58750_, CommonBeehiveEntity.BeeReleaseStatus p_58751_) {
      List<Entity> list = this.releaseAllOccupants(p_58750_, p_58751_);
      if (p_58749_ != null) {
         for(Entity entity : list) {
            if (entity instanceof CommonBee bee) {
                if (p_58749_.position().distanceToSqr(entity.position()) <= 16.0D) {
                  if (!this.isSedated()) {
                     bee.setTarget(p_58749_);
                  } else {
                     bee.setStayOutOfHiveCountdown(MIN_TICKS_BEFORE_REENTERING_HIVE);
                  }
               }
            }
         }
      }

   }

   private List<Entity> releaseAllOccupants(BlockState blockState, CommonBeehiveEntity.BeeReleaseStatus beeReleaseStatus) {
      List<Entity> list = Lists.newArrayList();
      this.stored.removeIf((beeData) -> {
          if (this.level != null) {
              return releaseOccupant(this.level, this.worldPosition, blockState, beeData, list, beeReleaseStatus, this.savedFlowerPos);
          }
          return false;
      });
      if (!list.isEmpty()) {
         super.setChanged();
      }

      return list;
   }

   public void addOccupant(Entity commonBee, boolean hasNectar, int gathering_level) {
        this.addOccupantWithPresetTicks(commonBee, hasNectar, 0, gathering_level);
   }

   public int getOccupantCount() {
      return this.stored.size();
   }

   public static int getHoneyLevel(BlockState p_58753_) {
      return p_58753_.getValue(CommonBeehive.HONEY_LEVEL);
   }

   public boolean isSedated() {
       if (this.level != null) {
           return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
       }
       return false;
   }

   public void addOccupantWithPresetTicks(Entity commonBee, boolean hasNectar, int tickInHive, int gathering_level) {
      if (this.stored.size() < this.MaxOccupants && commonBee instanceof CommonBee bee) {
         commonBee.stopRiding();
         commonBee.ejectPassengers();
         CompoundTag compoundtag = new CompoundTag();
         commonBee.save(compoundtag);
          this.storeBee(compoundtag, tickInHive, hasNectar, gathering_level);
         if (this.level != null) {
            if (this.getCurrentProduction().is(Items.AIR)){
                this.setCurrentProduction(bee.getFlowerProduction());
            }
            if (bee.hasSavedFlowerPos() && !this.hasSavedFlowerPos()) {
              this.savedFlowerPos = bee.getSavedFlowerPos();
            }

            BlockPos blockpos = this.getBlockPos();
            this.level.playSound(null, blockpos.getX(), blockpos.getY(), blockpos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(commonBee, this.getBlockState()));
         }

         commonBee.discard();
         super.setChanged();
      }
   }

   public void storeBee(CompoundTag tag, int ticksInHive, boolean minTickOccupation, int gatheringLevel) {
      this.stored.add(new CommonBeehiveEntity.BeeData(tag, ticksInHive, minTickOccupation ? 3 : 1, gatheringLevel)); //default 2400 : 600
   }

   private static boolean releaseOccupant(Level level, BlockPos blockPos, BlockState blockState, CommonBeehiveEntity.BeeData beeData, @Nullable List<Entity> entityList, CommonBeehiveEntity.BeeReleaseStatus p_155142_, @Nullable BlockPos flowerPos) {
      if ((level.isNight() || level.isRaining()) && p_155142_ != CommonBeehiveEntity.BeeReleaseStatus.EMERGENCY) {
         return false;
      } else {
         CompoundTag compoundtag = beeData.entityData.copy();
         removeIgnoredBeeTags(compoundtag);
         compoundtag.put("HivePos", NbtUtils.writeBlockPos(blockPos));
         compoundtag.putBoolean("NoGravity", true);
         Direction direction = blockState.getValue(CommonBeehive.FACING);
         BlockPos blockpos = blockPos.relative(direction);
         boolean flag = !level.getBlockState(blockpos).getCollisionShape(level, blockpos).isEmpty();
         if (flag && p_155142_ != CommonBeehiveEntity.BeeReleaseStatus.EMERGENCY) {
            return false;
         } else {
            Entity entity = EntityType.loadEntityRecursive(compoundtag, level, (p_58740_) -> {
               return p_58740_;
            });
            if (entity instanceof CommonBee bee) {
               if (flowerPos != null && !bee.hasSavedFlowerPos() && level.random.nextFloat() < 0.9F) {
                  bee.setSavedFlowerPos(flowerPos);
               }
               bee.setStayOutOfHiveCountdown(MIN_TICKS_BEFORE_REENTERING_HIVE);
               bee.setGathering_level(beeData.gatheringLvl);
               if (p_155142_ == CommonBeehiveEntity.BeeReleaseStatus.HONEY_DELIVERED) {
                  bee.dropOffNectar();
                  int i = getHoneyLevel(blockState);
                  CommonBeehiveEntity commonBeehiveEntity = (CommonBeehiveEntity) level.getBlockEntity(blockPos);
                  if (commonBeehiveEntity != null && i < commonBeehiveEntity.MaxHoneyLevel) {
                      int j;
                      if (bee.getGathering_level() != 0)
                          j = level.random.nextInt(100 / bee.getGathering_level()) == 0 ? 2 : 1;
                      else
                          j = level.random.nextInt(100) == 0 ? 2 : 1;
                      if (i + j > commonBeehiveEntity.MaxHoneyLevel)
                          --j;
                      level.setBlockAndUpdate(blockPos, blockState.setValue(CommonBeehive.HONEY_LEVEL, i + j));
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

               level.playSound(null, blockPos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
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

   private static void tickOccupants(Level level, BlockPos blockPos, BlockState blockState, List<CommonBeehiveEntity.BeeData> beeDataList, @Nullable BlockPos flowerPos) {
      boolean flag = false;

      CommonBeehiveEntity.BeeData CommonBeehiveEntity$beedata;
      for(Iterator<BeeData> iterator = beeDataList.iterator(); iterator.hasNext(); ++CommonBeehiveEntity$beedata.ticksInHive) {
         CommonBeehiveEntity$beedata = iterator.next();
  //        System.out.printf("Ticks in hive : %d\n", CommonBeehiveEntity$beedata.ticksInHive);
         if (CommonBeehiveEntity$beedata.ticksInHive > CommonBeehiveEntity$beedata.minOccupationTicks) {
            CommonBeehiveEntity.BeeReleaseStatus CommonBeehiveEntity$beereleasestatus = CommonBeehiveEntity$beedata.entityData.getBoolean("HasNectar") ? CommonBeehiveEntity.BeeReleaseStatus.HONEY_DELIVERED : CommonBeehiveEntity.BeeReleaseStatus.BEE_RELEASED;
            if (releaseOccupant(level, blockPos, blockState, CommonBeehiveEntity$beedata, null, CommonBeehiveEntity$beereleasestatus, flowerPos)) {
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
       CommonBeehiveEntity entity = (CommonBeehiveEntity) t;
      tickOccupants(level, pos, state, entity.stored, entity.savedFlowerPos);
      if (!entity.stored.isEmpty() && level.getRandom().nextDouble() < 0.005D) {
         double d0 = (double)pos.getX() + 0.5D;
         double d1 = pos.getY();
         double d2 = (double)pos.getZ() + 0.5D;
         level.playSound(null, d0, d1, d2, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
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
            CommonBeehiveEntity.BeeData CommonBeehiveEntity$beedata = new CommonBeehiveEntity.BeeData(compoundtag.getCompound("EntityData"),
                    compoundtag.getInt("TicksInHive"),
                    compoundtag.getInt("MinOccupationTicks"),
                    compoundtag.getInt(CommonBee.TAG_GATHERING_LVL));
            this.stored.add(CommonBeehiveEntity$beedata);
        }
        this.MaxHoneyLevel = tag.getInt("MaxLevel");
        this.MaxOccupants = tag.getInt("MaxOccupant");

        this.savedFlowerPos = null;
        if (tag.contains("FlowerPos")) {
            this.savedFlowerPos = NbtUtils.readBlockPos(tag.getCompound("FlowerPos"));
        }
    }

    @Override
   public void saveAdditional(CompoundTag tag) {
        //System.out.print("Saving Common beehive\n");
        tag.put("Bees", this.writeBees());
        tag.putInt("MaxLevel", MaxHoneyLevel);
        tag.putInt("MaxOccupant", MaxOccupants);
      if (this.hasSavedFlowerPos()) {
         tag.put("FlowerPos", NbtUtils.writeBlockPos(this.savedFlowerPos));
      }
      tag.put("Production", this.currentProduction.serializeNBT());
      super.saveAdditional(tag);
   }

   public ListTag writeBees() {
      ListTag listtag = new ListTag();

      for(CommonBeehiveEntity.BeeData CommonBeehiveEntity$beedata : this.stored) {
         CompoundTag compoundtag = CommonBeehiveEntity$beedata.entityData.copy();
         compoundtag.remove("UUID");
         CompoundTag compoundtag1 = new CompoundTag();
         compoundtag1.put("EntityData", compoundtag);
         compoundtag1.putInt("TicksInHive", CommonBeehiveEntity$beedata.ticksInHive);
         compoundtag1.putInt("MinOccupationTicks", CommonBeehiveEntity$beedata.minOccupationTicks);
         compoundtag1.putInt(CommonBee.TAG_GATHERING_LVL, CommonBeehiveEntity$beedata.gatheringLvl);
         listtag.add(compoundtag1);
      }

      return listtag;
   }

    static class BeeData {
      final CompoundTag entityData;
      int ticksInHive;
      int gatheringLvl;
      final int minOccupationTicks;

      BeeData(CompoundTag tag, int ticksInHive, int minTicksOccupation, int gatheringLvl) {
         CommonBeehiveEntity.removeIgnoredBeeTags(tag);
         this.entityData = tag;
         this.ticksInHive = ticksInHive;
         this.gatheringLvl = gatheringLvl;
         this.minOccupationTicks = minTicksOccupation;
      }
   }

   public enum BeeReleaseStatus {
      HONEY_DELIVERED,
      BEE_RELEASED,
      EMERGENCY
   }
}

