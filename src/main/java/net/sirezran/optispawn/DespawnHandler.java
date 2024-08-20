package net.sirezran.optispawn;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DespawnHandler {

    @SubscribeEvent
    public void onLivingEvent(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player) {
            Level world = player.getCommandSenderWorld();
            world.getEntitiesOfClass(Mob.class, OptiSpawn.setPlayerArea(player)).stream()
                    .filter(mob -> mob.distanceToSqr(player.position()) > OptiSpawn.DESPAWN_RADIUS * OptiSpawn.DESPAWN_RADIUS)
                    .forEach(Mob::discard);
        }
    }
}
