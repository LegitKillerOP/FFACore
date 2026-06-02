package me.legit.ffacore.hologram;

import me.legit.ffacore.FFACore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HologramListener implements Listener {

    private final FFACore plugin;

    public HologramListener(
            FFACore plugin
    ) {

        this.plugin = plugin;

    }

    @EventHandler
    public void onJoin(
            PlayerJoinEvent e
    ) {

        plugin.getHologramManager()
                .showAll(
                        e.getPlayer()
                );

    }

}