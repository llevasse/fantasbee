package com.elevasse.fantasbee.entity;

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

public class CommonBee extends Bee {
    public CommonBee(EntityType<CommonBee> type, Level level) {
        super(type, level);
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

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    public static boolean canSpawn(EntityType<CommonBee> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random){
        return Animal.checkAnimalSpawnRules(entityType, level, spawnType, pos, random) && !level.getLevelData().isRaining();
    }
}
