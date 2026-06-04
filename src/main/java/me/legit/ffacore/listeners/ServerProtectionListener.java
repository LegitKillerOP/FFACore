package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.weather.*;
import org.bukkit.event.player.*;

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

    private boolean isArenaContext(Location loc) {
        String mode = plugin.getConfig().getString("server-mode.type", "TWO_WORLD");

        switch (mode.toUpperCase()) {

            case "ONE_WORLD":
                return true;

            case "TWO_WORLD":
                String arenaWorld = plugin.getConfig().getString("worlds.arena", "world");
                return loc.getWorld().getName().equalsIgnoreCase(arenaWorld);

            case "REGION":
                return isInRegion(loc);

            default:
                return false;
        }
    }

    private boolean isInRegion(Location loc) {

        String worldName = plugin.getConfig().getString("region.world");

        if (worldName == null || loc == null || loc.getWorld() == null) {
            return false;
        }

        if (!loc.getWorld().getName().equalsIgnoreCase(worldName)) {
            return false;
        }

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        int minX = plugin.getConfig().getInt("region.min.x");
        int minY = plugin.getConfig().getInt("region.min.y");
        int minZ = plugin.getConfig().getInt("region.min.z");

        int maxX = plugin.getConfig().getInt("region.max.x");
        int maxY = plugin.getConfig().getInt("region.max.y");
        int maxZ = plugin.getConfig().getInt("region.max.z");

        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player p = (Player) e.getEntity();
        if (!isArenaContext(p.getLocation())) return;

        e.setCancelled(true);
        p.setFoodLevel(20);
        p.setSaturation(20f);
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player p = (Player) e.getEntity();
        if (!isArenaContext(p.getLocation())) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!isArenaContext(e.getPlayer().getLocation())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if (!isArenaContext(e.getPlayer().getLocation())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!isArenaContext(e.getBlock().getLocation())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!isArenaContext(e.getBlock().getLocation())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPhysics(BlockPhysicsEvent e) {
        if (!isArenaContext(e.getBlock().getLocation())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onSpread(BlockSpreadEvent e) {
        if (!isArenaContext(e.getBlock().getLocation())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onLeaves(LeavesDecayEvent e) {
        if (!isArenaContext(e.getBlock().getLocation())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBurn(BlockBurnEvent e) {
        if (!isArenaContext(e.getBlock().getLocation())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        if (!isArenaContext(e.getWorld().getSpawnLocation())) return;

        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onThunder(ThunderChangeEvent e) {
        if (!isArenaContext(e.getWorld().getSpawnLocation())) return;

        if (e.toThunderState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (!isArenaContext(e.getLocation())) return;

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL
                || e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent e) {
        if (!isArenaContext(e.getLocation())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {

        if (!(e.getEntity() instanceof Player)) return;

        Player p = (Player) e.getEntity();
        if (!isArenaContext(p.getLocation())) return;

        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!isArenaContext(e.getPlayer().getLocation())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent e) {
        if (!isArenaContext(e.getPlayer().getLocation())) return;
        e.setCancelled(true);
    }
}