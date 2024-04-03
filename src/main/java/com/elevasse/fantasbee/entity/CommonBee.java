package com.elevasse.fantasbee.entity;

import com.elevasse.fantasbee.blockEntity.MysteriousBeehiveEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

public class CommonBee extends Bee {
    public CommonBee(EntityType<CommonBee> type, Level level) {
        super(type, level);

        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.lookControl = new BeeLookControl(this);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
    }

    public CommonBee(ServerLevel level, double x, double y, double z) {
        this(RefEntities.COMMON_BEE.get(), level);
        setPos(x, y, z);
    }
    public CommonBee(ServerLevel level, BlockPos blockPos) {
        this(level, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Nullable
    @Override
    public CommonBee getBreedOffspring(ServerLevel p_148760_, AgeableMob p_148761_) {
        return new CommonBee(p_148760_, this.blockPosition());
    }

    public static boolean canSpawn(EntityType<CommonBee> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random){
        return Animal.checkAnimalSpawnRules(entityType, level, spawnType, pos, random) && !level.getLevelData().isRaining();
    }

   public static final float FLAP_DEGREES_PER_TICK = 120.32113F;
   public static final int TICKS_PER_FLAP = Mth.ceil(1.4959966F);
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Bee.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(Bee.class, EntityDataSerializers.INT);
   private static final int FLAG_ROLL = 2;
   private static final int FLAG_HAS_STUNG = 4;
   private static final int FLAG_HAS_NECTAR = 8;
   private static final int STING_DEATH_COUNTDOWN = 1200;
   private static final int TICKS_BEFORE_GOING_TO_KNOWN_FLOWER = 2400;
   private static final int TICKS_WITHOUT_NECTAR_BEFORE_GOING_HOME = 3600;
   private static final int MIN_ATTACK_DIST = 4;
   private static final int MAX_CROPS_GROWABLE = 10;
   private static final int POISON_SECONDS_NORMAL = 10;
   private static final int POISON_SECONDS_HARD = 18;
   private static final int TOO_FAR_DISTANCE = 32;
   private static final int HIVE_CLOSE_ENOUGH_DISTANCE = 2;
   private static final int PATHFIND_TO_HIVE_WHEN_CLOSER_THAN = 16;
   private static final int HIVE_SEARCH_DISTANCE = 20;
   public static final String TAG_CROPS_GROWN_SINCE_POLLINATION = "CropsGrownSincePollination";
   public static final String TAG_CANNOT_ENTER_HIVE_TICKS = "CannotEnterHiveTicks";
   public static final String TAG_TICKS_SINCE_POLLINATION = "TicksSincePollination";
   public static final String TAG_HAS_STUNG = "HasStung";
   public static final String TAG_HAS_NECTAR = "HasNectar";
   public static final String TAG_FLOWER_POS = "FlowerPos";
   public static final String TAG_HIVE_POS = "HivePos";
   private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
   @Nullable
   private UUID persistentAngerTarget;
   private float rollAmount;
   private float rollAmountO;
   private int timeSinceSting;
   int ticksWithoutNectarSinceExitingHive;
   private int stayOutOfHiveCountdown;
   private int numCropsGrownSincePollination;
   private static final int COOLDOWN_BEFORE_LOCATING_NEW_HIVE = 200;
   int remainingCooldownBeforeLocatingNewHive;
   private static final int COOLDOWN_BEFORE_LOCATING_NEW_FLOWER = 200;
   int remainingCooldownBeforeLocatingNewFlower = Mth.nextInt(this.random, 20, 60);
   @Nullable
   BlockPos savedFlowerPos;
   @Nullable
   BlockPos hivePos;
   BeePollinateGoal beePollinateGoal;
   BeeGoToHiveGoal goToHiveGoal;
   private BeeGoToKnownFlowerGoal goToKnownFlowerGoal;
   private int underWaterTicks;

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
      this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
   }

   public float getWalkTargetValue(BlockPos p_27788_, LevelReader p_27789_) {
      return p_27789_.getBlockState(p_27788_).isAir() ? 10.0F : 0.0F;
   }

   @Override
   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new BeeAttackGoal(this, (double)1.4F, true));
      this.goalSelector.addGoal(1, new BeeEnterHiveGoal());
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(ItemTags.FLOWERS), false));
      this.beePollinateGoal = new BeePollinateGoal();
      this.goalSelector.addGoal(4, this.beePollinateGoal);
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(5, new BeeLocateHiveGoal());
      this.goToHiveGoal = new BeeGoToHiveGoal();
      this.goalSelector.addGoal(5, this.goToHiveGoal);
      this.goToKnownFlowerGoal = new BeeGoToKnownFlowerGoal();
      this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);
      this.goalSelector.addGoal(7, new BeeGrowCropGoal());
      this.goalSelector.addGoal(8, new BeeWanderGoal());
      this.goalSelector.addGoal(9, new FloatGoal(this));
      this.targetSelector.addGoal(1, (new BeeHurtByOtherGoal(this)).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new BeeBecomeAngryTargetGoal(this));
      this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
   }
   
   public void addAdditionalSaveData(CompoundTag p_27823_) {
      super.addAdditionalSaveData(p_27823_);
      if (this.hasHive()) {
         p_27823_.put("HivePos", NbtUtils.writeBlockPos(this.getHivePos()));
      }

      if (this.hasSavedFlowerPos()) {
         p_27823_.put("FlowerPos", NbtUtils.writeBlockPos(this.getSavedFlowerPos()));
      }

      p_27823_.putBoolean("HasNectar", this.hasNectar());
      p_27823_.putBoolean("HasStung", this.hasStung());
      p_27823_.putInt("TicksSincePollination", this.ticksWithoutNectarSinceExitingHive);
      p_27823_.putInt("CannotEnterHiveTicks", this.stayOutOfHiveCountdown);
      p_27823_.putInt("CropsGrownSincePollination", this.numCropsGrownSincePollination);
      this.addPersistentAngerSaveData(p_27823_);
   }

   public void readAdditionalSaveData(CompoundTag p_27793_) {
      this.hivePos = null;
      if (p_27793_.contains("HivePos")) {
         this.hivePos = NbtUtils.readBlockPos(p_27793_.getCompound("HivePos"));
      }

      this.savedFlowerPos = null;
      if (p_27793_.contains("FlowerPos")) {
         this.savedFlowerPos = NbtUtils.readBlockPos(p_27793_.getCompound("FlowerPos"));
      }

      super.readAdditionalSaveData(p_27793_);
      this.setHasNectar(p_27793_.getBoolean("HasNectar"));
      this.setHasStung(p_27793_.getBoolean("HasStung"));
      this.ticksWithoutNectarSinceExitingHive = p_27793_.getInt("TicksSincePollination");
      this.stayOutOfHiveCountdown = p_27793_.getInt("CannotEnterHiveTicks");
      this.numCropsGrownSincePollination = p_27793_.getInt("CropsGrownSincePollination");
      this.readPersistentAngerSaveData(this.level, p_27793_);
   }

   public boolean doHurtTarget(Entity p_27722_) {
      boolean flag = p_27722_.hurt(this.damageSources().sting(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
      if (flag) {
         this.doEnchantDamageEffects(this, p_27722_);
         if (p_27722_ instanceof LivingEntity) {
            ((LivingEntity)p_27722_).setStingerCount(((LivingEntity)p_27722_).getStingerCount() + 1);
            int i = 0;
            if (this.level.getDifficulty() == Difficulty.NORMAL) {
               i = 10;
            } else if (this.level.getDifficulty() == Difficulty.HARD) {
               i = 18;
            }

            if (i > 0) {
               ((LivingEntity)p_27722_).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 0), this);
            }
         }

         this.setHasStung(true);
         this.stopBeingAngry();
         this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
      }

      return flag;
   }

   public void tick() {
      super.tick();
      if (this.hasNectar() && this.getCropsGrownSincePollination() < 10 && this.random.nextFloat() < 0.05F) {
         for(int i = 0; i < this.random.nextInt(2) + 1; ++i) {
            this.spawnFluidParticle(this.level, this.getX() - (double)0.3F, this.getX() + (double)0.3F, this.getZ() - (double)0.3F, this.getZ() + (double)0.3F, this.getY(0.5D), ParticleTypes.FALLING_NECTAR);
         }
      }

      this.updateRollAmount();
   }

   private void spawnFluidParticle(Level p_27780_, double p_27781_, double p_27782_, double p_27783_, double p_27784_, double p_27785_, ParticleOptions p_27786_) {
      p_27780_.addParticle(p_27786_, Mth.lerp(p_27780_.random.nextDouble(), p_27781_, p_27782_), p_27785_, Mth.lerp(p_27780_.random.nextDouble(), p_27783_, p_27784_), 0.0D, 0.0D, 0.0D);
   }

   void pathfindRandomlyTowards(BlockPos p_27881_) {
      Vec3 vec3 = Vec3.atBottomCenterOf(p_27881_);
      int i = 0;
      BlockPos blockpos = this.blockPosition();
      int j = (int)vec3.y - blockpos.getY();
      if (j > 2) {
         i = 4;
      } else if (j < -2) {
         i = -4;
      }

      int k = 6;
      int l = 8;
      int i1 = blockpos.distManhattan(p_27881_);
      if (i1 < 15) {
         k = i1 / 2;
         l = i1 / 2;
      }

      Vec3 vec31 = AirRandomPos.getPosTowards(this, k, l, i, vec3, (double)((float)Math.PI / 10F));
      if (vec31 != null) {
         this.navigation.setMaxVisitedNodesMultiplier(0.5F);
         this.navigation.moveTo(vec31.x, vec31.y, vec31.z, 1.0D);
      }
   }

   @Nullable
   public BlockPos getSavedFlowerPos() {
      return this.savedFlowerPos;
   }

   public boolean hasSavedFlowerPos() {
      return this.savedFlowerPos != null;
   }

   public void setSavedFlowerPos(BlockPos p_27877_) {
      this.savedFlowerPos = p_27877_;
   }

   @VisibleForDebug
   public int getTravellingTicks() {
      return Math.max(this.goToHiveGoal.travellingTicks, this.goToKnownFlowerGoal.travellingTicks);
   }

   @VisibleForDebug
   public List<BlockPos> getBlacklistedHives() {
      return this.goToHiveGoal.blacklistedTargets;
   }

   private boolean isTiredOfLookingForNectar() {
      return this.ticksWithoutNectarSinceExitingHive > 3600;
   }

   boolean wantsToEnterHive() {
      if (this.stayOutOfHiveCountdown <= 0 && !this.beePollinateGoal.isPollinating() && !this.hasStung() && this.getTarget() == null) {
         boolean flag = this.isTiredOfLookingForNectar() || this.level.isRaining() || this.level.isNight() || this.hasNectar();
         return flag && !this.isHiveNearFire();
      } else {
         return false;
      }
   }

   public void setStayOutOfHiveCountdown(int p_27916_) {
      this.stayOutOfHiveCountdown = p_27916_;
   }

   public float getRollAmount(float p_27936_) {
      return Mth.lerp(p_27936_, this.rollAmountO, this.rollAmount);
   }

   private void updateRollAmount() {
      this.rollAmountO = this.rollAmount;
      if (this.isRolling()) {
         this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
      } else {
         this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
      }

   }

   protected void customServerAiStep() {
      boolean flag = this.hasStung();
      if (this.isInWaterOrBubble()) {
         ++this.underWaterTicks;
      } else {
         this.underWaterTicks = 0;
      }

      if (this.underWaterTicks > 20) {
         this.hurt(this.damageSources().drown(), 1.0F);
      }

      if (flag) {
         ++this.timeSinceSting;
         if (this.timeSinceSting % 5 == 0 && this.random.nextInt(Mth.clamp(1200 - this.timeSinceSting, 1, 1200)) == 0) {
            this.hurt(this.damageSources().generic(), this.getHealth());
         }
      }

      if (!this.hasNectar()) {
         ++this.ticksWithoutNectarSinceExitingHive;
      }

      if (!this.level.isClientSide) {
         this.updatePersistentAnger((ServerLevel)this.level, false);
      }

   }

   public void resetTicksWithoutNectarSinceExitingHive() {
      this.ticksWithoutNectarSinceExitingHive = 0;
   }

   private boolean isHiveNearFire() {
      if (this.hivePos == null) {
         return false;
      } else {
         BlockEntity blockentity = this.level.getBlockEntity(this.hivePos);
         return blockentity instanceof MysteriousBeehiveEntity && ((MysteriousBeehiveEntity)blockentity).isFireNearby();
      }
   }

   public int getRemainingPersistentAngerTime() {
      return this.entityData.get(DATA_REMAINING_ANGER_TIME);
   }

   public void setRemainingPersistentAngerTime(int p_27795_) {
      this.entityData.set(DATA_REMAINING_ANGER_TIME, p_27795_);
   }

   @Nullable
   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public void setPersistentAngerTarget(@Nullable UUID p_27791_) {
      this.persistentAngerTarget = p_27791_;
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
   }

   private boolean doesHiveHaveSpace(BlockPos p_27885_) {
      BlockEntity blockentity = this.level.getBlockEntity(p_27885_);
      if (blockentity instanceof MysteriousBeehiveEntity) {
         return !((MysteriousBeehiveEntity)blockentity).isFull();
      } else {
         return false;
      }
   }

   @VisibleForDebug
   public boolean hasHive() {
      return this.hivePos != null;
   }

   @Nullable
   @VisibleForDebug
   public BlockPos getHivePos() {
      return this.hivePos;
   }

   @VisibleForDebug
   public GoalSelector getGoalSelector() {
      return this.goalSelector;
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendBeeInfo(this);
   }

   int getCropsGrownSincePollination() {
      return this.numCropsGrownSincePollination;
   }

   private void resetNumCropsGrownSincePollination() {
      this.numCropsGrownSincePollination = 0;
   }

   void incrementNumCropsGrownSincePollination() {
      ++this.numCropsGrownSincePollination;
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level.isClientSide) {
         if (this.stayOutOfHiveCountdown > 0) {
            --this.stayOutOfHiveCountdown;
         }

         if (this.remainingCooldownBeforeLocatingNewHive > 0) {
            --this.remainingCooldownBeforeLocatingNewHive;
         }

         if (this.remainingCooldownBeforeLocatingNewFlower > 0) {
            --this.remainingCooldownBeforeLocatingNewFlower;
         }

         boolean flag = this.isAngry() && !this.hasStung() && this.getTarget() != null && this.getTarget().distanceToSqr(this) < 4.0D;
         this.setRolling(flag);
         if (this.tickCount % 20 == 0 && !this.isHiveValid()) {
            this.hivePos = null;
         }
      }

   }

   boolean isHiveValid() {
      if (!this.hasHive()) {
         return false;
      } else if (this.isTooFarAway(this.hivePos)) {
         return false;
      } else {
         BlockEntity blockentity = this.level.getBlockEntity(this.hivePos);
         return blockentity instanceof MysteriousBeehiveEntity;
      }
   }

   public boolean hasNectar() {
      return this.getFlag(8);
   }

   void setHasNectar(boolean p_27920_) {
      if (p_27920_) {
         this.resetTicksWithoutNectarSinceExitingHive();
      }

      this.setFlag(8, p_27920_);
   }

   public boolean hasStung() {
      return this.getFlag(4);
   }

   private void setHasStung(boolean p_27926_) {
      this.setFlag(4, p_27926_);
   }

   private boolean isRolling() {
      return this.getFlag(2);
   }

   private void setRolling(boolean p_27930_) {
      this.setFlag(2, p_27930_);
   }

   boolean isTooFarAway(BlockPos p_27890_) {
      return !this.closerThan(p_27890_, 32);
   }

   private void setFlag(int p_27833_, boolean p_27834_) {
      if (p_27834_) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) | p_27833_));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) & ~p_27833_));
      }

   }

   private boolean getFlag(int p_27922_) {
      return (this.entityData.get(DATA_FLAGS_ID) & p_27922_) != 0;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FLYING_SPEED, (double)0.6F).add(Attributes.MOVEMENT_SPEED, (double)0.3F).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.FOLLOW_RANGE, 48.0D);
   }

   protected PathNavigation createNavigation(Level p_27815_) {
      FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_27815_) {
         public boolean isStableDestination(BlockPos p_27947_) {
            return !this.level.getBlockState(p_27947_.below()).isAir();
         }

         public void tick() {
            if (!CommonBee.this.beePollinateGoal.isPollinating()) {
               super.tick();
            }
         }
      };
      flyingpathnavigation.setCanOpenDoors(false);
      flyingpathnavigation.setCanFloat(false);
      flyingpathnavigation.setCanPassDoors(true);
      return flyingpathnavigation;
   }

   public boolean isFood(ItemStack p_27895_) {
      return p_27895_.is(ItemTags.FLOWERS);
   }

   boolean isFlowerValid(BlockPos p_27897_) {
      return this.level.isLoaded(p_27897_) && this.level.getBlockState(p_27897_).is(BlockTags.FLOWERS);
   }

   protected void playStepSound(BlockPos p_27820_, BlockState p_27821_) {
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected SoundEvent getHurtSound(DamageSource p_27845_) {
      return SoundEvents.BEE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.BEE_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected float getStandingEyeHeight(Pose p_27804_, EntityDimensions p_27805_) {
      return this.isBaby() ? p_27805_.height * 0.5F : p_27805_.height * 0.5F;
   }

   protected void checkFallDamage(double p_27754_, boolean p_27755_, BlockState p_27756_, BlockPos p_27757_) {
   }

   public boolean isFlapping() {
      return this.isFlying() && this.tickCount % TICKS_PER_FLAP == 0;
   }

   public boolean isFlying() {
      return !this.onGround;
   }

   public void dropOffNectar() {
      this.setHasNectar(false);
      this.resetNumCropsGrownSincePollination();
   }

   public boolean hurt(DamageSource p_27762_, float p_27763_) {
      if (this.isInvulnerableTo(p_27762_)) {
         return false;
      } else {
         if (!this.level.isClientSide) {
            this.beePollinateGoal.stopPollinating();
         }

         return super.hurt(p_27762_, p_27763_);
      }
   }

   public MobType getMobType() {
      return MobType.ARTHROPOD;
   }

   @Deprecated // FORGE: use jumpInFluid instead
   protected void jumpInLiquid(TagKey<Fluid> p_204061_) {
      this.jumpInLiquidInternal();
   }

   private void jumpInLiquidInternal() {
      this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.01D, 0.0D));
   }

   @Override
   public void jumpInFluid(FluidType type) {
      this.jumpInLiquidInternal();
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, (double)(0.5F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.2F));
   }

   boolean closerThan(BlockPos p_27817_, int p_27818_) {
      return p_27817_.closerThan(this.blockPosition(), (double)p_27818_);
   }

   abstract class BaseBeeGoal extends Goal {
      public abstract boolean canBeeUse();

      public abstract boolean canBeeContinueToUse();

      public boolean canUse() {
         return this.canBeeUse() && !CommonBee.this.isAngry();
      }

      public boolean canContinueToUse() {
         return this.canBeeContinueToUse() && !CommonBee.this.isAngry();
      }
   }

   class BeeAttackGoal extends MeleeAttackGoal {
      BeeAttackGoal(PathfinderMob p_27960_, double p_27961_, boolean p_27962_) {
         super(p_27960_, p_27961_, p_27962_);
      }

      public boolean canUse() {
         return super.canUse() && CommonBee.this.isAngry() && !CommonBee.this.hasStung();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && CommonBee.this.isAngry() && !CommonBee.this.hasStung();
      }
   }

   static class BeeBecomeAngryTargetGoal extends NearestAttackableTargetGoal<Player> {
      BeeBecomeAngryTargetGoal(CommonBee p_27966_) {
         super(p_27966_, Player.class, 10, true, false, p_27966_::isAngryAt);
      }

      public boolean canUse() {
         return this.beeCanTarget() && super.canUse();
      }

      public boolean canContinueToUse() {
         boolean flag = this.beeCanTarget();
         if (flag && this.mob.getTarget() != null) {
            return super.canContinueToUse();
         } else {
            this.targetMob = null;
            return false;
         }
      }

      private boolean beeCanTarget() {
         CommonBee bee = (CommonBee)this.mob;
         return bee.isAngry() && !bee.hasStung();
      }
   }

   class BeeEnterHiveGoal extends BaseBeeGoal {
      public boolean canBeeUse() {
         if (CommonBee.this.hasHive() && CommonBee.this.wantsToEnterHive() && CommonBee.this.hivePos.closerToCenterThan(CommonBee.this.position(), 2.0D)) {
            BlockEntity blockentity = CommonBee.this.level.getBlockEntity(CommonBee.this.hivePos);
            if (blockentity instanceof MysteriousBeehiveEntity) {
               MysteriousBeehiveEntity beehiveblockentity = (MysteriousBeehiveEntity)blockentity;
               if (!beehiveblockentity.isFull()) {
                  return true;
               }

               CommonBee.this.hivePos = null;
            }
         }

         return false;
      }

      public boolean canBeeContinueToUse() {
         return false;
      }

      public void start() {
         BlockEntity blockentity = CommonBee.this.level.getBlockEntity(CommonBee.this.hivePos);
         if (blockentity instanceof MysteriousBeehiveEntity beehiveblockentity) {
            beehiveblockentity.addOccupant(CommonBee.this, CommonBee.this.hasNectar());
         }

      }
   }

   @VisibleForDebug
   public class BeeGoToHiveGoal extends BaseBeeGoal {
      public static final int MAX_TRAVELLING_TICKS = 600;
      int travellingTicks = CommonBee.this.level.random.nextInt(10);
      private static final int MAX_BLACKLISTED_TARGETS = 3;
      final List<BlockPos> blacklistedTargets = Lists.newArrayList();
      @Nullable
      private Path lastPath;
      private static final int TICKS_BEFORE_HIVE_DROP = 60;
      private int ticksStuck;

      BeeGoToHiveGoal() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canBeeUse() {
         return CommonBee.this.hivePos != null && !CommonBee.this.hasRestriction() && CommonBee.this.wantsToEnterHive() && !this.hasReachedTarget(CommonBee.this.hivePos) && CommonBee.this.level.getBlockState(CommonBee.this.hivePos).is(BlockTags.BEEHIVES);
      }

      public boolean canBeeContinueToUse() {
         return this.canBeeUse();
      }

      public void start() {
         this.travellingTicks = 0;
         this.ticksStuck = 0;
         super.start();
      }

      public void stop() {
         this.travellingTicks = 0;
         this.ticksStuck = 0;
         CommonBee.this.navigation.stop();
         CommonBee.this.navigation.resetMaxVisitedNodesMultiplier();
      }

      public void tick() {
         if (CommonBee.this.hivePos != null) {
            ++this.travellingTicks;
            if (this.travellingTicks > this.adjustedTickDelay(600)) {
               this.dropAndBlacklistHive();
            } else if (!CommonBee.this.navigation.isInProgress()) {
               if (!CommonBee.this.closerThan(CommonBee.this.hivePos, 16)) {
                  if (CommonBee.this.isTooFarAway(CommonBee.this.hivePos)) {
                     this.dropHive();
                  } else {
                     CommonBee.this.pathfindRandomlyTowards(CommonBee.this.hivePos);
                  }
               } else {
                  boolean flag = this.pathfindDirectlyTowards(CommonBee.this.hivePos);
                  if (!flag) {
                     this.dropAndBlacklistHive();
                  } else if (this.lastPath != null && CommonBee.this.navigation.getPath().sameAs(this.lastPath)) {
                     ++this.ticksStuck;
                     if (this.ticksStuck > 60) {
                        this.dropHive();
                        this.ticksStuck = 0;
                     }
                  } else {
                     this.lastPath = CommonBee.this.navigation.getPath();
                  }

               }
            }
         }
      }

      private boolean pathfindDirectlyTowards(BlockPos p_27991_) {
         CommonBee.this.navigation.setMaxVisitedNodesMultiplier(10.0F);
         CommonBee.this.navigation.moveTo((double)p_27991_.getX(), (double)p_27991_.getY(), (double)p_27991_.getZ(), 1.0D);
         return CommonBee.this.navigation.getPath() != null && CommonBee.this.navigation.getPath().canReach();
      }

      boolean isTargetBlacklisted(BlockPos p_27994_) {
         return this.blacklistedTargets.contains(p_27994_);
      }

      private void blacklistTarget(BlockPos p_27999_) {
         this.blacklistedTargets.add(p_27999_);

         while(this.blacklistedTargets.size() > 3) {
            this.blacklistedTargets.remove(0);
         }

      }

      void clearBlacklist() {
         this.blacklistedTargets.clear();
      }

      private void dropAndBlacklistHive() {
         if (CommonBee.this.hivePos != null) {
            this.blacklistTarget(CommonBee.this.hivePos);
         }

         this.dropHive();
      }

      private void dropHive() {
         CommonBee.this.hivePos = null;
         CommonBee.this.remainingCooldownBeforeLocatingNewHive = 200;
      }

      private boolean hasReachedTarget(BlockPos p_28002_) {
         if (CommonBee.this.closerThan(p_28002_, 2)) {
            return true;
         } else {
            Path path = CommonBee.this.navigation.getPath();
            return path != null && path.getTarget().equals(p_28002_) && path.canReach() && path.isDone();
         }
      }
   }

   public class BeeGoToKnownFlowerGoal extends BaseBeeGoal {
      private static final int MAX_TRAVELLING_TICKS = 600;
      int travellingTicks = CommonBee.this.level.random.nextInt(10);

      BeeGoToKnownFlowerGoal() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canBeeUse() {
         return CommonBee.this.savedFlowerPos != null && !CommonBee.this.hasRestriction() && this.wantsToGoToKnownFlower() && CommonBee.this.isFlowerValid(CommonBee.this.savedFlowerPos) && !CommonBee.this.closerThan(CommonBee.this.savedFlowerPos, 2);
      }

      public boolean canBeeContinueToUse() {
         return this.canBeeUse();
      }

      public void start() {
         this.travellingTicks = 0;
         super.start();
      }

      public void stop() {
         this.travellingTicks = 0;
         CommonBee.this.navigation.stop();
         CommonBee.this.navigation.resetMaxVisitedNodesMultiplier();
      }

      public void tick() {
         if (CommonBee.this.savedFlowerPos != null) {
            ++this.travellingTicks;
            if (this.travellingTicks > this.adjustedTickDelay(600)) {
               CommonBee.this.savedFlowerPos = null;
            } else if (!CommonBee.this.navigation.isInProgress()) {
               if (CommonBee.this.isTooFarAway(CommonBee.this.savedFlowerPos)) {
                  CommonBee.this.savedFlowerPos = null;
               } else {
                  CommonBee.this.pathfindRandomlyTowards(CommonBee.this.savedFlowerPos);
               }
            }
         }
      }

      private boolean wantsToGoToKnownFlower() {
         return CommonBee.this.ticksWithoutNectarSinceExitingHive > 2400;
      }
   }

   class BeeGrowCropGoal extends BaseBeeGoal {
      static final int GROW_CHANCE = 30;

      public boolean canBeeUse() {
         if (CommonBee.this.getCropsGrownSincePollination() >= 10) {
            return false;
         } else if (CommonBee.this.random.nextFloat() < 0.3F) {
            return false;
         } else {
            return CommonBee.this.hasNectar() && CommonBee.this.isHiveValid();
         }
      }

      public boolean canBeeContinueToUse() {
         return this.canBeeUse();
      }

      public void tick() {
         if (CommonBee.this.random.nextInt(this.adjustedTickDelay(30)) == 0) {
            for(int i = 1; i <= 2; ++i) {
               BlockPos blockpos = CommonBee.this.blockPosition().below(i);
               BlockState blockstate = CommonBee.this.level.getBlockState(blockpos);
               Block block = blockstate.getBlock();
               boolean flag = false;
               IntegerProperty integerproperty = null;
               if (blockstate.is(BlockTags.BEE_GROWABLES)) {
                  if (block instanceof CropBlock) {
                     CropBlock cropblock = (CropBlock)block;
                     if (!cropblock.isMaxAge(blockstate)) {
                        flag = true;
                        integerproperty = cropblock.getAgeProperty();
                     }
                  } else if (block instanceof StemBlock) {
                     int k = blockstate.getValue(StemBlock.AGE);
                     if (k < 7) {
                        flag = true;
                        integerproperty = StemBlock.AGE;
                     }
                  } else if (blockstate.is(Blocks.SWEET_BERRY_BUSH)) {
                     int j = blockstate.getValue(SweetBerryBushBlock.AGE);
                     if (j < 3) {
                        flag = true;
                        integerproperty = SweetBerryBushBlock.AGE;
                     }
                  } else if (blockstate.is(Blocks.CAVE_VINES) || blockstate.is(Blocks.CAVE_VINES_PLANT)) {
                     ((BonemealableBlock)blockstate.getBlock()).performBonemeal((ServerLevel)CommonBee.this.level, CommonBee.this.random, blockpos, blockstate);
                  }

                  if (flag) {
                     CommonBee.this.level.levelEvent(2005, blockpos, 0);
                     CommonBee.this.level.setBlockAndUpdate(blockpos, blockstate.setValue(integerproperty, Integer.valueOf(blockstate.getValue(integerproperty) + 1)));
                     CommonBee.this.incrementNumCropsGrownSincePollination();
                  }
               }
            }

         }
      }
   }

   class BeeHurtByOtherGoal extends HurtByTargetGoal {
      BeeHurtByOtherGoal(CommonBee p_28033_) {
         super(p_28033_);
      }

      public boolean canContinueToUse() {
         return CommonBee.this.isAngry() && super.canContinueToUse();
      }

      protected void alertOther(Mob p_28035_, LivingEntity p_28036_) {
         if (p_28035_ instanceof CommonBee && this.mob.hasLineOfSight(p_28036_)) {
            p_28035_.setTarget(p_28036_);
         }

      }
   }

   class BeeLocateHiveGoal extends BaseBeeGoal {
      public boolean canBeeUse() {
         return CommonBee.this.remainingCooldownBeforeLocatingNewHive == 0 && !CommonBee.this.hasHive() && CommonBee.this.wantsToEnterHive();
      }

      public boolean canBeeContinueToUse() {
         return false;
      }

      public void start() {
         CommonBee.this.remainingCooldownBeforeLocatingNewHive = 200;
         List<BlockPos> list = this.findNearbyHivesWithSpace();
         System.out.println("Searching a hive");
         if (!list.isEmpty()) {
            for(BlockPos blockpos : list) {
               if (!CommonBee.this.goToHiveGoal.isTargetBlacklisted(blockpos)) {
                  CommonBee.this.hivePos = blockpos;
                  return;
               }
            }

            CommonBee.this.goToHiveGoal.clearBlacklist();
            CommonBee.this.hivePos = list.get(0);
         }
         else
            System.out.println("None found");
      }

      private List<BlockPos> findNearbyHivesWithSpace() {
         BlockPos blockpos = CommonBee.this.blockPosition();
         List<BlockPos> list = new ArrayList<>();
         int   range = 8;
         int   x = blockpos.getX() - range, y = blockpos.getY() - range, z = blockpos.getZ() - range;
         for (int checkX = x; checkX <= x + (range * 2) + 1; checkX++){
            for (int checkY = y; checkY <= y + (range * 2) + 1; checkY++){
               for (int checkZ = z; checkZ <= z + (range * 2) + 1; checkZ++){
                  if (doesHiveHaveSpace(new BlockPos(checkX, checkY, checkZ)))
                     list.add(new BlockPos(checkX, checkY, checkZ));
               }
            }
         }
         return list;
      }
   }

   class BeeLookControl extends LookControl {
      BeeLookControl(Mob p_28059_) {
         super(p_28059_);
      }

      public void tick() {
         if (!CommonBee.this.isAngry()) {
            super.tick();
         }
      }

      protected boolean resetXRotOnTick() {
         return !CommonBee.this.beePollinateGoal.isPollinating();
      }
   }

   class BeePollinateGoal extends BaseBeeGoal {
      private static final int MIN_POLLINATION_TICKS = 400;
      private static final int MIN_FIND_FLOWER_RETRY_COOLDOWN = 20;
      private static final int MAX_FIND_FLOWER_RETRY_COOLDOWN = 60;
      private final Predicate<BlockState> VALID_POLLINATION_BLOCKS = (p_28074_) -> {
         if (p_28074_.hasProperty(BlockStateProperties.WATERLOGGED) && p_28074_.getValue(BlockStateProperties.WATERLOGGED)) {
            return false;
         } else if (p_28074_.is(BlockTags.FLOWERS)) {
            if (p_28074_.is(Blocks.SUNFLOWER)) {
               return p_28074_.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
            } else {
               return true;
            }
         } else {
            return false;
         }
      };
      private static final double ARRIVAL_THRESHOLD = 0.1D;
      private static final int POSITION_CHANGE_CHANCE = 25;
      private static final float SPEED_MODIFIER = 0.35F;
      private static final float HOVER_HEIGHT_WITHIN_FLOWER = 0.6F;
      private static final float HOVER_POS_OFFSET = 0.33333334F;
      private int successfulPollinatingTicks;
      private int lastSoundPlayedTick;
      private boolean pollinating;
      @Nullable
      private Vec3 hoverPos;
      private int pollinatingTicks;
      private static final int MAX_POLLINATING_TICKS = 600;

      BeePollinateGoal() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canBeeUse() {
         if (CommonBee.this.remainingCooldownBeforeLocatingNewFlower > 0) {
            return false;
         } else if (CommonBee.this.hasNectar()) {
            return false;
         } else if (CommonBee.this.level.isRaining()) {
            return false;
         } else {
            Optional<BlockPos> optional = this.findNearbyFlower();
            if (optional.isPresent()) {
               CommonBee.this.savedFlowerPos = optional.get();
               CommonBee.this.navigation.moveTo((double)CommonBee.this.savedFlowerPos.getX() + 0.5D, (double)CommonBee.this.savedFlowerPos.getY() + 0.5D, (double)CommonBee.this.savedFlowerPos.getZ() + 0.5D, (double)1.2F);
               return true;
            } else {
               CommonBee.this.remainingCooldownBeforeLocatingNewFlower = Mth.nextInt(CommonBee.this.random, 20, 60);
               return false;
            }
         }
      }

      public boolean canBeeContinueToUse() {
         if (!this.pollinating) {
            return false;
         } else if (!CommonBee.this.hasSavedFlowerPos()) {
            return false;
         } else if (CommonBee.this.level.isRaining()) {
            return false;
         } else if (this.hasPollinatedLongEnough()) {
            return CommonBee.this.random.nextFloat() < 0.2F;
         } else if (CommonBee.this.tickCount % 20 == 0 && !CommonBee.this.isFlowerValid(CommonBee.this.savedFlowerPos)) {
            CommonBee.this.savedFlowerPos = null;
            return false;
         } else {
            return true;
         }
      }

      private boolean hasPollinatedLongEnough() {
         return this.successfulPollinatingTicks > 400;
      }

      boolean isPollinating() {
         return this.pollinating;
      }

      void stopPollinating() {
         this.pollinating = false;
      }

      public void start() {
         this.successfulPollinatingTicks = 0;
         this.pollinatingTicks = 0;
         this.lastSoundPlayedTick = 0;
         this.pollinating = true;
         CommonBee.this.resetTicksWithoutNectarSinceExitingHive();
      }

      public void stop() {
         if (this.hasPollinatedLongEnough()) {
            CommonBee.this.setHasNectar(true);
         }

         this.pollinating = false;
         CommonBee.this.navigation.stop();
         CommonBee.this.remainingCooldownBeforeLocatingNewFlower = 200;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         ++this.pollinatingTicks;
         if (this.pollinatingTicks > 600) {
            CommonBee.this.savedFlowerPos = null;
         } else {
            Vec3 vec3 = Vec3.atBottomCenterOf(CommonBee.this.savedFlowerPos).add(0.0D, (double)0.6F, 0.0D);
            if (vec3.distanceTo(CommonBee.this.position()) > 1.0D) {
               this.hoverPos = vec3;
               this.setWantedPos();
            } else {
               if (this.hoverPos == null) {
                  this.hoverPos = vec3;
               }

               boolean flag = CommonBee.this.position().distanceTo(this.hoverPos) <= 0.1D;
               boolean flag1 = true;
               if (!flag && this.pollinatingTicks > 600) {
                  CommonBee.this.savedFlowerPos = null;
               } else {
                  if (flag) {
                     boolean flag2 = CommonBee.this.random.nextInt(25) == 0;
                     if (flag2) {
                        this.hoverPos = new Vec3(vec3.x() + (double)this.getOffset(), vec3.y(), vec3.z() + (double)this.getOffset());
                        CommonBee.this.navigation.stop();
                     } else {
                        flag1 = false;
                     }

                     CommonBee.this.getLookControl().setLookAt(vec3.x(), vec3.y(), vec3.z());
                  }

                  if (flag1) {
                     this.setWantedPos();
                  }

                  ++this.successfulPollinatingTicks;
                  if (CommonBee.this.random.nextFloat() < 0.05F && this.successfulPollinatingTicks > this.lastSoundPlayedTick + 60) {
                     this.lastSoundPlayedTick = this.successfulPollinatingTicks;
                     CommonBee.this.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
                  }

               }
            }
         }
      }

      private void setWantedPos() {
         CommonBee.this.getMoveControl().setWantedPosition(this.hoverPos.x(), this.hoverPos.y(), this.hoverPos.z(), (double)0.35F);
      }

      private float getOffset() {
         return (CommonBee.this.random.nextFloat() * 2.0F - 1.0F) * 0.33333334F;
      }

      private Optional<BlockPos> findNearbyFlower() {
         return this.findNearestBlock(this.VALID_POLLINATION_BLOCKS, 5.0D);
      }

      private Optional<BlockPos> findNearestBlock(Predicate<BlockState> p_28076_, double p_28077_) {
         BlockPos blockpos = CommonBee.this.blockPosition();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int i = 0; (double)i <= p_28077_; i = i > 0 ? -i : 1 - i) {
            for(int j = 0; (double)j < p_28077_; ++j) {
               for(int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                  for(int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                     blockpos$mutableblockpos.setWithOffset(blockpos, k, i - 1, l);
                     if (blockpos.closerThan(blockpos$mutableblockpos, p_28077_) && p_28076_.test(CommonBee.this.level.getBlockState(blockpos$mutableblockpos))) {
                        return Optional.of(blockpos$mutableblockpos);
                     }
                  }
               }
            }
         }

         return Optional.empty();
      }
   }

   class BeeWanderGoal extends Goal {
      private static final int WANDER_THRESHOLD = 22;

      BeeWanderGoal() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return CommonBee.this.navigation.isDone() && CommonBee.this.random.nextInt(10) == 0;
      }

      public boolean canContinueToUse() {
         return CommonBee.this.navigation.isInProgress();
      }

      public void start() {
         Vec3 vec3 = this.findPos();
         if (vec3 != null) {
            CommonBee.this.navigation.moveTo(CommonBee.this.navigation.createPath(BlockPos.containing(vec3), 1), 1.0D);
         }

      }

      @Nullable
      private Vec3 findPos() {
         Vec3 vec3;
         if (CommonBee.this.isHiveValid() && !CommonBee.this.closerThan(CommonBee.this.hivePos, 22)) {
            Vec3 vec31 = Vec3.atCenterOf(CommonBee.this.hivePos);
            vec3 = vec31.subtract(CommonBee.this.position()).normalize();
         } else {
            vec3 = CommonBee.this.getViewVector(0.0F);
         }

         int i = 8;
         Vec3 vec32 = HoverRandomPos.getPos(CommonBee.this, 8, 7, vec3.x, vec3.z, ((float)Math.PI / 2F), 3, 1);
         return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(CommonBee.this, 8, 4, -2, vec3.x, vec3.z, (double)((float)Math.PI / 2F));
      }
   }
}
