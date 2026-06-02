package me.legit.ffacore.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.legit.ffacore.FFACore;
import org.bson.Document;

public class MongoDataStore {

    private final FFACore plugin;

    public MongoDataStore(FFACore plugin) {
        this.plugin = plugin;
    }

    public void saveDocument(String collectionName, String key, Document document) {
        try {
            if (!plugin.getMongoDBHandler().isConnected()) {
                return;
            }

            MongoCollection<Document> collection = plugin.getMongoDBHandler()
                    .getCollection(collectionName);

            document.put("_key", key);

            collection.replaceOne(
                    Filters.eq("_key", key),
                    document,
                    new ReplaceOptions().upsert(true)
            );

        } catch (Exception e) {
            plugin.log("Error saving document to MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Document loadDocument(String collectionName, String key) {
        try {
            if (!plugin.getMongoDBHandler().isConnected()) {
                return null;
            }

            MongoCollection<Document> collection = plugin.getMongoDBHandler()
                    .getCollection(collectionName);

            return collection.find(Filters.eq("_key", key)).first();

        } catch (Exception e) {
            plugin.log("Error loading document from MongoDB: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void deleteDocument(String collectionName, String key) {
        try {
            if (!plugin.getMongoDBHandler().isConnected()) {
                return;
            }

            MongoCollection<Document> collection = plugin.getMongoDBHandler()
                    .getCollection(collectionName);

            collection.deleteOne(Filters.eq("_key", key));

        } catch (Exception e) {
            plugin.log("Error deleting document from MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
