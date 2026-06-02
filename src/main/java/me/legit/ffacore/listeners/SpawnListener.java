package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpawnListener implements Listener {

    private final FFACore plugin;

    public SpawnListener(FFACore plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(plugin.getConfigManager().getSpawn().get().getBoolean("settings.teleport-on-join")){
            plugin.getSpawnManager().teleport(e.getPlayer());
        }
    }
}