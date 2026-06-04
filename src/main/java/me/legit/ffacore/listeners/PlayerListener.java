package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final FFACore plugin;

    public PlayerListener(FFACore plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        plugin.getPlayerDataManager().load(e.getPlayer().getUniqueId());
        plugin.getLeaderboardManager().showAllTo(e.getPlayer());
        plugin.getScoreboardManager().update(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        plugin.getLeaderboardManager().removePlayer(e.getPlayer());
        plugin.getPlayerDataManager().unload(e.getPlayer().getUniqueId());
    }
}