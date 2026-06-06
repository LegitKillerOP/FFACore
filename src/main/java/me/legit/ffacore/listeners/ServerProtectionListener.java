package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.weather.*;

public class ServerProtectionListener implements Listener {

    private final FFACore plugin;

    public ServerProtectionListener(FFACore plugin) {
        this.plugin = plugin;
        setupWorldRules();
    }

    private void setupWorldRules() {

        for (World world : Bukkit.getWorlds()) {
            world.setStorm(false);
            world.setThundering(false);

            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("mobGriefing", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("randomTickSpeed", "0");

            world.setGameRuleValue("doDaylightCycle", "false");
            world.setTime(6000);
        }

        plugin.getLogger().info("FFA server protection initialized.");
    }

    public boolean isArena(Location loc) {
        return plugin.getArenaManager().isInArena(loc);
    }

    public boolean canPvP(Location loc) {
        return isArena(loc);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!isArena(p.getLocation())) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectile(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Player)) return;
        Player victim = (Player) e.getEntity();
        Player attacker = (Player) e.getDamager();
        boolean victimInArena = isArena(victim.getLocation());
        boolean attackerInArena = isArena(attacker.getLocation());
        if (!victimInArena || !attackerInArena) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRod(PlayerFishEvent e) {
        Player p = e.getPlayer();
        if (!isArena(p.getLocation())) {
            e.setCancelled(true);
        }
    }

    // ---------------- PLAYER PROTECTION ----------------

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player && isArena(e.getEntity().getLocation())) {
            e.setCancelled(true);
            ((Player) e.getEntity()).setFoodLevel(20);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        if (isArena(e.getEntity().getLocation())
                && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlock(BlockPlaceEvent e) {
        if (isArena(e.getPlayer().getLocation())) e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (isArena(e.getPlayer().getLocation())) e.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        if (isArena(e.getWorld().getSpawnLocation())) {
            e.setCancelled(e.toWeatherState());
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (isArena(e.getLocation())) {
            if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
                e.setCancelled(true);
            }
        }
    }
}