package me.legit.ffacore.spawn;

import me.legit.ffacore.FFACore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpawnManager {

    private final FFACore plugin;

    public SpawnManager(FFACore plugin) {
        this.plugin = plugin;
    }

    public void setSpawn(Location location){
        FileConfiguration config = plugin.getConfigManager().getSpawn().get();
        config.set("spawn.world", location.getWorld().getName());
        config.set("spawn.x", location.getX());
        config.set("spawn.y", location.getY());
        config.set("spawn.z", location.getZ());
        config.set("spawn.yaw", location.getYaw());
        config.set("spawn.pitch", location.getPitch());
        plugin.getConfigManager().getSpawn().save();
    }

    public Location getSpawn(){
        FileConfiguration config = plugin.getConfigManager().getSpawn().get();

        World world = Bukkit.getWorld(config.getString("spawn.world"));
        return new Location(world, config.getDouble("spawn.x"), config.getDouble("spawn.y"), config.getDouble("spawn.z"),(float) config.getDouble("spawn.yaw"), (float) config.getDouble("spawn.pitch"));
    }

    public void teleport(Player player){
        player.teleport(getSpawn());
    }

}