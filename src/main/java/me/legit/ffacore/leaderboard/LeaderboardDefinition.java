package me.legit.ffacore.leaderboard;

import me.legit.ffacore.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LeaderboardDefinition {

    private final String id;
    private final LeaderboardType type;
    private Location location;
    private final Map<UUID, Hologram> holograms = new HashMap<>();

    public LeaderboardDefinition(String id, LeaderboardType type, Location location) {
        this.id = id;
        this.type = type;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public LeaderboardType getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setHologram(UUID uuid, Hologram hologram) {
        holograms.put(uuid, hologram);
    }

    public void destroy(Player player) {
        Hologram hologram = holograms.remove(player.getUniqueId());

        if (hologram != null) {
            hologram.destroy(player);
        }
    }

    public void destroyAll() {
        for (UUID uuid : holograms.keySet()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                holograms.get(uuid).destroy(player);
            }
        }

        holograms.clear();
    }

    public void removePlayer(Player player) {
        Hologram hologram = holograms.remove(player.getUniqueId());

        if (hologram != null) {
            hologram.destroy(player);
        }
    }

}
