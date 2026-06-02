package me.legit.ffacore.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.legit.ffacore.FFACore;
import org.bson.Document;

public class MongoDBHandler {

    private final FFACore plugin;
    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoDBHandler(FFACore plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            String uri = plugin.getConfig().getString("mongodb.uri");

            if (uri == null || uri.isEmpty()) {
                plugin.log("MongoDB URI not configured in config.yml");
                return;
            }

            mongoClient = MongoClients.create(uri);
            String dbName = plugin.getConfig().getString("mongodb.database", "ffacore");
            database = mongoClient.getDatabase(dbName);

            // Test connection
            database.runCommand(new Document("ping", 1));
            plugin.log("Connected to MongoDB successfully!");

        } catch (Exception e) {
            plugin.log("Failed to connect to MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
            plugin.log("Disconnected from MongoDB");
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        if (database == null) {
            return null;
        }
        return database.getCollection(collectionName);
    }

    public boolean isConnected() {
        return database != null;
    }

}
