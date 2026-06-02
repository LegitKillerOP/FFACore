package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    private final FFACore plugin;
    public DeathListener(FFACore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = victim.getKiller();
        PlayerData victimData = plugin.getPlayerDataManager().get(victim.getUniqueId());
        victimData.addDeath();
        if (killer != null) {
            PlayerData killerData = plugin.getPlayerDataManager().get(killer.getUniqueId());
            killerData.addKill();
            killer.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "+1 Kill");
            victim.sendMessage(plugin.getPrefix() + ChatColor.RED + "Killed by " + killer.getName());

            if (killerData.getKillStreak() == 5) {
                plugin.getServer().broadcastMessage("§6" + killer.getName() + " is on a 5 kill streak!");
            }
        }
        plugin.getCombatManager().remove(victim.getUniqueId());
    }
}