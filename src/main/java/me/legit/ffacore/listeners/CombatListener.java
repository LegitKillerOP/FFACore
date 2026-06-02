package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatListener implements Listener {

    private final FFACore plugin;
    public CombatListener(FFACore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        Player victim = (Player)e.getEntity();
        Player attacker = (Player)e.getDamager();
        plugin.getCombatManager().tag(victim.getUniqueId());
        plugin.getCombatManager().tag(attacker.getUniqueId());

        attacker.sendMessage(plugin.getPrefix() + ChatColor.RED + "Combat tagged for 15s");
    }
}