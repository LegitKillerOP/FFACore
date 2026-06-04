package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final FFACore plugin;

    public PlayerListener(FFACore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getPlayerDataManager().load(e.getPlayer().getUniqueId());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            plugin.getScoreboardManager().update(e.getPlayer());
            plugin.getTabManager().initPlayer(e.getPlayer());
            plugin.getTabManager().startTabUpdater();
            plugin.getLeaderboardManager().showAllTo(e.getPlayer());
        }, 10L); // 0.5 sec delay
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getLeaderboardManager().removePlayer(e.getPlayer());
        plugin.getPlayerDataManager().unload(e.getPlayer().getUniqueId());
        plugin.getTabManager().removePlayer(e.getPlayer());
    }
}