package me.legit.ffacore.scoreboard;

import me.legit.ffacore.FFACore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardTask extends BukkitRunnable {

    private final FFACore plugin;

    public ScoreboardTask(FFACore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getScoreboardManager().update(player);
        }
    }
}