package me.legit.ffacore.player;

import me.legit.ffacore.FFACore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final FFACore plugin;

    private final Map<UUID, PlayerData> cache =
            new HashMap<>();

    public PlayerDataManager(FFACore plugin){
        this.plugin = plugin;
    }

    public void load(UUID uuid){

        if(cache.containsKey(uuid))
            return;

        PlayerData data = new PlayerData();

        cache.put(uuid,data);
    }

    public void unload(UUID uuid){

        save(uuid);

        cache.remove(uuid);
    }

    public PlayerData get(UUID uuid){
        return cache.get(uuid);
    }

    public void save(UUID uuid){

        PlayerData data = cache.get(uuid);

        if(data == null)
            return;

        plugin.getConfigManager()
                .getPlayerData()
                .get()
                .set("players."+uuid+".kills",
                        data.getKills());

        plugin.getConfigManager()
                .getPlayerData()
                .get()
                .set("players."+uuid+".deaths",
                        data.getDeaths());

        plugin.getConfigManager()
                .getPlayerData()
                .get()
                .set("players."+uuid+".coins",
                        data.getCoins());

        plugin.getConfigManager()
                .getPlayerData()
                .save();
    }

    public void saveAll(){

        cache.keySet().forEach(this::save);

    }
}