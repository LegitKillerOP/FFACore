package me.legit.ffacore.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigFile {

    private final JavaPlugin plugin;
    private final String fileName;

    private File file;
    private FileConfiguration config;

    public ConfigFile(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;

        create();
    }

    private void create() {

        file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {

            file.getParentFile().mkdirs();

            plugin.saveResource(fileName, false);

        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public FileConfiguration get() {
        return config;
    }

    public File getFile() {
        return file;
    }
}