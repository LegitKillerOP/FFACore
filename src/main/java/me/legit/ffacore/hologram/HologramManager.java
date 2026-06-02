package me.legit.ffacore.hologram;

import me.legit.ffacore.FFACore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HologramManager {

    private final FFACore plugin;

    private final Map<String,Hologram> holograms = new HashMap<>();

    public HologramManager(FFACore plugin) {
        this.plugin = plugin;
    }

    public void create(String id,Location location) {
        holograms.put(id,new Hologram(location));
    }

    public Hologram get(String id) {
        return holograms.get(id);
    }

    public void showAll(Player player) {
        for(Hologram hologram : holograms.values()) {
            hologram.show(player);
        }
    }

    public void remove(String id) {
        Hologram hologram = holograms.remove(id);
        if(hologram == null)
            return;
        for(Player player : Bukkit.getOnlinePlayers()) {
            hologram.destroy(player);
        }
    }
}