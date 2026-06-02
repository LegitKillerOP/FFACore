package me.legit.ffacore.player;

import me.legit.ffacore.FFACore;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final FFACore plugin;

    private final Map<UUID, PlayerData> cache = new HashMap<>();

    public PlayerDataManager(FFACore plugin) {
        this.plugin = plugin;
    }

    public void load(UUID uuid) {

        if (cache.containsKey(uuid)) {
            return;
        }

        FileConfiguration config =
                plugin.getConfigManager()
                        .getPlayerData()
                        .get();

        String path = "players." + uuid + ".";

        PlayerData data = new PlayerData(
                config.getInt(path + "kills"),
                config.getInt(path + "deaths"),
                config.getInt(path + "killstreak"),
                config.getInt(path + "higheststreak"),
                config.getInt(path + "coins"),
                config.getInt(path + "xp")
        );

        cache.put(uuid, data);
    }

    public void save(UUID uuid) {

        PlayerData data = cache.get(uuid);

        if (data == null) {
            return;
        }

        FileConfiguration config =
                plugin.getConfigManager()
                        .getPlayerData()
                        .get();

        String path = "players." + uuid + ".";

        config.set(path + "kills", data.getKills());
        config.set(path + "deaths", data.getDeaths());
        config.set(path + "killstreak", data.getKillStreak());
        config.set(path + "higheststreak", data.getHighestStreak());
        config.set(path + "coins", data.getCoins());
        config.set(path + "xp", data.getXp());

        plugin.getConfigManager()
                .getPlayerData()
                .save();
    }

    public void unload(UUID uuid) {
        save(uuid);
        cache.remove(uuid);
    }

    public PlayerData get(UUID uuid) {
        return cache.get(uuid);
    }

    public Map<UUID, PlayerData> getCache() {
        return cache;
    }

    public void saveAll() {

        for (UUID uuid : cache.keySet()) {
            save(uuid);
        }

    }
}