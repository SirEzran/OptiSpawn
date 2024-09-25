package net.sirezran.onetale.optispawn;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;


public class OptiSpawn {
    public static final int MAX_SPAWN_RADIUS = 20;
    public static final int MIN_SPAWN_RADIUS = 5;
    public static final int DESPAWN_RADIUS = 32;
    public static final int LOCAL_PLAYER_RADIUS = 32;
    public static final int LOCAL_MOB_CAP = 10;


    public static AABB setPlayerArea(Player player) {
        return new AABB (
                player.getX() - LOCAL_PLAYER_RADIUS, player.getY() - LOCAL_PLAYER_RADIUS, player.getZ() - LOCAL_PLAYER_RADIUS,
                player.getX() + LOCAL_PLAYER_RADIUS, player.getY() + LOCAL_PLAYER_RADIUS, player.getZ() + LOCAL_PLAYER_RADIUS
        );
    }

    public static int getAreaMobCount(Level level, AABB searchArea) {
        int entityCount = 0;
        for (Entity entity : level.getEntities(null, searchArea)) {
            if (entity instanceof Mob) {
                entityCount++;
            }
        }
        return entityCount;
    }
}