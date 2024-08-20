package net.sirezran.optispawn;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

@Mod(OptiSpawn.MODID)
public class OptiSpawn {
    public static final String MODID = "optispawn";

    public static final int DESPAWN_RADIUS = 32;
    public static final int MOBS_PER_PLAYER_RADIUS = 32;
    public static final int MAX_MOBS_PER_PLAYER = 10;

    public OptiSpawn() {
        MinecraftForge.EVENT_BUS.register(this);
    }


    public AABB setSearchArea(Player player, double radius) {
        return new AABB (
                player.getX() - radius, player.getY() - radius, player.getZ() - radius,
                player.getX() + radius, player.getY() + radius, player.getZ() + radius
        );
    }


    public float getMobCountAroundPlayer (Level level, Player player, double radius) {
        AABB searchArea = new AABB(
          player.getX() - radius, player.getY() - radius, player.getZ() - radius,
          player.getX() + radius, player.getY() + radius, player.getZ() + radius
        );

        int entityCount = 0;
        for (Entity entity : level.getEntities(null,searchArea)) {
            if (entity instanceof Mob) {
                entityCount++;
            }
        }
        return entityCount;
    }


    @SubscribeEvent
    public void despawn(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player) {
            Level world = player.getCommandSenderWorld();
            world.getEntitiesOfClass(Mob.class, setSearchArea(player, MOBS_PER_PLAYER_RADIUS)).stream().filter(mob -> mob.distanceToSqr(player.position()) > DESPAWN_RADIUS * DESPAWN_RADIUS)
                    .forEach(Mob::discard);
        }
    }


    // Prevents mob spawns far away from the player, or if there are already the max within range
    @SubscribeEvent
    public void preventSpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Mob) {
            Level level = event.getLevel();
            Player nearestPlayer = level.getNearestPlayer(entity, DESPAWN_RADIUS);

            if (nearestPlayer == null) {
                event.setCanceled(true);
            } else if (getMobCountAroundPlayer(level, nearestPlayer, MOBS_PER_PLAYER_RADIUS) >= MAX_MOBS_PER_PLAYER) {
                event.setCanceled(true);
            }
        }
    }
}