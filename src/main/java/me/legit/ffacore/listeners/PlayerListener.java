package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.kits.Kit;
import org.bukkit.*;
import org.bukkit.entity.Player;
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

        Player player = e.getPlayer();
        e.setJoinMessage(null);
        plugin.getPlayerDataManager().load(player.getUniqueId());
        resetPlayer(player);

        Kit defaultKit = plugin.getKitManager().getKit("default");
        if (defaultKit != null) {
            plugin.getKitManager().applyKit(player, defaultKit);
        }
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1.2f);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getScoreboardManager().update(player);
            plugin.getTabManager().initPlayer(player);
            plugin.getLeaderboardManager().showAllTo(player);
            player.sendMessage("§aWelcome to §lFFA Server§a!");
        }, 10L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Player player = e.getPlayer();

        e.setQuitMessage(null);

        plugin.getLeaderboardManager().removePlayer(player);
        plugin.getPlayerDataManager().unload(player.getUniqueId());
        plugin.getTabManager().removePlayer(player);

        player.getActivePotionEffects().forEach(effect ->
                player.removePotionEffect(effect.getType())
        );
    }

    private void resetPlayer(Player p) {

        p.getInventory().clear();
        p.getInventory().setArmorContents(null);

        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.setSaturation(20f);
        p.setFireTicks(0);
        p.setExp(0);
        p.setLevel(0);
        p.setWalkSpeed(0.2f);
        p.setFlySpeed(0.1f);

        p.getActivePotionEffects().forEach(effect ->
                p.removePotionEffect(effect.getType())
        );

        p.setNoDamageTicks(20);
    }
}