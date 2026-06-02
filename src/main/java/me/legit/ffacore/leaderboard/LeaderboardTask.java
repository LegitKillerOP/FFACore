package me.legit.ffacore.leaderboard;

import me.legit.ffacore.FFACore;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaderboardTask extends BukkitRunnable {

    private final FFACore plugin;

    public LeaderboardTask(FFACore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getLeaderboardManager().updateAll();
    }
}