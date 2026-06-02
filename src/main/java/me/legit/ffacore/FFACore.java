package me.legit.ffacore;

import me.legit.ffacore.arena.ArenaManager;
import me.legit.ffacore.combat.CombatManager;
import me.legit.ffacore.commands.*;
import me.legit.ffacore.config.ConfigManager;
import me.legit.ffacore.kits.KitManager;
import me.legit.ffacore.listeners.CombatListener;
import me.legit.ffacore.listeners.DeathListener;
import me.legit.ffacore.listeners.PlayerListener;
import me.legit.ffacore.listeners.SpawnListener;
import me.legit.ffacore.player.PlayerDataManager;
import me.legit.ffacore.scoreboard.ScoreboardManager;
import me.legit.ffacore.scoreboard.ScoreboardTask;
import me.legit.ffacore.spawn.SpawnManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FFACore extends JavaPlugin {

    private static FFACore instance;
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private SpawnManager spawnManager;
    private ArenaManager arenaManager;
    private KitManager kitManager;
    private CombatManager combatManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager(this);
        spawnManager = new SpawnManager(this);
        arenaManager = new ArenaManager(this);
        kitManager = new KitManager(this);
        combatManager = new CombatManager();
        scoreboardManager = new ScoreboardManager(this);

        new ScoreboardTask(this).runTaskTimer(this,20L,20L);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this),this);
        getServer().getPluginManager().registerEvents(new DeathListener(this),this);

        getCommand("ffa").setExecutor(new FFACoreCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("arena").setExecutor(new ArenaCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));

        getLogger().info("FFACore enabled");
    }

    @Override
    public void onDisable() {
        configManager.saveAll();
    }

    public static FFACore getInstance() {
        return instance;
    }

    public String getPrefix() {
        return "§6[§bFFACORE§6] §r";
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public ArenaManager getArenaManager(){
        return arenaManager;
    }

    public KitManager getKitManager(){
        return kitManager;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}