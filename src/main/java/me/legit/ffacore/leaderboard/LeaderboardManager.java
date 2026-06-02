package me.legit.ffacore.leaderboard;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.hologram.Hologram;
import me.legit.ffacore.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class LeaderboardManager {

    private final FFACore plugin;
    private final Map<String, LeaderboardDefinition> leaderboards = new HashMap<>();

    public LeaderboardManager(FFACore plugin) {
        this.plugin = plugin;
        loadLeaderboards();
    }

    public void loadLeaderboards() {
        leaderboards.clear();

        FileConfiguration config = plugin.getConfigManager()
                .getLeaderboards()
                .get();

        if (!config.isConfigurationSection("leaderboards")) {
            return;
        }

        for (String id : config.getConfigurationSection("leaderboards").getKeys(false)) {
            String path = "leaderboards." + id + ".";
            LeaderboardType type = LeaderboardType.fromString(config.getString(path + "type"));
            String world = config.getString(path + "world");
            double x = config.getDouble(path + "x");
            double y = config.getDouble(path + "y");
            double z = config.getDouble(path + "z");

            if (type == null || world == null) {
                continue;
            }

            if (Bukkit.getWorld(world) == null) {
                continue;
            }

            Location location = new Location(Bukkit.getWorld(world), x, y, z);
            leaderboards.put(id.toLowerCase(), new LeaderboardDefinition(id.toLowerCase(), type, location));
        }
    }

    public void saveLeaderboards() {
        FileConfiguration config = plugin.getConfigManager()
                .getLeaderboards()
                .get();

        config.set("leaderboards", null);

        for (LeaderboardDefinition definition : leaderboards.values()) {
            String path = "leaderboards." + definition.getId() + ".";
            config.set(path + "type", definition.getType().name().toLowerCase());
            config.set(path + "world", definition.getLocation().getWorld().getName());
            config.set(path + "x", definition.getLocation().getX());
            config.set(path + "y", definition.getLocation().getY());
            config.set(path + "z", definition.getLocation().getZ());
        }

        plugin.getConfigManager()
                .getLeaderboards()
                .save();
    }

    public boolean createLeaderboard(String id, LeaderboardType type, Location location) {
        id = id.toLowerCase();

        if (leaderboards.containsKey(id)) {
            return false;
        }

        leaderboards.put(id, new LeaderboardDefinition(id, type, location));
        saveLeaderboards();
        updateAll();
        return true;
    }

    public boolean removeLeaderboard(String id) {
        id = id.toLowerCase();
        LeaderboardDefinition definition = leaderboards.remove(id);

        if (definition == null) {
            return false;
        }

        definition.destroyAll();
        saveLeaderboards();
        return true;
    }

    public boolean moveLeaderboard(String id, Location location) {
        id = id.toLowerCase();
        LeaderboardDefinition definition = leaderboards.get(id);

        if (definition == null) {
            return false;
        }

        definition.destroyAll();
        definition.setLocation(location);
        saveLeaderboards();
        updateAll();
        return true;
    }

    public List<String> listLeaderboards() {
        List<String> output = new ArrayList<>();

        for (LeaderboardDefinition definition : leaderboards.values()) {
            output.add(definition.getId()
                    + " (" + definition.getType().getDisplayName() + ") at "
                    + definition.getLocation().getWorld().getName() + " "
                    + definition.getLocation().getBlockX() + " "
                    + definition.getLocation().getBlockY() + " "
                    + definition.getLocation().getBlockZ());
        }

        return output;
    }

    public void reloadLeaderboards() {
        for (LeaderboardDefinition definition : leaderboards.values()) {
            definition.destroyAll();
        }

        loadLeaderboards();
        updateAll();
    }

    public void updateAll() {
        for (LeaderboardDefinition definition : leaderboards.values()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateForPlayer(definition, player);
            }
        }
    }

    public void showAllTo(Player player) {
        for (LeaderboardDefinition definition : leaderboards.values()) {
            updateForPlayer(definition, player);
        }
    }

    public void removePlayer(Player player) {
        for (LeaderboardDefinition definition : leaderboards.values()) {
            definition.removePlayer(player);
        }
    }

    private void updateForPlayer(LeaderboardDefinition definition, Player player) {
        definition.destroy(player);

        Hologram hologram = new Hologram(definition.getLocation());
        List<String> lines = buildLines(definition, player);

        for (String line : lines) {
            hologram.addLine(line);
        }

        definition.setHologram(player.getUniqueId(), hologram);
        hologram.show(player);
    }

    private List<String> buildLines(LeaderboardDefinition definition, Player player) {
        List<Map.Entry<UUID, PlayerData>> sorted = getSortedEntries(definition.getType());
        List<String> lines = new ArrayList<>();

        lines.add("§6--- §b" + definition.getType().getDisplayName() + " Leaderboard §6---");

        for (int i = 0; i < 10; i++) {
            if (i >= sorted.size()) {
                lines.add("§7" + (i + 1) + ". §8- §7Nobody");
                continue;
            }

            Map.Entry<UUID, PlayerData> entry = sorted.get(i);
            PlayerData data = entry.getValue();
            String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();

            if (name == null) {
                name = "Unknown";
            }

            lines.add("§7" + (i + 1) + ". §f" + name + " §8- §e" + definition.getType().formatScore(data));
        }

        lines.add(" ");
        int rank = getPlayerRank(player.getUniqueId(), definition.getType());
        PlayerData playerData = plugin.getPlayerDataManager().get(player.getUniqueId());
        String score = playerData == null ? "0" : definition.getType().formatScore(playerData);

        lines.add("§aYour Rank: §f#" + (rank <= 0 ? "N/A" : rank) + " §8| §b" + player.getName() + " §8- §e" + score);

        return lines;
    }

    private List<Map.Entry<UUID, PlayerData>> getSortedEntries(LeaderboardType type) {
        return plugin.getPlayerDataManager()
                .getCache()
                .entrySet()
                .stream()
                .sorted(Comparator.comparingDouble((Map.Entry<UUID, PlayerData> entry) -> type.getScore(entry.getValue()))
                        .reversed())
                .collect(Collectors.toList());
    }

    public int getPlayerRank(UUID uuid, LeaderboardType type) {
        List<Map.Entry<UUID, PlayerData>> sorted = getSortedEntries(type);

        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getKey().equals(uuid)) {
                return i + 1;
            }
        }

        return -1;
    }

}
