package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class CombatListener implements Listener {

    private final FFACore plugin;
    private final HashMap<UUID, Long> messageCooldown = new HashMap<>();

    public CombatListener(FFACore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Player)) return;
        Player victim = (Player) e.getEntity();
        Player attacker = (Player) e.getDamager();
        if (victim.equals(attacker)) return;
        long now = System.currentTimeMillis();
        plugin.getCombatManager().tag(victim.getUniqueId());
        plugin.getCombatManager().tag(attacker.getUniqueId());

        playTagSound(victim);
        playTagSound(attacker);

        sendTagMessage(attacker, now);
        sendTagMessage(victim, now);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getCombatManager().isTagged(p.getUniqueId())) return;
        String msg = e.getMessage().toLowerCase();

        if (msg.startsWith("/spawn") || msg.startsWith("/hub") || msg.startsWith("/home")) {
            e.setCancelled(true);
            p.sendMessage(plugin.getPrefix() + ChatColor.RED + "You cannot use this command while in combat!");
            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getCombatManager().isTagged(p.getUniqueId())) return;
        // punish player
        p.setHealth(0.0);
        Bukkit.broadcastMessage(plugin.getPrefix() +
                ChatColor.RED + p.getName() + " logged out in combat and died!");
    }

    private void sendTagMessage(Player player, long now) {

        long last = messageCooldown.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < 5000) return;
        messageCooldown.put(player.getUniqueId(), now);
        player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You are now combat tagged for 15s!");
    }

    private void playTagSound(Player player) {
        try {
            player.playSound(player.getLocation(),
                    Sound.NOTE_PLING, 1f, 1f);
        } catch (Exception ignored) {
            // fallback safety for older builds
        }
    }
}