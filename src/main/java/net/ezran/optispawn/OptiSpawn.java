package net.ezran.optispawn;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

@Mod("optispawn")
public class OptiSpawn {

    public static final int MIN_SPAWN_DISTANCE = 5; // Minimum distance from player
    public static final int MAX_SPAWN_DISTANCE = 32; // Maximum distance from player
    public static final int DESPAWN_RADIUS = 32; // Distance from player to despawn
    public static final int MAX_MOBS_PER_PLAYER = 5; // Maximum number of mobs around each player

    public OptiSpawn() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Common setup code here
    }

    @SubscribeEvent
    public void onCheckSpawn(MobSpawnEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Mob) {
            if (!isWithinSpawnRange((Mob) entity) || hasTooManyMobsAround((Mob) entity)) {
                event.setResult(MobSpawnEvent.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player) {
            var world = player.getCommandSenderWorld();
            var playerBox = player.getBoundingBox().inflate(DESPAWN_RADIUS);

            // Get all mobs within the expanded bounding box and check if they are outside the player's despawn radius
            world.getEntitiesOfClass(Mob.class, playerBox).stream()
                    .filter(mob -> mob.distanceToSqr(player.position()) > DESPAWN_RADIUS * DESPAWN_RADIUS)
                    .forEach(Mob::discard); // Discard mobs outside the despawn radius

            // Check if there are too many mobs around the player and remove the excess
            List<Mob> mobsAroundPlayer = world.getEntitiesOfClass(Mob.class, playerBox);
            if (mobsAroundPlayer.size() > MAX_MOBS_PER_PLAYER) {
                mobsAroundPlayer.stream()
                        .sorted((mob1, mob2) -> Double.compare(mob1.distanceToSqr(player.position()), mob2.distanceToSqr(player.position())))
                        .skip(MAX_MOBS_PER_PLAYER)
                        .forEach(Mob::discard);
            }
        }
    }

    private boolean isWithinSpawnRange(Mob mob) {
        var world = mob.getCommandSenderWorld();
        var nearestPlayer = world.getNearestPlayer(mob, MAX_SPAWN_DISTANCE);
        if (nearestPlayer == null) {
            return false;
        }
        double distanceToPlayer = nearestPlayer.distanceToSqr(mob.position());
        return distanceToPlayer >= MIN_SPAWN_DISTANCE * MIN_SPAWN_DISTANCE && distanceToPlayer <= MAX_SPAWN_DISTANCE * MAX_SPAWN_DISTANCE;
    }

    private boolean hasTooManyMobsAround(Mob mob) {
        var world = mob.getCommandSenderWorld();
        var nearestPlayer = world.getNearestPlayer(mob, MAX_SPAWN_DISTANCE);
        if (nearestPlayer == null) {
            return false;
        }
        var mobsAroundPlayer = world.getEntitiesOfClass(Mob.class, nearestPlayer.getBoundingBox().inflate(DESPAWN_RADIUS));
        return mobsAroundPlayer.size() >= MAX_MOBS_PER_PLAYER;
    }
}