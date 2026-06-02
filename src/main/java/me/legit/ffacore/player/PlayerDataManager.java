package me.legit.ffacore.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.legit.ffacore.FFACore;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final FFACore plugin;
    private final Map<UUID, PlayerData> cache = new HashMap<>();
    private static final String COLLECTION_NAME = "players";

    public PlayerDataManager(FFACore plugin) {
        this.plugin = plugin;
    }

    public void load(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return;
        }

        PlayerData data = null;

        if (plugin.getMongoDBHandler().isConnected()) {
            data = loadFromMongo(uuid);
        }

        if (data == null) {
            data = new PlayerData();
        }

        cache.put(uuid, data);
    }

    public void save(UUID uuid) {
        PlayerData data = cache.get(uuid);

        if (data == null) {
            return;
        }

        if (plugin.getMongoDBHandler().isConnected()) {
            saveToMongo(uuid, data);
        }
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

    private PlayerData loadFromMongo(UUID uuid) {
        try {
            MongoCollection<Document> collection = plugin.getMongoDBHandler()
                    .getCollection(COLLECTION_NAME);

            Document doc = collection.find(
                    Filters.eq("uuid", uuid.toString())
            ).first();

            if (doc == null) {
                return null;
            }

            return new PlayerData(
                    doc.getInteger("kills", 0),
                    doc.getInteger("deaths", 0),
                    doc.getInteger("killstreak", 0),
                    doc.getInteger("higheststreak", 0),
                    doc.getInteger("coins", 0),
                    doc.getInteger("xp", 0)
            );

        } catch (Exception e) {
            plugin.log("Error loading player data from MongoDB: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void saveToMongo(UUID uuid, PlayerData data) {
        try {
            MongoCollection<Document> collection = plugin.getMongoDBHandler()
                    .getCollection(COLLECTION_NAME);

            Document doc = new Document()
                    .append("uuid", uuid.toString())
                    .append("kills", data.getKills())
                    .append("deaths", data.getDeaths())
                    .append("killstreak", data.getKillStreak())
                    .append("higheststreak", data.getHighestStreak())
                    .append("coins", data.getCoins())
                    .append("xp", data.getXp());

            collection.replaceOne(
                    Filters.eq("uuid", uuid.toString()),
                    doc,
                    new ReplaceOptions().upsert(true)
            );

        } catch (Exception e) {
            plugin.log("Error saving player data to MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

}