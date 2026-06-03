package me.legit.ffacore.kits;

import me.legit.ffacore.FFACore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitManager {

    private final FFACore plugin;
    private final Map<String, Kit> kits = new HashMap<>();

    public KitManager(FFACore plugin) {
        this.plugin = plugin;
        loadKits();
    }

    @SuppressWarnings("unchecked")
    public void loadKits() {
        kits.clear();
        ConfigurationSection section = plugin.getConfigManager().getKits().get().getConfigurationSection("kits");
        if (section == null) {
            return;
        }

        for (String name : section.getKeys(false)) {
            List<ItemStack> items = new ArrayList<>();
            List<?> loaded = section.getList(name + ".items");

            if (loaded != null) {
                for (Object obj : loaded) {
                    if (obj instanceof ItemStack) {
                        items.add((ItemStack) obj);
                    } else if (obj instanceof java.util.Map) {
                        try {
                            @SuppressWarnings("unchecked")
                            java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                            ItemStack is = ItemStack.deserialize(map);
                            items.add(is);
                        } catch (Exception ex) {
                            plugin.getLogger().warning("Failed to deserialize item for kit " + name + ": " + ex.getMessage());
                        }
                    }
                }
            }
            kits.put(name.toLowerCase(),new Kit(name,section.getString(name + ".permission"),items));
        }
    }

    public Kit getKit(String name) {
        if (name == null) return null;
        return kits.get(name.toLowerCase());
    }

    public Map<String, Kit> getKits() {
        return kits;
    }

    public boolean createKit(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String key = name.toLowerCase();
        if (kits.containsKey(key)) {
            return false;
        }
        kits.put(key, new Kit(name, null, new ArrayList<ItemStack>()));
        saveKits();
        return true;
    }

    public boolean deleteKit(String name) {
        if (name == null) {
            return false;
        }
        String key = name.toLowerCase();
        if (!kits.containsKey(key)) {
            return false;
        }
        kits.remove(key);
        saveKits();
        return true;
    }

    public void updateKitItems(String name, List<ItemStack> items) {
        Kit kit = getKit(name);
        if (kit == null) {
            return;
        }
        kit.setItems(new ArrayList<>(items));
        saveKits();
    }

    public void saveKits() {
        FileConfiguration config = plugin.getConfigManager().getKits().get();
        config.set("kits", null);
        for (Map.Entry<String, Kit> entry : kits.entrySet()) {
            String key = entry.getKey();
            Kit kit = entry.getValue();
            config.set("kits." + key + ".permission", kit.getPermission());
            // Save complete ItemStacks
            config.set("kits." + key + ".items", kit.getItems());
        }
        plugin.getConfigManager().getKits().save();
    }
}