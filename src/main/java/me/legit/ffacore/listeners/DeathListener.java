package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
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
        UUID realKillerId = victim.getKiller() != null
                ? victim.getKiller().getUniqueId()
                : plugin.getCombatManager().getLastHit(victim.getUniqueId());
        Player killer = realKillerId != null ? Bukkit.getPlayer(realKillerId) : null;

        plugin.getKillManager().handleKill(victim, killer);
        sendRandomKillMessage(victim, killer);

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