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

    private final String mode;
    private final String arenaWorld;

    public ServerProtectionListener(FFACore plugin) {
        this.plugin = plugin;

        this.mode = plugin.getConfig().getString("server-mode.type", "TWO_WORLD");
        this.arenaWorld = plugin.getConfig().getString("worlds.arena", "world");

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

    private boolean isArena(Location loc) {

        if (loc == null || loc.getWorld() == null) return false;

        String world = loc.getWorld().getName();

        if (mode.equalsIgnoreCase("ONE_WORLD")) {
            return true;
        }

        if (mode.equalsIgnoreCase("TWO_WORLD")) {
            return world.equalsIgnoreCase(arenaWorld);
        }

        if (mode.equalsIgnoreCase("REGION")) {
            return isInRegion(loc);
        }

        return false;
    }

    private boolean isInRegion(Location loc) {

        String worldName = plugin.getConfig().getString("region.world");
        if (worldName == null) return false;

        if (!loc.getWorld().getName().equalsIgnoreCase(worldName)) return false;

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return x >= plugin.getConfig().getInt("region.min.x")
                && x <= plugin.getConfig().getInt("region.max.x")
                && y >= plugin.getConfig().getInt("region.min.y")
                && y <= plugin.getConfig().getInt("region.max.y")
                && z >= plugin.getConfig().getInt("region.min.z")
                && z <= plugin.getConfig().getInt("region.max.z");
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