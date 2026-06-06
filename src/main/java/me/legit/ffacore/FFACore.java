package me.legit.ffacore;

import me.legit.ffacore.arena.ArenaManager;
import me.legit.ffacore.combat.CombatManager;
import me.legit.ffacore.combat.CombatTask;
import me.legit.ffacore.commands.*;
import me.legit.ffacore.config.ConfigManager;
import me.legit.ffacore.database.MongoDBHandler;
import me.legit.ffacore.database.MongoDataStore;
import me.legit.ffacore.database.MongoSaveTask;
import me.legit.ffacore.gui.kitselector.KitSelectorGUI;
import me.legit.ffacore.gui.kitselector.KitSelectorListener;
import me.legit.ffacore.hologram.HologramListener;
import me.legit.ffacore.hologram.HologramManager;
import me.legit.ffacore.kits.KitCooldownManager;
import me.legit.ffacore.kits.KitManager;
import me.legit.ffacore.leaderboard.LeaderboardManager;
import me.legit.ffacore.leaderboard.LeaderboardTask;
import me.legit.ffacore.listeners.*;
import me.legit.ffacore.gui.GUIListener;
import me.legit.ffacore.player.PlayerDataManager;
import me.legit.ffacore.scoreboard.ScoreboardManager;
import me.legit.ffacore.scoreboard.ScoreboardTask;
import me.legit.ffacore.scoreboard.TabManager;
import me.legit.ffacore.spawn.SpawnManager;

import org.bukkit.plugin.java.JavaPlugin;

public class FFACore extends JavaPlugin {

    private static FFACore instance;

    private ConfigManager configManager;
    private MongoDBHandler mongoDBHandler;
    private MongoDataStore mongoDataStore;

    private PlayerDataManager playerDataManager;
    private SpawnManager spawnManager;
    private ArenaManager arenaManager;
    private KitManager kitManager;
    private CombatManager combatManager;

    private ScoreboardManager scoreboardManager;
    private TabManager tabManager;
    private LeaderboardManager leaderboardManager;
    private HologramManager hologramManager;

    private KitCooldownManager kitCooldownManager;
    private KitSelectorGUI kitSelectorGUI;

    private ServerProtectionListener serverProtectionListener;

    @Override
    public void onEnable() {
        instance = this;

        initCore();
        initManagers();
        initGUI();
        initListeners();
        initCommands();
        initTasks();

        log("FFACore enabled");
    }

    @Override
    public void onDisable() {
        shutdown();
        log("FFACore disabled");
    }

    // ------------------------
    // INIT SECTIONS
    // ------------------------

    private void initCore() {
        configManager = new ConfigManager(this);
        mongoDBHandler = new MongoDBHandler(this);
        mongoDBHandler.connect();
        mongoDataStore = new MongoDataStore(this);
    }

    private void initManagers() {
        playerDataManager = new PlayerDataManager(this);
        spawnManager = new SpawnManager(this);
        arenaManager = new ArenaManager(this);
        kitManager = new KitManager(this);
        combatManager = new CombatManager(15);

        scoreboardManager = new ScoreboardManager(this);
        tabManager = new TabManager(this);
        leaderboardManager = new LeaderboardManager(this);
        hologramManager = new HologramManager(this);

        kitCooldownManager = new KitCooldownManager();
    }

    private void initGUI() {
        kitSelectorGUI = new KitSelectorGUI(this);
    }

    private void initListeners() {
        serverProtectionListener = new ServerProtectionListener(this);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new HologramListener(this), this);
        getServer().getPluginManager().registerEvents(new KitSelectorListener(this), this);
        getServer().getPluginManager().registerEvents(serverProtectionListener, this);
    }

    private void initCommands() {
        getCommand("ffa").setExecutor(new FFACoreCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("arena").setExecutor(new ArenaCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));
        getCommand("lb").setExecutor(new LeaderboardCommand(this));
        getCommand("kitadmin").setExecutor(new KitAdminCommand(this));
    }

    private void initTasks() {
        new ScoreboardTask(this).runTaskTimer(this, 20L, 20L);
        new LeaderboardTask(this).runTaskTimer(this, 20L, 200L);
        new MongoSaveTask(this).runTaskTimer(this, 6000L, 6000L);
        new CombatTask(this).runTaskTimer(this, 0L, 20L);

        tabManager.startTabUpdater();
    }

    private void shutdown() {
        playerDataManager.saveAll();
        configManager.saveAll();
        mongoDBHandler.disconnect();
    }

    // ------------------------
    // GETTERS
    // ------------------------

    public static FFACore getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public MongoDBHandler getMongoDBHandler() { return mongoDBHandler; }
    public MongoDataStore getMongoDataStore() { return mongoDataStore; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public SpawnManager getSpawnManager() { return spawnManager; }
    public ArenaManager getArenaManager() { return arenaManager; }
    public KitManager getKitManager() { return kitManager; }
    public CombatManager getCombatManager() { return combatManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public LeaderboardManager getLeaderboardManager() { return leaderboardManager; }
    public TabManager getTabManager() { return tabManager; }
    public HologramManager getHologramManager() { return hologramManager; }
    public KitCooldownManager getKitCooldownManager() { return kitCooldownManager; }
    public KitSelectorGUI getKitSelectorGUI() { return kitSelectorGUI; }
    public ServerProtectionListener getServerProtectionListener() { return serverProtectionListener; }

    public void log(String msg) {
        getLogger().info(msg);
    }

    public String getPrefix() {
        return "§8[§x§0§0§A§A§F§F§lF§x§0§0§C§C§F§F§lF§x§0§0§E§E§F§F§lA§8] §7";
    }
}