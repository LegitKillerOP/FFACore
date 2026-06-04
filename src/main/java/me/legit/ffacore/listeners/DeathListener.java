package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DeathListener implements Listener {

    private final FFACore plugin;

    public DeathListener(FFACore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        Player victim = e.getEntity();
        Player killer = victim.getKiller();
        PlayerData victimData = plugin.getPlayerDataManager().get(victim.getUniqueId());
        if (victimData != null) {
            victimData.addDeath();
        }
        if (killer != null) {
            PlayerData killerData = plugin.getPlayerDataManager().get(killer.getUniqueId());
            if (killerData != null) {
                killerData.addKill();
                int streak = killerData.getKillStreak();
                killer.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "+1 Kill");
                victim.sendMessage(plugin.getPrefix() + ChatColor.RED + "Killed by " + killer.getName());
                if (streak > 0 && streak % 5 == 0) {
                    Bukkit.broadcastMessage(ChatColor.GOLD + killer.getName()
                            + " is on a " + streak + " kill streak!");
                }
            }
            sendRandomKillMessage(victim, killer);
        }
        plugin.getCombatManager().remove(victim.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (victim.isOnline()) {
                    victim.spigot().respawn();
                    plugin.getSpawnManager().teleport(victim);
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    private void sendRandomKillMessage(Player victim, Player killer) {
        List<String> messages = plugin.getConfigManager().getMessages().get().getStringList("kill-messages");
        if (messages.isEmpty()) {
            Bukkit.getLogger().warning("kill-messages is EMPTY or NOT LOADED!");
            return;
        }

        String msg = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
        msg = msg.replace("%victim%", victim.getName())
                .replace("%killer%", killer.getName());

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}