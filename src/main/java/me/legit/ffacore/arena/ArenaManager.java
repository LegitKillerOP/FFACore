package me.legit.ffacore.arena;

import me.legit.ffacore.FFACore;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ArenaManager {
    private final FFACore plugin;
    private final Map<String,Arena> arenas = new HashMap<>();

    public ArenaManager(FFACore plugin){
        this.plugin = plugin;
    }

    public void createArena(String name){
        Arena arena = new Arena(name);
        arenas.put(name.toLowerCase(), arena);
    }

    public Arena getArena(String name){
        return arenas.get(name.toLowerCase());
    }

    public void deleteArena(String name){
        arenas.remove(name.toLowerCase());
    }

    public Location getRandomSpawn(String arenaName){
        Arena arena = getArena(arenaName);
        if(arena == null)
            return null;
        if(arena.getSpawns().isEmpty())
            return null;
        Random random = new Random();
        return arena.getSpawns().get(random.nextInt(arena.getSpawns().size()));
    }
}