package net.sirezran.onetale.optispawn;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PreventSpawnHandler {

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Mob) {

            Level level = event.getLevel();
            Player nearestPlayer = level.getNearestPlayer(entity, OptiSpawn.DESPAWN_RADIUS);
            if (nearestPlayer == null) {
                event.setCanceled(true);

            } else {
                AABB searchArea = OptiSpawn.setPlayerArea(nearestPlayer);
                int localMobCount = OptiSpawn.getAreaMobCount(level, searchArea);
                if (localMobCount >= OptiSpawn.LOCAL_MOB_CAP) {
                    event.setCanceled(true);
                }
            }
        }
    }
}