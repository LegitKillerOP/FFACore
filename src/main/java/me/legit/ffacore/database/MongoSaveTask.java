package me.legit.ffacore.database;

import me.legit.ffacore.FFACore;
import org.bukkit.scheduler.BukkitRunnable;

public class MongoSaveTask extends BukkitRunnable {

    private final FFACore plugin;

    public MongoSaveTask(FFACore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Save all player data to MongoDB
        plugin.getPlayerDataManager().saveAll();
    }

}
