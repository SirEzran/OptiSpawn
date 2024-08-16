package net.ezran.optispawn;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.world.entity.player.Player;

@Mod("optispawn")
public class OptiSpawn {

    public static final int MIN_SPAWN_DISTANCE = 5; // Minimum distance from player
    public static final int MAX_SPAWN_DISTANCE = 32; // Maximum distance from player
    public static final int DESPAWN_RADIUS = 32; // Distance from player to despawn
    public static final int MAX_MOBS_PER_PLAYER = 5; // Maximum mobs around each player

    public OptiSpawn() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Common setup code here
    }

    @SubscribeEvent
    public void onCheckSpawn(MobSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Mob mob) {
            if (!isWithinSpawnRange(mob) || isTooManyMobsNearPlayer(mob)) {
                event.setResult(MobSpawnEvent.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Mob mob) {
            // If the mob is too far from all players, remove it
            if (isTooFarFromPlayers(mob)) {
                mob.discard();
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

    private boolean isTooFarFromPlayers(Mob mob) {
        var world = mob.getCommandSenderWorld();
        return world.getEntitiesOfClass(Player.class, mob.getBoundingBox().inflate(DESPAWN_RADIUS))
                .stream()
                .noneMatch(player -> player.distanceToSqr(mob.position()) <= DESPAWN_RADIUS * DESPAWN_RADIUS);
    }

    private boolean isTooManyMobsNearPlayer(Mob mob) {
        var world = mob.getCommandSenderWorld();
        var nearestPlayer = world.getNearestPlayer(mob, MAX_SPAWN_DISTANCE);
        if (nearestPlayer == null) {
            return false;
        }
        long nearbyMobCount = world.getEntitiesOfClass(Mob.class, mob.getBoundingBox().inflate(MAX_SPAWN_DISTANCE))
                .stream()
                .filter(m -> m.distanceToSqr(nearestPlayer) <= MAX_SPAWN_DISTANCE * MAX_SPAWN_DISTANCE)
                .count();
        return nearbyMobCount >= MAX_MOBS_PER_PLAYER;
    }
}
