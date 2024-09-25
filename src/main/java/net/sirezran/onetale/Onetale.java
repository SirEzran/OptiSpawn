package net.sirezran.onetale;

import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.sirezran.onetale.optispawn.DespawnHandler;
import net.sirezran.onetale.optispawn.PreventSpawnHandler;
import net.sirezran.onetale.commands.SetSafeZone;

@Mod(Onetale.MODID)
public class Onetale {
    public static final String MODID = "onetale";
    public Onetale() {
        MinecraftForge.EVENT_BUS.register(new DespawnHandler());
        MinecraftForge.EVENT_BUS.register(new PreventSpawnHandler());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Register the command
        SetSafeZone.register(event.getServer().getCommands().getDispatcher());
    }
}