package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
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

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Player)) return;
        Player victim = (Player) e.getEntity();
        Player attacker = (Player) e.getDamager();
        boolean victimInArena = plugin.getServerProtectionListener().isArena(victim.getLocation());
        boolean attackerInArena = plugin.getServerProtectionListener().isArena(attacker.getLocation());

        if (!victimInArena || !attackerInArena) {
            e.setCancelled(true);
            if (!attackerInArena) {
                attacker.sendMessage("§cYou must be inside the arena to attack!");
            } else {
                attacker.sendMessage("§cYou can only hit players inside the arena!");
            }
            attacker.playSound(attacker.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
            return;
        }

        if (victim.equals(attacker)) return;

        plugin.getCombatManager().tag(victim.getUniqueId());
        plugin.getCombatManager().tag(attacker.getUniqueId());

        plugin.getCombatManager().setLastHit(victim.getUniqueId(), attacker.getUniqueId());
        plugin.getCombatManager().addDamage(victim.getUniqueId(), attacker.getUniqueId(), e.getFinalDamage());

        playTagSound(victim);
        playTagSound(attacker);
        sendTagMessage(victim);
        sendTagMessage(attacker);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = victim.getKiller();
        UUID victimId = victim.getUniqueId();
        UUID realKiller = null;

        if (killer != null) {
            realKiller = killer.getUniqueId();
        } else {
            UUID last = plugin.getCombatManager().getLastHit(victimId);
            if (last != null) realKiller = last;
        }

        if (realKiller != null) {
            Player k = Bukkit.getPlayer(realKiller);
            if (k != null) {
                plugin.getPlayerDataManager().get(realKiller).addKill();
                plugin.getPlayerDataManager().get(realKiller).addCoins(5);
                plugin.getPlayerDataManager().get(realKiller).addXp(10);

                k.sendMessage("§a+1 Kill §7(§e+5 coins§7)");
                k.playSound(k.getLocation(), Sound.LEVEL_UP, 1f, 1.2f);
            }
        }

        UUID assister = plugin.getCombatManager().getTopAssister(victimId, realKiller);
        if (assister != null) {
            Player a = Bukkit.getPlayer(assister);
            if (a != null) {
                plugin.getPlayerDataManager().get(assister).addCoins(2);
                plugin.getPlayerDataManager().get(assister).addXp(5);

                a.sendMessage("§e+Assist §7(§6+2 coins§7)");
                a.playSound(a.getLocation(), Sound.ORB_PICKUP, 1f, 1.5f);
            }
        }
        plugin.getCombatManager().remove(victimId);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getCombatManager().isTagged(p.getUniqueId())) return;
        String msg = e.getMessage().toLowerCase();

        if (msg.startsWith("/spawn") ||
                msg.startsWith("/hub") ||
                msg.startsWith("/home") ||
                msg.startsWith("/kit")) {

            e.setCancelled(true);
            p.sendMessage("§cYou cannot use commands in combat!");
            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getCombatManager().isTagged(p.getUniqueId())) return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (p.isOnline()) {
                p.setHealth(0.0);
            }
        });
        Bukkit.broadcastMessage("§c" + p.getName() + " logged out in combat!");
    }

    private void sendTagMessage(Player p) {
        long now = System.currentTimeMillis();
        long last = messageCooldown.getOrDefault(p.getUniqueId(), 0L);

        if (now - last < 5000) return;

        messageCooldown.put(p.getUniqueId(), now);

        p.sendMessage("§cCombat Tagged §7(15s)");
    }

    private void playTagSound(Player p) {
        p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1f, 1.2f);
    }
}