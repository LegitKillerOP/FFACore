package me.legit.ffacore.config;

import me.legit.ffacore.FFACore;

public class ConfigManager {

    private final FFACore plugin;

    private ConfigFile kits;
    private ConfigFile arenas;
    private ConfigFile messages;
    private ConfigFile spawn;
    private ConfigFile holograms;
    private ConfigFile scoreboard;
    private ConfigFile leaderboards;
    private ConfigFile database;
    private ConfigFile combat;
    private ConfigFile playerData;

    public ConfigManager(FFACore plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {

        plugin.saveDefaultConfig();

        kits = new ConfigFile(plugin,"kits.yml");
        arenas = new ConfigFile(plugin,"arenas.yml");
        messages = new ConfigFile(plugin,"messages.yml");
        spawn = new ConfigFile(plugin,"spawn.yml");
        holograms = new ConfigFile(plugin,"holograms.yml");
        scoreboard = new ConfigFile(plugin,"scoreboard.yml");
        leaderboards = new ConfigFile(plugin,"leaderboards.yml");
        database = new ConfigFile(plugin,"database.yml");
        combat = new ConfigFile(plugin,"combat.yml");
        playerData = new ConfigFile(plugin, "playerdata.yml");

    }

    public void reloadAll(){
        plugin.reloadConfig();
        kits.reload();
        arenas.reload();
        messages.reload();
        spawn.reload();
        holograms.reload();
        scoreboard.reload();
        leaderboards.reload();
        database.reload();
        combat.reload();
        playerData.reload();
    }

    public void saveAll(){
        kits.save();
        arenas.save();
        messages.save();
        spawn.save();
        holograms.save();
        scoreboard.save();
        leaderboards.save();
        database.save();
        combat.save();
        playerData.save();
    }

    public ConfigFile getKits() {
        return kits;
    }

    public ConfigFile getArenas() {
        return arenas;
    }

    public ConfigFile getMessages() {
        return messages;
    }

    public ConfigFile getSpawn() {
        return spawn;
    }

    public ConfigFile getHolograms() {
        return holograms;
    }

    public ConfigFile getScoreboard() {
        return scoreboard;
    }

    public ConfigFile getLeaderboards() {
        return leaderboards;
    }

    public ConfigFile getDatabase() {
        return database;
    }

    public ConfigFile getCombat() {
        return combat;
    }

    public ConfigFile getPlayerData() {
        return playerData;
    }
}