package me.legit.ffacore.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    private final Location location;
    private final List<HologramLine> lines = new ArrayList<>();

    public Hologram(Location location) {
        this.location = location;
    }

    public void addLine(String text) {

        double y = location.getY() - (lines.size() * 0.25);

        Location lineLoc = new Location(
                location.getWorld(),
                location.getX(),
                y,
                location.getZ()
        );

        lines.add(
                new HologramLine(
                        lineLoc,
                        text
                )
        );
    }

    public void show(Player player) {

        for (HologramLine line : lines) {
            line.spawn(player);
        }

    }

    public void destroy(Player player) {

        for (HologramLine line : lines) {
            line.destroy(player);
        }

    }

    public void update(Player player) {

        destroy(player);
        show(player);

    }

}