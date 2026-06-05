package me.legit.ffacore.arena;

import me.legit.ffacore.FFACore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager {

    private final FFACore plugin;
    private final Map<String, Arena> arenas = new HashMap<>();

    private String activeArena;

    public ArenaManager(FFACore plugin) {
        this.plugin = plugin;
        loadArenas();
    }

    // ------------------------
    // CRUD
    // ------------------------

    public void createArena(String name) {
        String key = name.toLowerCase();

        Arena arena = new Arena(name);
        arenas.put(key, arena);

        saveArena(arena);
    }

    public void deleteArena(String name) {
        String key = name.toLowerCase();

        arenas.remove(key);

        plugin.getConfigManager().getArenas().get().set("arenas." + key, null);
        plugin.getConfigManager().getArenas().save();

        if (activeArena != null && activeArena.equals(key)) {
            activeArena = null;
        }
    }

    public Arena getArena(String name) {
        if (name == null) return null;
        return arenas.get(name.toLowerCase());
    }

    public Map<String, Arena> getArenas() {
        return arenas;
    }

    // ------------------------
    // ACTIVE ARENA
    // ------------------------

    public void setActiveArena(String name) {
        if (name == null) {
            activeArena = null;
            return;
        }
        if (!arenas.containsKey(name.toLowerCase())) return;
        activeArena = name.toLowerCase();
        plugin.getConfigManager().getArenas().get().set("active", activeArena);
        plugin.getConfigManager().getArenas().save();
    }

    public Arena getActiveArena() {
        return activeArena == null ? null : arenas.get(activeArena);
    }

    // ------------------------
    // SAFE SPAWN
    // ------------------------

    public Location getRandomSafeLocation(Arena arena) {
        if (arena == null || arena.getMin() == null || arena.getMax() == null) return null;
        World world = arena.getMin().getWorld();
        int minX = Math.min(arena.getMin().getBlockX(), arena.getMax().getBlockX());
        int maxX = Math.max(arena.getMin().getBlockX(), arena.getMax().getBlockX());
        int minZ = Math.min(arena.getMin().getBlockZ(), arena.getMax().getBlockZ());
        int maxZ = Math.max(arena.getMin().getBlockZ(), arena.getMax().getBlockZ());

        for (int i = 0; i < 300; i++) {
            int x = ThreadLocalRandom.current().nextInt(minX, maxX + 1);
            int z = ThreadLocalRandom.current().nextInt(minZ, maxZ + 1);
            int y = world.getHighestBlockYAt(x, z);

            Material ground = world.getBlockAt(x, y - 1, z).getType();
            Material feet = world.getBlockAt(x, y, z).getType();
            Material head = world.getBlockAt(x, y + 1, z).getType();

            if (!world.getBlockAt(x, y - 1, z).getType().isSolid()) continue;
            if (isUnsafe(ground)) continue;
            if (feet != Material.AIR) continue;
            if (head != Material.AIR) continue;
            return new Location(world, x + 0.5, y, z + 0.5);
        }
        return null;
    }

    private boolean isUnsafe(Material m) {
        switch (m) {
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
            case FIRE:
            case CACTUS:
            case WEB:
                return true;
            default:
                return false;
        }
    }

    // ------------------------
    // TELEPORT
    // ------------------------

    public boolean sendToArena(Player player) {
        Arena arena = getActiveArena();
        if (arena == null) {
            player.sendMessage(plugin.getPrefix() + "§cNo active arena set!");
            return false;
        }
        Location loc = getRandomSafeLocation(arena);
        if (loc == null) {
            player.sendMessage(plugin.getPrefix() + "§cArena region not configured!");
            return false;
        }
        player.teleport(loc);
        return true;
    }

    // ------------------------
    // SAVE / LOAD SYSTEM (FIX)
    // ------------------------

    public void saveArena(Arena arena) {
        FileConfiguration cfg = plugin.getConfigManager().getArenas().get();
        String path = "arenas." + arena.getName().toLowerCase();
        cfg.set(path + ".name", arena.getName());
        if (arena.getMin() != null) {
            saveLocation(cfg, path + ".min", arena.getMin());
        }
        if (arena.getMax() != null) {
            saveLocation(cfg, path + ".max", arena.getMax());
        }
        cfg.set("active", activeArena);
        plugin.getConfigManager().getArenas().save();
    }

    public void loadArenas() {
        FileConfiguration cfg = plugin.getConfigManager().getArenas().get();
        if (cfg.getConfigurationSection("arenas") == null) return;
        for (String key : cfg.getConfigurationSection("arenas").getKeys(false)) {
            String base = "arenas." + key;
            String name = cfg.getString(base + ".name", key);
            Arena arena = new Arena(name);
            arena.setMin(loadLocation(cfg, base + ".min"));
            arena.setMax(loadLocation(cfg, base + ".max"));
            arenas.put(key.toLowerCase(), arena);
        }
        activeArena = cfg.getString("active");
    }

    private void saveLocation(FileConfiguration cfg, String path, Location loc) {
        cfg.set(path + ".world", loc.getWorld().getName());
        cfg.set(path + ".x", loc.getX());
        cfg.set(path + ".y", loc.getY());
        cfg.set(path + ".z", loc.getZ());
        cfg.set(path + ".yaw", loc.getYaw());
        cfg.set(path + ".pitch", loc.getPitch());
    }

    private Location loadLocation(FileConfiguration cfg, String path) {
        if (!cfg.contains(path + ".world")) return null;
        World world = Bukkit.getWorld(cfg.getString(path + ".world"));
        if (world == null) return null;
        double x = cfg.getDouble(path + ".x");
        double y = cfg.getDouble(path + ".y");
        double z = cfg.getDouble(path + ".z");
        float yaw = (float) cfg.getDouble(path + ".yaw");
        float pitch = (float) cfg.getDouble(path + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }
}