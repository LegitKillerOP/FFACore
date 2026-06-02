package me.legit.ffacore.arena;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Arena {

    private final String name;

    private boolean enabled;

    private Location min;
    private Location max;

    private final List<Location> spawns = new ArrayList<>();

    public Arena(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Location getMin() {
        return min;
    }

    public void setMin(Location min) {
        this.min = min;
    }

    public Location getMax() {
        return max;
    }

    public void setMax(Location max) {
        this.max = max;
    }

    public List<Location> getSpawns() {
        return spawns;
    }

}